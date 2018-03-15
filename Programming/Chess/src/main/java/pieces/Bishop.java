package pieces;


import management.*;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.ArrayList;
import java.util.List;


public class Bishop extends ChessPiece {

	private final boolean canJump;
	private final Piece piece;
	private Vector2 position;


	public Bishop(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board);
		canJump = false;
		piece = Piece.BISHOP;
	}

	/**
	 *
	 * @return a list of all possible moves from this position
	 */

	// TODO: board should return its size. board.getSize/getDimentions

	public List<Vector2> getPossibleMoves(){

		List<Vector2> possibleMoves = new ArrayList<Vector2>();

		int row = position.getX();
		int collumn = position.getY();


		for(int j = collumn + 1, i = row + 1; j < 8 && i < 8; j++, i++){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
					possibleMoves.add(new Vector2(i, j));
			else
				break; // TODO: Should also check if it hits an enemy.

			}



		for(int j = collumn - 1, i = row + 1; j > -1 && i < 8; j--, i++){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
				possibleMoves.add(new Vector2(i, j));
			else
				break; // TODO: Should also check if it hits an enemy.

		}

		for(int j = collumn - 1, i = row - 1; j > -1 && i > -1; j--, i--){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
				possibleMoves.add(new Vector2(i, j));
			else
				break; // TODO: Should also check if it hits an enemy.

		}

		for(int j = collumn + 1, i = row - 1; j < 8 && i > -1; j++, i--){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
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
	 *
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		return (
				positiveCoordinates(move) &&
						inDiagonals(move) &&
						freePath(move)
		);
	}


	public boolean canJump() {
		return canJump;

	}

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}

	public Bishop clone() {
		return new Bishop(position, alliance, board);
	}
}