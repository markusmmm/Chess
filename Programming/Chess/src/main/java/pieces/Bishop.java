package pieces;


import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;


public class Bishop extends ChessPiece {
	public Bishop(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.DIAGONAL, MoveType.LINE, board, false, Piece.BISHOP, 3, hasMoved);
	}
	public Bishop(Bishop other) {
		super(other);
	}

	@Override
	public ChessPiece clonePiece() {
		return new Bishop(this);
	}

	/**
	 *
	 * @return a list of all possible moves from this position
	 */

	/**
	 *
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		if(!super.legalMove(destination)) return false;

		return (
			inDiagonals(destination) &&
			freePath(destination)
		);
	}
}