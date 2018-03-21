package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rook extends ChessPiece {
	private Set<Vector2> possibleMoves = new HashSet<>();

	/**
	 * @param position
	 */
	public Rook(Vector2 position, Alliance alliance, Board board) {
		super(position, alliance, board, false, Piece.ROOK);
	}

	public Rook clonePiece() {
		return new Rook(position, alliance, board);
	}

	/**
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		if(!super.legalMove(destination)) return false;

		System.out.println("inStraights: " + inStraights(destination));
		System.out.println("freePath: " + freePath(destination));

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
		for (int variable = 0; variable < board.getSize(); variable++) {
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