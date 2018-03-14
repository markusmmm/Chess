package pieces;


import management.*;
import resources.Piece;
import resources.Vector2;

import java.util.ArrayList;
import java.util.List;


public class Bishop extends ChessPiece {

	private final boolean canJump;
	private final Piece piece;
	private Vector2 position;


	public Bishop(Vector2 position){
		super();
		canJump = false;
		piece = Piece.BISHOP;
		this.position = position;

	}

	/**
	 *
	 * @param board
	 * @return a list of all possible moves from this position
	 */

	// TODO: board should return its size. board.getSize/getDimentions

	public List<Vector2> getPossibleMoves(Board board){

		int BoardSize = 8;
		List<Vector2> possibleMoves = new ArrayList<>();

		int row = position.getX();
		int collumn = position.getY();


		for(int j = collumn + 1, i = row + 1; j < 8 && i < 8; j++, i++){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move,board))
					possibleMoves.add(new Vector2(i, j));
			else
				break; // TODO: Should also check if it hits an enemy.

			}



		for(int j = collumn - 1, i = row + 1; j > -1 && i < 8; j--, i++){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move,board))
				possibleMoves.add(new Vector2(i, j));
			else
				break; // TODO: Should also check if it hits an enemy.

		}

		for(int j = collumn - 1, i = row - 1; j > -1 && i > -1; j--, i--){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move,board))
				possibleMoves.add(new Vector2(i, j));
			else
				break; // TODO: Should also check if it hits an enemy.

		}

		for(int j = collumn + 1, i = row - 1; j < 8 && i > -1; j++, i--){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move,board))
				possibleMoves.add(new Vector2(i, j));
			else
				break; // TODO: Should also check if it hits an enemy.

		}

		return possibleMoves;
	}



	public Piece piece() {
		return piece;
	}

	/**
	 * checks one by one position from this position
	 * toward destination, returns false if runs into another piece
	 */
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

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public boolean legalMove(Vector2 move, Board board) {
		return (
				positiveCoordinates(move) &&
						inDiagonals(move) &&
						freePath(move, board)
		);
	}

	private boolean inDiagonals(Vector2 newPos) {
		return Math.abs(this.position.getX() - newPos.getX()) == Math.abs(this.position.getY() - newPos.getY());
	}

	public boolean canJump() {
		return canJump;

	}

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}



}