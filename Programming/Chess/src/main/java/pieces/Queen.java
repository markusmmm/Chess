package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

public class Queen extends ChessPiece {

    public Queen(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.UNIT, ActionType.LINE, board, false, Piece.QUEEN, 9, hasMoved);
	}
    public Queen(Queen other) {
        super(other);
    }

    @Override
    public AbstractChessPiece clonePiece() {
        return new Queen(this);
    }
}