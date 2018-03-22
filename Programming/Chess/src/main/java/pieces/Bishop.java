package pieces;


import management.Board;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;


public class Bishop extends ChessPiece {
	private Set<Vector2> possibleMoves = new HashSet<>();
	public Bishop(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board, false, Piece.BISHOP);
	}
    public Bishop clonePiece() {
        return new Bishop(position, alliance, board);
    }

	/**
	 *
	 * @return a list of all possible moves from this position
	 */


	public Set<Vector2> getPossibleDestinations(String caller) {
		logActionPossibleDestinations(caller);

		possibleMoves.clear();
		for (int variable = 0; variable < board.getSize(); variable++) {
			//diagonals
			evalMove(new Vector2(position.getX() + variable, position.getY() + variable));

			evalMove(new Vector2(position.getX() + variable, position.getY() - variable));
			evalMove(new Vector2(position.getX() - variable, position.getY() + variable));
			evalMove(new Vector2(position.getX() - variable, position.getY() - variable));
		}
		return possibleMoves;
	}
	private void evalMove(Vector2 vector) {
		if(legalMove(vector)) possibleMoves.add(vector);
	}

	/**
	 *
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		if(!super.legalMove(destination)) return false;

		return (
			inDiagonals(destination) &&
			freePath(destination)
		);
	}
}