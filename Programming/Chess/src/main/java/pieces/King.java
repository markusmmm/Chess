package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        //if(!(board.insideBoard(position) && board.insideBoard(destination))) return false;

        IChessPiece endPiece = board.getPiece(destination);
        if(endPiece != null && endPiece.alliance().equals(alliance)) return false;

        return (
            (inDiagonals(destination) || inStraights(destination)) &&
            position.distance(destination) == 1 &&
            freePath(destination)
        );

    }

    public Set<Vector2> getPossibleDestinations() {

        Set<Vector2> possibleMoves = new HashSet<>();

        int row = position.getX();
        int col = position.getY();


        if (legalMove(new Vector2(row - 1, col - 1))) {
            possibleMoves.add(new Vector2(row - 1, col - 1));
        }
        if (legalMove(new Vector2(row, col - 1))) {
            possibleMoves.add(new Vector2(row, col - 1));
        }
        if (legalMove(new Vector2(row + 1, col - 1))) {
            possibleMoves.add(new Vector2(row + 1, col - 1));
        }
        if (legalMove(new Vector2(row - 1, col))) {
            possibleMoves.add(new Vector2(row - 1, col));
        }
        if (legalMove(new Vector2(row + 1, col))) {
            possibleMoves.add(new Vector2(row + 1, col));
        }
        if (legalMove(new Vector2(row - 1, col + 1))) {
            possibleMoves.add(new Vector2(row - 1, col + 1));
        }
        if (legalMove(new Vector2(row + 1, col - 1))) {
            possibleMoves.add(new Vector2(row, col + 1));
        }
        if (legalMove(new Vector2(row + 1, col - 1))) {
            possibleMoves.add(new Vector2(row + 1, col + 1));
        }
        return possibleMoves;

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