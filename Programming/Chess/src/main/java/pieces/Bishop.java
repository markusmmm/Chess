package pieces;


import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;


public class Bishop extends ChessPiece {
	public Bishop(Vector2 position, Alliance alliance, AbstractBoard board){
		super(position, alliance, board, false, Piece.BISHOP, 3);
	}

	private Bishop(Bishop other) {
		super(other);
	}
    public Bishop clonePiece() {
        return new Bishop(this);
    }

	/**
	 *
	 * @return a list of all possible moves from this position
	 */


	protected void calculatePossibleDestinations() {
		for (int variable = 0; variable < board.size(); variable++) {
			//diagonals
			evalMove(new Vector2(position.getX() + variable, position.getY() + variable));

			evalMove(new Vector2(position.getX() + variable, position.getY() - variable));
			evalMove(new Vector2(position.getX() - variable, position.getY() + variable));
			evalMove(new Vector2(position.getX() - variable, position.getY() - variable));
		}
	}
	private void evalMove(Vector2 vector) {
		if(legalMove(vector)) destinationBuffer.add(vector);
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