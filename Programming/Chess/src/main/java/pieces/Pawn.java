package pieces;

import resources.*;
import management.*;

public class Pawn extends ChessPiece {

    private Vector2[] attacks = new Vector2[] {};

	public Piece piece() {
		return Piece.PAWN;
	}

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public boolean legalMove(Vector2 move, Board board) {
		// TODO - implement Pawn.legalMove
		throw new UnsupportedOperationException();
	}

	public boolean canJump() {
		// TODO - implement Pawn.canJump
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param position
	 * @param alliance
	 */
	public Pawn(Vector2 position, Alliance alliance) {
		super(position, alliance);
	}

}