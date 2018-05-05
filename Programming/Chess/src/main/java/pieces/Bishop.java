package pieces;


import management.AbstractBoard;
import resources.Alliance;
import resources.Vector2;
import resources.Piece;


public class Bishop extends ChessPiece {

	public Bishop(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.DIAGONAL, ActionType.LINE, board, false, Piece.BISHOP, 3, hasMoved);
	}
	private Bishop(Vector2 position, Bishop other) { super(position, other); }

	@Override
	public AbstractChessPiece clonePiece(Vector2 position) {
		return new Bishop(position,this);
	}
}