package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Vector2;
import resources.Piece;

public class Rook extends ChessPiece {
	/**
	 * @param position
	 */
	public Rook(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.STRAIGHT, ActionType.LINE, board, false, Piece.ROOK, 5, hasMoved);
	}
	private Rook(Vector2 position, Rook other) { super(position, other); }

	@Override
	public AbstractChessPiece clonePiece(Vector2 position) {
		return new Rook(position,this);
	}
}