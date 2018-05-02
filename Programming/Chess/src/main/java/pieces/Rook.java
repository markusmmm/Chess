package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Console;
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
	public ChessPiece clonePiece() {
		return new Rook(this);
	}

	/**
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		Vector2 delta = destination.sub(position());
		Console.printNotice(this + " checking move " + position() + " -> " + destination + "\tdelta: " + delta);

		if(super.legalMove(destination)) {

			//Console.printNotice(this + " (" + moveType + ", " + moves.size() + ") begun move check.");
			//Console.printNotice("Position: " + position() + ", destination: " + destination + ", delta: " + delta);

			if (moveType == MoveType.STEP) {
				boolean result = moves.contains(delta);
				if(result) Console.printSuccess("Move success");
				return result;
			}
			else if (moveType == MoveType.LINE)
				for (Vector2 move : moves) {
					Console.printNotice("\tChecking move " + move);
					if (move.isParallelTo(delta)) {
						Console.printSuccess("Move success");
						return true;
					}
				}
		}

		Console.printError("Move failure");
        return false;
	}
}