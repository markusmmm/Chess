package pieces;

import management.Board;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.List;
import java.util.Set;

public interface IChessPiece {

	Vector2 position();
	Alliance alliance();
	Piece piece();

	Set<Vector2> getPossibleActions();
	Set<Vector2> getPossibleAttacks();

	boolean hasMoved();

	boolean isLegalMove(Vector2 destination);

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end);

	void loadData(List<Boolean> values);

	boolean freePath(Vector2 destination);

	AbstractChessPiece clonePiece();
}