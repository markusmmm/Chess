package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

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
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		return (
				insideBoard(move) &&
						positiveCoordinates(move) &&
						inStraights(move) &&
						freePath(move)
		);
	}

	/**
	 * @return a list of all possible moves from this position
	 */

	public List<Vector2> getPossibleDestinations() {
		List<Vector2> possibleDestinations = new ArrayList<Vector2>();

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


		return filterPossibleDestinations(possibleDestinations);
	}
}