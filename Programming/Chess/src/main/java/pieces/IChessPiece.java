package pieces;

import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.Set;

public interface IChessPiece {

	Vector2 position();
	Alliance alliance();
	Piece piece();
	int getValue();

	Set<Vector2> getPossibleDestinations();
	Set<Vector2> getPossibleDestinations(int moveI);

	boolean hasMoved();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end, int moveI);

	ChessPiece clonePiece();
}