package pieces;

import resources.*;
import management.*;

public class Queen extends ChessPiece {
	private boolean canJump = false;

	public Queen(Vector2 position) {
		super();
	}
	public Piece piece() {
		// TODO - implement Queen.piece
		throw new UnsupportedOperationException();
	}
    /**
     * checks one by one position toward destination, returns false if runs into piece
     */
    private boolean freePath(Vector2 destination, Board board) {
        Vector2 path = null;
        int between = this.position.distance(destination) - 1;

        for (int step = 0; step < between; step++) {
            path = path.stepToward(destination);
            if (board.getPiece(path) != null) {
                return false;
            }
        }
        return true;
    }

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public boolean legalMove(Vector2 move, Board board) {
		return (
		        positiveCoordinates(move) &&
                        inDiagonals(move) &&
                        inStraights(move) &&
                        freePath(move, board)
        );
	}

    /**
     * @return if piece is placed in the lines:
     * up, down, left, right
     */
    private boolean inStraights(Vector2 move) {
        return (
                ( this.position.getX() == (move.getX()) && this.position.getY() != (move.getY()) )
                ||
                ( this.position.getX() != (move.getX()) && this.position.getY() == (move.getY()) )
        );
    }

    public boolean canJump() {
		return canJump;
	}

	private boolean inDiagonals(Vector2 newPos) {
		return Math.abs(this.position.getX() - newPos.getX()) == Math.abs(this.position.getY() - newPos.getY());
	}
	private boolean positiveCoordinates(Vector2 pos) {
	    return 0 <= pos.getX() && 0 <= pos.getY();
    }

}