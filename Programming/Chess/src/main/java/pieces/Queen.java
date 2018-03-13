package pieces;

import resources.*;
import management.*;

public class Queen extends ChessPiece {
	private boolean canJump = false;

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
		    checkDiagonals(move) &&
            positiveCoordinates(move)
        );
	}



	public boolean canJump() {
		return canJump;
	}

	/**
	 * 
	 * @param position
	 */
	public Queen(Vector2 position) {
		// TODO - implement Queen.Queen
		throw new UnsupportedOperationException();
	}
	private boolean checkDiagonals(Vector2 newPos) {
		return Math.abs(this.position.getX() - newPos.getX()) == Math.abs(this.position.getY() - newPos.getY());
	}
	private boolean positiveCoordinates(Vector2 pos) {
	    return 0 <= pos.getX() && 0 <= pos.getY();
    }

}