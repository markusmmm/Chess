package pieces;

import resources.*;
import management.*;

public interface IChessPiece {

	static Vector2[] moves = null;

	Piece piece();

	/**
	 * 
	 * @param move
	 * @param board
	 */
	boolean legalMove(Vector2 move, Board board);

	boolean canJump();

	/**
	 * 
	 * @param move
	 * @param board
	 */
	boolean move(Vector2 move, Board board);

	void remove();

}