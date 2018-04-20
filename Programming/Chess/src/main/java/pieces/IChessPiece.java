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
	int getValue();

	Set<Vector2> getPossibleDestinations();

	boolean hasMoved();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end, Board board);

	void loadData(List<Boolean> vals);

	ChessPiece clonePiece();
}