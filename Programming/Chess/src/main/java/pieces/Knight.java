package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Knight  extends ChessPiece {

	public Knight (Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance,
                new HashSet<>(Arrays.asList(
                        new Vector2( 2, 1), new Vector2( 1, 2),
                        new Vector2(-2, 1), new Vector2(-1, 2),
                        new Vector2( 2,-1), new Vector2( 1,-2),
                        new Vector2(-2,-1), new Vector2(-1,-2))),
                MoveType.STEP, board, true, Piece.KNIGHT, 3, hasMoved);
	}
	public Knight(Knight other) {
		super(other);
	}

	@Override
	public ChessPiece clonePiece() {
		return new Knight(this);
	}

	/**
	 *
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		if(!super.legalMove(destination)) return false;

		Vector2 delta = destination.sub(position());
		return moves.contains(delta);
	}
}