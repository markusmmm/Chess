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
	public Knight (Vector2 position, Alliance alliance){
		super(position, alliance);
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
						freePath(move, board)
		);
	}
	
	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}
	
	public boolean canJump() {
		return canJump;
	}

	
	

}