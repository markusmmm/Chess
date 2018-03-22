package pieces;

import management.Board;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.*;

public class King extends ChessPiece {
    private final int value = 2;
    private Set<Vector2> moves = new HashSet<>(Arrays.asList(
        new Vector2(-1, -1), new Vector2( 0, -1), new Vector2( 1, -1),
        new Vector2(-1,  0),                           new Vector2( 1,  0),
        new Vector2(-1,  1), new Vector2( 0,  1), new Vector2( 1,  1)
    ));

    /**
     * @param position
     */
    public King(Vector2 position, Alliance alliance, Board board) {
        super(position, alliance, board, false, Piece.KING, 2);
    }

    public King clonePiece() {
        return new King(position, alliance, board);
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public boolean legalMove(Vector2 destination) {
        //IMPORTANT! King can NOT call super.legalMove, as the king demands a custom alliance check (when performing castling),
        //and does not need to perform the inCheck-call that occurs from within super

        if(!(board.insideBoard(position) && board.insideBoard(destination))) return false;

        IChessPiece endPiece = board.getPiece(destination);
        if(endPiece != null && endPiece.alliance().equals(alliance)) return false; // Temporary fix, until castling has been integrated

        // Ensures that the king can't be moved into check
        // Ignored if king is not on live board
        if(board.isLive() && resolvesCheck(position, destination)) return false;

        return (
            (inDiagonals(destination) || inStraights(destination)) &&
            position.distance(destination) == 1 &&
            freePath(destination)
        );
    }

    public Set<Vector2> getPossibleDestinations(String caller) {
        logActionPossibleDestinations(caller);

        Set<Vector2> possibleMoves = new HashSet<>();

        for(Vector2 move : moves) {
            Vector2 endPos = position.add(move);

            if(legalMove(endPos))
                possibleMoves.add(endPos);
        }

        return possibleMoves;
    }

    public boolean inCheck() {
        if(!board.getActivePlayer().equals(alliance)) return false;

        // NEVER USE 'board.getUsablePieces' WITHIN THIS METHOD
        HashMap<Vector2, IChessPiece> hostilePieces = board.getPieces(otherAlliance());

        for(Vector2 key : hostilePieces.keySet()) {
            IChessPiece piece = hostilePieces.get(key);

            // Custom handling for king, as the default implementation causes an infinite circular call
            if(piece instanceof King) {
                King hostileKing = (King) piece;
                if(position.distance(hostileKing.position()) == 1)
                    return true;
            }
            else if(piece instanceof Pawn) {
                Pawn hostilePawn = (Pawn) piece;
                if(hostilePawn.getPossibleAttacks().contains(position))
                    return true;
            }
            else {
                if (piece.getPossibleDestinations(toString()).contains(position))
                    return true;
            }
        }

        return false;
    }

    public boolean resolvesCheck(Vector2 start, Vector2 end) {
        System.out.println("(resolvesCheck): Board is live: " + board.isLive());

        if(!board.isLive()) return true;    // IMPORTANT! DO NOT DELETE

        if(!inCheck()) return true;

        System.out.println("Initiating board-simulation");

        Board newBoard = board.clone();
        newBoard.movePiece(start, end);

        return newBoard.getKing(alliance).inCheck();
    }

    public boolean checkmate() {
        return /* inCheck() && */board.getUsablePieces(alliance).size() == 0;
    }

    /**
     * @param rook
     */
    public void castling(Vector2 rook) {
        if (hasMoved()) {
            System.out.println("The king has been moved");
            return;
        }
        if (rook == null) {
            System.out.println("There is no rook to castle!");
            return;
        }

        boolean queenSide = rook.getY() < 4;
        boolean kingSide = rook.getY() > 4;

        IChessPiece piece = board.getPiece(rook);

        if (piece.hasMoved()) {
            System.out.println("The rook has already been moved");
            return;
        }
        if (!freePath(rook)) {
            System.out.println("The path is not clear to perform castling");
            return;
        }

        if (rook.getX() < 4) {
            if (queenSide) {
                Vector2 rooksEnd = new Vector2(0, 3);
                Vector2 kingsEnd = new Vector2(0, 2);
                board.movePiece(position, kingsEnd);
                board.movePiece(rook, rooksEnd);
            }
            if (kingSide) {
                Vector2 rooksEnd = new Vector2(0, 2);
                Vector2 kingsEnd = new Vector2(0, 6);
                board.movePiece(position, kingsEnd);
                board.movePiece(rook, rooksEnd);
            }
        } else {
            if (queenSide) {
                Vector2 rooksEnd = new Vector2(7, 3);
                Vector2 kingsEnd = new Vector2(7, 2);
                board.movePiece(position, kingsEnd);
                board.movePiece(rook, rooksEnd);
            }
            if (kingSide) {
                Vector2 rooksEnd = new Vector2(7, 2);
                Vector2 kingsEnd = new Vector2(7, 6);
                board.movePiece(position, kingsEnd);
                board.movePiece(rook, rooksEnd);
            }
        }


    }

}