package pieces;

import resources.*;
import management.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface IChessPiece {

	Vector2 position();
	Alliance alliance();
    boolean canJump();
	Piece piece();

	/**
	 *
	 * @param end
	 */
	boolean legalMove(Vector2 end);

	List<Vector2> getPossibleMoves();

	boolean hasMoved();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end);

	void remove();

	IChessPiece clonePiece();

}