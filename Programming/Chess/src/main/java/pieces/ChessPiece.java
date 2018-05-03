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
	protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, MoveType moveType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
		super(position, alliance, moves, moveType, board, canJump, piece, value, hasMoved);
	}
	protected ChessPiece(ChessPiece other) {
		super(other);
	}

	@Override
	public boolean legalMove(Vector2 destination) {
		Vector2 delta = destination.sub(position());
		//Console.printNotice(this + " checking relativeMovement " + position() + " -> " + destination + "\tdelta: " + delta);

		if(super.legalMove(destination)) {

			//Console.printNotice(this + " (" + moveType + ", " + moves.size() + ") begun relativeMovement check.");
			//Console.printNotice("Position: " + position() + ", destination: " + destination + ", delta: " + delta);

			if (moveType == MoveType.STEP) {
				boolean result = moves.contains(delta);
				if(result) Console.printSuccess("Move check success");
				return result;
			}
			else if (moveType == MoveType.LINE)
				for (Vector2 move : moves) {
					Console.printNotice("\tChecking dir " + move + " to delta " + delta);
					if (move.isParallelTo(delta)) {
						Console.printSuccess("\tMove check success");
						return true;
					} else {
						Console.printError("Move " + move + " is not parallel to " + delta);
					}
				}
		}

		//Console.printError("Move failure");
		return false;
	}
}
