package Pieces;

public interface IChessPiece {

	String name();

	boolean legalMove();

	boolean canJump();

	/**
	 * 
	 * @param move
	 */
	boolean move(Vector2 move);

	void remove();

	static Vector2[] moves = null;

}