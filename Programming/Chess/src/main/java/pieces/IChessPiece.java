package pieces;

import resources.*;
import management.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface IChessPiece {

	HashSet<Move> moves = new HashSet<Move>();

	Piece piece();

	/**
	 * 
	 * @param start
	 * @param end
	 * @param board
	 */
	boolean legalMove(Vector2 start, Vector2 end, Board board);

	List<Vector2> possibleMoves(Board board);

	boolean canJump();

	/**
	 * 
	 * @param start
	 * @param end
	 */
	boolean move(Vector2 start, Vector2 end);

	void remove();

}