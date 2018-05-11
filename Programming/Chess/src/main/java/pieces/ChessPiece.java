package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Console;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;

public abstract class ChessPiece extends AbstractChessPiece {

	/**
	 *
	 */
	protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, MoveType moveType, AbstractBoard board, boolean canJump, Piece piece, boolean hasMoved) {
		super(position, alliance, moves, moveType, board, canJump, piece, hasMoved);
	}
	protected ChessPiece(ChessPiece other) {
		super(other);
	}

	@Override
	public boolean isLegalMove(Vector2 destination) {
		Vector2 delta = destination.sub(position());

		if(super.isLegalMove(destination)) {
			if(isShadam(destination)) return true; // All additional evaluation has been approved. Shadam can be performed

			if (moveType == MoveType.STEP) {
				return moves.contains(delta);
			}
			else if (moveType == MoveType.LINE)
				for (Vector2 move : moves) {
					if (move.isParallelTo(delta))
						return true;
				}
		}

		return false;
	}
}
