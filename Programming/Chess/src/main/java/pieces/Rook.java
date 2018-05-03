package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

public class Rook extends ChessPiece {
	/**
	 * @param position
	 */
	public Rook(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.STRAIGHT, MoveType.LINE, board, false, Piece.ROOK, 5, hasMoved);
	}
	public Rook(Rook other) {
	    super(other);
	}

	@Override
	public AbstractChessPiece clonePiece() {
		return new Rook(this);
	}
}