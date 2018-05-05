package pieces;

import management.Board;
import resources.Alliance;
import resources.Vector2;
import resources.Piece;

import java.util.List;
import java.util.Set;

public interface IChessPiece {

	Vector2 position();
	Alliance alliance();
	Piece piece();
	int getValue();

	Set<Vector2> getPossibleActions();
	Set<Vector2> getPossibleMoves();
	Set<Vector2> getPossibleAttacks();

	boolean legalAction(Vector2 destination);

	boolean hasMoved();
	boolean canJump();

	/**
	 *
	 * @param end
	 */
	boolean move(Vector2 end, Board board);

	void loadData(List<Boolean> vals);

	AbstractChessPiece clonePiece(Vector2 position);
	AbstractChessPiece clonePiece();
}