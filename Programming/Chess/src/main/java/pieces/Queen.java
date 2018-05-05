package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Vector2;
import resources.Piece;

public class Queen extends ChessPiece {

    public Queen(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.UNIT, ActionType.LINE, board, false, Piece.QUEEN, 9, hasMoved);
	}
    private Queen(Vector2 position, Queen other) { super(position, other); }

    @Override
    public AbstractChessPiece clonePiece(Vector2 position) {
        return new Queen(position, this);
    }
}