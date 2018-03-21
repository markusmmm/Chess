package pieces;

import resources.*;
import management.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IChessPiece {

	Vector2 position();
	Alliance alliance();
	Piece piece();

	Set<Vector2> getPossibleDestinations();

	boolean hasMoved();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end);

	IChessPiece clonePiece();
	void syncContent(ChessPiece piece);

}