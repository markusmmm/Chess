package pieces;


import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;


public class Bishop extends ChessPiece {

	public Bishop(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.DIAGONAL, MoveType.LINE, board, false, Piece.BISHOP, hasMoved);
	}
	public Bishop(Bishop other) {
		super(other);
	}

	@Override
	public IChessPiece clonePiece() {
		return new Bishop(this);
	}
}