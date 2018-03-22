package pieces;

import management.Board;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.*;

public class King extends ChessPiece {

    /**
     * @param position
     */
    public King(Vector2 position, Alliance alliance, Board board) {
        super(position, alliance, board, false, Piece.KING);
    }

    public King clonePiece() {
        return new King(position, alliance, board);
    }

    @Override
    public boolean legalMove(Vector2 destination) {
        if(!super.legalMove(destination)) return false;

        if(!(board.insideBoard(position) && board.insideBoard(destination))) return false;

        IChessPiece endPiece = board.getPiece(destination);
        if(endPiece != null && endPiece.alliance().equals(alliance)) return false;

        if(inCheck(destination)) return false; // Ensures that the king can't be moved into check

        return (
            (inDiagonals(destination) || inStraights(destination)) &&
            position.distance(destination) == 1 &&
            freePath(destination)
        );
    }

    public Set<Vector2> getPossibleDestinations(String caller) {
        logActionPossibleDestinations(caller);

        Set<Vector2> possibleMoves = new HashSet<>();

        int x = position.getX();
        int y = position.getY();


        if (legalMove(new Vector2(x - 1, y - 1))) {
            possibleMoves.add(new Vector2(x - 1, y - 1));
        }
        if (legalMove(new Vector2(x, y - 1))) {
            possibleMoves.add(new Vector2(x, y - 1));
        }
        if (legalMove(new Vector2(x + 1, y - 1))) {
            possibleMoves.add(new Vector2(x + 1, y - 1));
        }
        if (legalMove(new Vector2(x - 1, y))) {
            possibleMoves.add(new Vector2(x - 1, y));
        }
        if (legalMove(new Vector2(x + 1, y))) {
            possibleMoves.add(new Vector2(x + 1, y));
        }
        if (legalMove(new Vector2(x - 1, y + 1))) {
            possibleMoves.add(new Vector2(x - 1, y + 1));
        }
        if (legalMove(new Vector2(x + 1, y - 1))) {
            possibleMoves.add(new Vector2(x, y + 1));
        }
        if (legalMove(new Vector2(x + 1, y - 1))) {
            possibleMoves.add(new Vector2(x + 1, y + 1));
        }

        return possibleMoves;
    }

    public boolean inCheck(Vector2 destination) {
        Alliance otherAlliance = alliance == Alliance.BLACK ? Alliance.WHITE : Alliance.BLACK;
        HashMap<Vector2, IChessPiece> hostilePieces = board.getPieces(otherAlliance);

        for(Vector2 key : hostilePieces.keySet()) {
            IChessPiece piece = hostilePieces.get(key);

            if(piece instanceof King) {
                King hostileKing = (King) piece;
                if(destination.distance(hostileKing.position()) == 1)
                    return true;

                continue;
            }

            if (piece.getPossibleDestinations(toString()).contains(destination))
                return true;
        }

        return false;
    }
    public boolean inCheck() {
        return inCheck(position);
    }

    public boolean checkmate() {
        //TODO Check if any pieces can shield the king
        throw new UnsupportedOperationException("Check for shielding pieces not yet implemented");
        //return inCheck() && getPossibleDestinations().size() == 0;
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