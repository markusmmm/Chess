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
		return this.position;
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
			position = new Vector2(move.getX(), move.getY());
			moved = true;
			return true;
		}
		return false;
	}

	public void remove() {
		// TODO - implement ChessPiece.remove
		throw new UnsupportedOperationException();
	}

	protected ChessPiece() {
		// TODO - implement ChessPiece.ChessPiece
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param position
	 */
	protected ChessPiece(Vector2 position) {
		// TODO - implement ChessPiece.ChessPiece
		throw new UnsupportedOperationException();
	}

}