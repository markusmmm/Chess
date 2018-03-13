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
	 * 
	 * @param move
	 * @param board
	 */
	public boolean legalMove(Vector2 move, Board board) {
		return (
		        positiveCoordinates(move) &&
                        inDiagonals(move) &&
                        freePath(move, board)
        );
	}

    private boolean freePath(Vector2 destination, Board board) {
	    Vector2 path = null;

	    path = path.stepToward(destination);
	    destination.getX();
    }


    public boolean canJump() {
		return canJump;
	}

	/**
	 * 
	 * @param position
	 */

	private boolean inDiagonals(Vector2 newPos) {
		return Math.abs(this.position.getX() - newPos.getX()) == Math.abs(this.position.getY() - newPos.getY());
	}
	private boolean positiveCoordinates(Vector2 pos) {
	    return 0 <= pos.getX() && 0 <= pos.getY();
    }

}