package pieces;

import resources.*;
import management.*;

import java.util.List;

public class Pawn extends ChessPiece {

    private Vector2[] attacks = new Vector2[] {};

	public Piece piece() {
		return Piece.PAWN;
	}

	/**
	 * 
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		// TODO - implement Pawn.legalMove
		throw new UnsupportedOperationException();
	}

	public List<Vector2> getPossibleMoves() {
		//TODO Pawn.getPossibleMoves
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
	public Pawn(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board);
	}

	public Pawn clone() {
		return new Pawn(position, alliance, board);
	}
}