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
				positiveCoordinates(move) &&
						inStraights(move) &&
						freePath(move)
		);
	}

	/**
	 * @return a list of all possible moves from this position
	 */

	public List<Vector2> getPossibleMoves() {
		List<Vector2> possibleMoves = new ArrayList<Vector2>();

		int row = position.getX();
		int column = position.getY();

		for (int i = row + 1; i < board.getSize(); i++) {
			Vector2 move = new Vector2(i, column);
			if (legalMove(move)) possibleMoves.add(move);
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
		}

		for (int i = row - 1; i > -1; i--) {
			Vector2 move = new Vector2(i, column);
			if (legalMove(move)) possibleMoves.add(move);
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
		}

		for (int i = column + 1; i < board.getSize(); i++) {
			Vector2 move = new Vector2(i, column);
			if (legalMove(move)) possibleMoves.add(move);
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
		}
		for (int i = column - 1; i > -1; i--) {
			Vector2 move = new Vector2(i, column);
			if (legalMove(move)) possibleMoves.add(move);
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
		}


		return possibleMoves;
	}

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}
}