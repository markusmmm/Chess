package pieces;

import resources.*;

import java.util.ArrayList;
import java.util.List;

import management.*;

public class Knight  extends ChessPiece {

	private final Piece piece;
	private final boolean canJump;
	private Vector2 position;
	
	/**
	 * 
	 * @param position
	 */
	public Knight (Vector2 position){
		super();
		canJump = false;
		piece = Piece.KNIGHT;
		this.position = position;

	}
	
	public Piece piece() {
		return piece;
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

	/**
     * @return if piece is placed in the lines:
     * up, down, left, right
     *
     * logic is: if only x or y change, the piece move in a straight path
     */
	 private boolean inStraights(Vector2 move) {
	        return (
	                ( this.position.getX() == move.getX() && this.position.getY() != move.getY() )
	                ||
	                ( this.position.getX() != move.getX() && this.position.getY() == move.getY() )
	        );   
	 }
	 
	private boolean inDiagonals(Vector2 newPos) {
		return Math.abs(this.position.getX() - newPos.getX()) == Math.abs(this.position.getY() - newPos.getY());
	}
	
	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}
	
	/**
     * checks one by one position from this position
     * toward destination, returns false if runs into another piece
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
	
	public boolean canJump() {
		return canJump;
	}

	
	

}