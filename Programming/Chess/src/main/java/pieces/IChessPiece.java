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

	List<Vector2> getPossibleDestinations();

	boolean hasMoved();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end);

	void remove();

	IChessPiece clonePiece();
	void syncContent(ChessPiece piece);

}