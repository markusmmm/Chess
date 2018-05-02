package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public class Queen extends ChessPiece {

    public Queen(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, Vector2.UNIT, MoveType.LINE, board, false, Piece.QUEEN, 9, hasMoved);
	}
    public Queen(Queen other) {
        super(other);
    }

    @Override
    public ChessPiece clonePiece() {
        return new Queen(this);
    }

	/**
	 * 
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
        if(!super.legalMove(destination)) return false;

		return (
            (inDiagonals(destination) || inStraights(destination)) &&
            freePath(destination)
        );
	}
}