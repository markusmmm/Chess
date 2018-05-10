package pieces;

import management.AbstractBoard;
import resources.*;

import java.util.*;

public class King extends ChessPiece {
    public King(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
        super(position, alliance,
                vectorTools.addAll(Vector2.UNIT, new Vector2(2,0), new Vector2(-2,0)),
                MoveType.STEP, board, false, Piece.KING, hasMoved);
    }
    public King(King other) {
        super(other);
    }

    @Override
    public IChessPiece clonePiece() {
        return new King(this);
    }

    @Override
    public boolean isLegalMove(Vector2 destination) {
        boolean isCastling = isCastling(destination);
        if(!super.isLegalMove(destination)) return false;
        if(isCastling) return getCastlingRook(destination) != null;
        return true;
    }

    @Override
    public boolean move(Vector2 destination) {
        if(isCastling(destination) && isLegalMove(destination)) {
            if(!performCastling(destination)) return false;
            performMove(destination);
            return true;
        }

        return super.move(destination);
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

        HashMap<Vector2, IChessPiece> hostilePieces = board.getPieces(otherAlliance());
        for(IChessPiece hostile : hostilePieces.values()) {
            if(hostile.getLegalAttacks().contains(destination)) {
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
     * @param start Start position of the attempted move
     * @param end End position of the attempted move
     * @return Whether or not the move successfully protects the king
     */
    public boolean resolvesCheck(Vector2 start, Vector2 end) {
        if(start.equals(position()))
            return !movesIntoCheck(end);

        return !board.simulateCheck(start, end, alliance);
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
     * @param rookPos Position of rook to attempt castling with
     * @return If castling can be performed
     */
    private boolean canCastleWith(Vector2 rookPos) {
        if(hasMoved() || inCheck()) return false;

        IChessPiece rook = board.getPiece(rookPos);
        if(!(rook instanceof Rook) || rook.hasMoved()) return false;
        Vector2 kingPos = position();

        int x = kingPos.getX();
        int y = kingPos.getY();

        int castlingDir = Tools.sign(rookPos.getX() - x);
        if(!rook.freePath(kingPos)) return false;

        // King can't pass through or end up at a checked destination
        return !inCheck(new Vector2(x + castlingDir,y)) && !inCheck(new Vector2(x + castlingDir*2, y));
    }

    /**
     * @param destination King's position after castling (if successful)
     * @return Position of rook to castle with (if any)
     */
    private Vector2 getCastlingRook(Vector2 destination) {
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
        Vector2 rookPos = getCastlingRook(destination);
        if(rookPos == null) return false; // No valid rook found. Castling failed

        int diff = rookPos.getX() - position().getX();
        Vector2 endRookPos = destination.add(diff > 0 ? Vector2.W : Vector2.E);

        // Move rook that was castled (The king is temporarily suspended, so that the rook can move "through" the king's square
        board.suspendPieces(position());
        board.performAdditionalAction(rookPos, endRookPos);
        board.releasePieces(position());
        return true;
    }
}
