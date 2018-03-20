package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

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


    public boolean legalMove(Vector2 move) {
        return (
                positiveCoordinates(move) &&
                        inDiagonals(move) &&
                        inStraights(move) &&
                        position.distance(move) == 1 &&
                        freePath(move)
        );
    }

    public List<Vector2> getPossibleMoves() {

        List<Vector2> possibleMoves = new ArrayList<>();

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


    private boolean positiveCoordinates(Vector2 pos) {
        return 0 <= pos.getX() && 0 <= pos.getY();
    }

}