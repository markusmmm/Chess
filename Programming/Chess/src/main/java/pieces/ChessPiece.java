package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece implements IChessPiece {

	protected Vector2 position;
	protected List<Vector2> moveLog = new ArrayList<Vector2>();
	private boolean moved = false;

	public Vector2 getPosition() {
		return position;
	}

	public abstract Piece piece();

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public abstract boolean legalMove(Vector2 move, Board board);

	public abstract boolean canJump();

	/**
	 * If the piece has been moved (moveLog.size() == 0)
	 */
	public boolean hasMoved() {
		return moved;
	}

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public boolean move(Vector2 move, Board board) {
		if (legalMove(move, board)) {
		    moveLog.add(move);
			position = new Vector2(move.getX(), move.getY());
			moved = true;
			return true;
		}
		return false;
	}

	public void remove() {
		moveLog.clear();
		moved = false;
	}

	protected ChessPiece() {
		position = new Vector2(0, 0);
	}

	/**
	 * 
	 * @param position The piece's initial position on the board
	 */
	protected ChessPiece(Vector2 position) {
		this.position = position;
	}

}