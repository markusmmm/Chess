package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rook extends ChessPiece {

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

		return (
			inStraights(destination) &&
			freePath(destination)
		);
	}

	/**
	 * @return a list of all possible moves from this position
	 */

	public Set<Vector2> getPossibleDestinations() {
		Set<Vector2> possibleDestinations = new HashSet<>();

		int row = position.getX();
		int column = position.getY();

		for (int i = row + 1; i < board.getSize(); i++) {
			Vector2 destination = new Vector2(i, column);
			if (legalMove(destination)) possibleDestinations.add(destination);
			if(board.getPiece(destination) != null){
				if(board.getPiece(destination).alliance() != this.alliance) possibleDestinations.add(destination);
			}
		}

		for (int i = row - 1; i > -1; i--) {
			Vector2 destination = new Vector2(i, column);
			if (legalMove(destination)) possibleDestinations.add(destination);
			if(board.getPiece(destination) != null){
				if(board.getPiece(destination).alliance() != this.alliance) possibleDestinations.add(destination);
			}
		}

		for (int i = column + 1; i < board.getSize(); i++) {
			Vector2 destination = new Vector2(i, column);
			if (legalMove(destination)) possibleDestinations.add(destination);
			if(board.getPiece(destination) != null){
				if(board.getPiece(destination).alliance() != this.alliance) possibleDestinations.add(destination);
			}
		}
		for (int i = column - 1; i > -1; i--) {
			Vector2 destination = new Vector2(i, column);
			if (legalMove(destination)) possibleDestinations.add(destination);
			if(board.getPiece(destination) != null){
				if(board.getPiece(destination).alliance() != this.alliance) possibleDestinations.add(destination);
			}
		}

		return possibleDestinations;
	}
}