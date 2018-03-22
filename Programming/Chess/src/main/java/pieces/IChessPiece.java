package pieces;

import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.Set;

public interface IChessPiece {

	Vector2 position();
	Alliance alliance();
	Piece piece();

	Set<Vector2> getPossibleDestinations(String caller);
	Set<Vector2> getPossibleDestinations();

	boolean hasMoved();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end);
	
	IChessPiece clonePiece();

    int getValue();

}