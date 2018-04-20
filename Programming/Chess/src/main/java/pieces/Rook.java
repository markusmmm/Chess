package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public class Rook extends ChessPiece {
	private final int value = 5;
	private Set<Vector2> possibleMoves = new HashSet<>();

	/**
	 * @param position
	 */
	public Rook(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
		super(position, alliance, board, false, Piece.ROOK, 5,hasMoved);
	}
	public Rook(Rook other) {
		super(other);
	}

	@Override
	public ChessPiece clonePiece() {
		return new Rook(this);
	}

	@Override
	public int getValue() {
		return value;
	}

	/**
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		if(!super.legalMove(destination)) return false;

		//resources.Console.println("inStraights: " + inStraights(destination));
		//resources.Console.println("freePath: " + freePath(destination));

		return (
			inStraights(destination) &&
			freePath(destination)
		);
	}

	/**
	 * @return a list of all possible moves from this position
	 */

	public Set<Vector2> getPossibleDestinations() {
		possibleMoves.clear();
		Vector2 position = position();

		for (int variable = 0; variable < board.size(); variable++) {
			//Straights
			evalMove(new Vector2(position.getX(), position.getY() + variable));
			evalMove(new Vector2(position.getX(), position.getY() - variable));
			evalMove(new Vector2(position.getX() + variable, position.getY()));
			evalMove(new Vector2(position.getX() - variable, position.getY()));
		}
		return possibleMoves;
	}
	private void evalMove(Vector2 vector) {
		if(legalMove(vector)) possibleMoves.add(vector);
	}
}