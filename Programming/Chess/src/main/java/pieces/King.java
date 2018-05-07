package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.*;

public class King extends ChessPiece {
    public King(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
        super(position, alliance,
                vectorTools.addAll(Vector2.UNIT, new Vector2(2,0), new Vector2(-2,0)),
                Vector2.UNIT,
                ActionType.STEP, board, false, Piece.KING, 2, hasMoved);
    }
    private King(Vector2 position, King other) { super(position, other); }

    @Override
    public AbstractChessPiece clonePiece(Vector2 position) {
        return new King(position,this);
    }

    @Override
    public boolean legalAction(Vector2 destination) {
        boolean isCastling = isCastling(destination);
        if(!super.legalAction(destination)) return false;
        if(isCastling) return canCastle(destination) != null;
        return true;
    }

    @Override
    public boolean move(Vector2 destination, Board board) {
        Vector2 pos = position();
        boolean isCastling = isCastling(destination);
        if(isCastling && canCastle(destination) == null) return false; // Prevent position update if castling failed
        if(!super.move(destination, board)) return false; // Evaluate and perform king move
        if(isCastling) return performCastling(destination); // Perform castling, if it was attempted
        return true;
    }

    /**
     * Determines if a given position is threatened by a hostile piece
     * @param destination Position to evaluate
     * @return inCheck
     */
    public boolean inCheck(Vector2 destination) {
        if(!board.getActivePlayer().equals(alliance))
            return false;

        Vector2 position = position();

        boolean checked = false;
        board.suspendPieces(position);

        HashMap<Vector2, AbstractChessPiece> hostilePieces = board.getPieces(otherAlliance());
        for(IChessPiece hostile : hostilePieces.values()) {
            if(hostile.getPossibleAttacks().contains(destination)) {
                checked = true;
                break;
            }
        }

        board.releasePieces(position);
        return checked;
    }
    public boolean inCheck() {
        return inCheck(position());
    }

    /**
     * Determines if a move will check the king
     * @param end End position of the king's attempted move
     * @return If the move will set check
     */
    private boolean movesIntoCheck(Vector2 end) {
        Vector2 position = position();

        board.suspendPieces(position);
        board.suspendPieces(end);

        boolean setsCheck = inCheck(end);

        board.releasePieces(position);
        board.releasePieces(end);

        return setsCheck;
    }

    /**
     * Determines if the attempted move puts the king in check
     * @param start Start position of the attempted move
     * @param end End position of the attempted move
     * @return Whether or not the move successfully protects the king
     */
    public boolean resolvesCheck(Vector2 start, Vector2 end) {
        if(start.equals(position()))
            return !movesIntoCheck(end);

        AbstractChessPiece other = board.getPiece(start);
        boolean doPrint = start.getY() == 7 && other != null && other.piece() == Piece.QUEEN;

        if(doPrint) Console.printNotice(start + " -> " + end + " resolves check?");

        Board tempBoard = board.clone();

        board.forceMovePiece(start, end);
        boolean checked = inCheck();

        if(doPrint) Console.printNotice(Console.indent("Board state:\n" + board));
        if(doPrint) Console.printCustom(Console.indent("RESULT: " + !checked), !checked ? ANSI.GREEN : ANSI.RED);

        board.sync(tempBoard);

        return !checked;
    }

    /**
     * Determines if the king is in check, and no legal moves can resolve it
     * @return checkmate
     */
    public boolean checkmate() {
        return inCheck() && board.getUsablePieces(alliance).size() == 0;
    }

    /**
     * Determines if the king is not in check, but all possible moves will set check
     * @return stalemate
     */
    public boolean stalemate() {
        return !inCheck() && board.getUsablePieces(alliance).size() == 0;
    }

    /**
     * Checks if the king can castle with the given rook
     * @param rookPos Position of rook to attempt canCastle with
     * @return If canCastle can be performed
     */
    private boolean canCastleWith(Vector2 rookPos) {
        //Console.printNotice("Checking castling between king " + position() + " and " + rookPos);
        if(hasMoved() || inCheck()) return false;

        IChessPiece rook = board.getPiece(rookPos);
        if(!(rook instanceof Rook) || rook.hasMoved()) return false;
        Console.printNotice("Is rook");
        Vector2 kingPos = position();

        int x = kingPos.getX();
        int y = kingPos.getY();

        int diff = rookPos.getX() - x;
        if(!rook.freePath(kingPos)) return false;
        Console.printNotice("Free path");
        // King can't pass through or end up at a checked destination
        return !inCheck(new Vector2(x + diff,y)) && !inCheck(new Vector2(x + diff*2, y));
    }

    /**
     * @param destination End position of canCastle
     * @return Position of rook to castle with (if any)
     */
    private Vector2 canCastle(Vector2 destination) {
        HashSet<Vector2> rookPositions = new HashSet<>(Arrays.asList(destination.add(Vector2.E), destination.add(Vector2.W.mult(2))));

        for(Vector2 rookPos : rookPositions)
            if(canCastleWith(rookPos))
                return rookPos;
        return null;
    }

    private boolean isCastling(Vector2 destination) {
        return Math.abs(destination.sub(position()).getX()) > 1;
    }

    private boolean performCastling(Vector2 destination) {
        Vector2 rookPos = canCastle(destination);
        if(rookPos == null) return false;

        Console.printNotice("Performing canCastle: " + position() + " -> " + destination);

        int diff = rookPos.getX() - position().getX();
        Vector2 endRookPos = destination.add(diff > 0 ? Vector2.W : Vector2.E);

        board.forceMovePiece(rookPos, endRookPos);
        board.addDrawPos(rookPos, endRookPos);
        return true;
    }
}