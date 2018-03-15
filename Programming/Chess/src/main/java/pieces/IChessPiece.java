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
	 * @param end
	 * @param board
	 */
	boolean legalMove(Vector2 end, Board board);

	boolean canJump();

	/**
	 *
	 * @param end
	 * @param board
	 */
	boolean move(Vector2 end, Board board);

	void remove();

}