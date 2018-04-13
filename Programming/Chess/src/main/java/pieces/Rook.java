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

	public Rook clonePiece() {
		return new Rook(position, alliance, board, hasMoved());
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

		//System.out.println("inStraights: " + inStraights(destination));
		//System.out.println("freePath: " + freePath(destination));

		return (
			inStraights(destination) &&
			freePath(destination)
		);
	}

	/**
	 * @return a list of all possible moves from this position
	 */

	public Set<Vector2> getPossibleDestinations(String caller) {
		logActionPossibleDestinations(caller);

		possibleMoves.clear();
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