package pieces;

import resources.*;
import management.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface IChessPiece {

	HashSet<Move> moves = new HashSet<Move>();

	Piece piece();

	Vector2 position();

	Alliance alliance();

	/**
	 *
	 * @param end
	 */
	boolean legalMove(Vector2 end);

	List<Vector2> getPossibleMoves();

	boolean canJump();

	boolean hasMoved();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end);

	void remove();

}