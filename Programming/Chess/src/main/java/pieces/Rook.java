package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

public class Rook extends ChessPiece {
	private final Piece piece;
	private final boolean canJump;
	private Vector2 position;


	public Piece piece() {
		return piece;
	}

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public boolean legalMove(Vector2 move, Board board) {
		return (
				positiveCoordinates(move) &&
						inStraights(move) &&
						freePath(move, board)
		);
	}

	public boolean canJump() {
		return canJump;
	}


	/**
	 *
	 * @param board
	 * @return a list of all possible moves from this position
	 */

	// TODO: board should return its size. board.getSize/getDimentions

	public List<Vector2> getPossibleMoves(Board board) {
		List<Vector2> possibleMoves = new ArrayList<>();

		int row = position.getX();
		int collumn = position.getY();

		for(int i = row + 1; i < 8; i++){
			Vector2 move = new Vector2(i,collumn);
			if(legalMove(move,board)) possibleMoves.add(move);
			else break; // TODO: Should also check if it hits an enemy.
		}

		for(int i = row - 1; i > -1; i--){
			Vector2 move = new Vector2(i,collumn);
			if(legalMove(move,board)) possibleMoves.add(move);
			else break; // TODO: Should also check if it hits an enemy.
		}

		for(int i = collumn + 1; i < 8; i++){
			Vector2 move = new Vector2(i,collumn);
			if(legalMove(move,board)) possibleMoves.add(move);
			else break; // TODO: Should also check if it hits an enemy.
		}
		for(int i = collumn - 1; i > -1; i--){
			Vector2 move = new Vector2(i,collumn);
			if(legalMove(move,board)) possibleMoves.add(move);
			else break; // TODO: Should also check if it hits an enemy.
		}


		return possibleMoves;
	}


		/**
         *
         * @param position
         */
	public Rook(Vector2 position) {
		super();
		this.position = position;
		piece = Piece.ROOK;
		canJump = false;
	}



	/**
	 * @return if piece is placed in the lines:
	 * up, down, left, right
	 *
	 * logic is: if only x or y change, the piece move in a straight path
	 */
	private boolean inStraights(Vector2 move) {
		return (
				( this.position.getX() == move.getX() && this.position.getY() != move.getY() )
						||
						( this.position.getX() != move.getX() && this.position.getY() == move.getY() )
		);
	}

	private boolean freePath(Vector2 destination, Board board) {
		Vector2 path = null;
		int between = this.position.distance(destination) - 1;

		for (int step = 0; step < between; step++) {
			path = path.stepToward(destination);
			if (board.getPiece(path) != null) {
				return false;
			}
		}
		return true;
	}

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}

}