package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece implements IChessPiece {

	protected Vector2 position;
	protected Alliance alliance;

	protected List<Vector2> moveLog = new ArrayList<Vector2>();
	private boolean moved = false;

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
	protected ChessPiece(Vector2 position, Alliance alliance) {
	    this.position = position;
	    this.alliance = alliance;
	}
	/**
	 * checks one by one position from this position
	 * toward destination, returns false if runs into another piece
	 */
	protected boolean freePath(Vector2 destination, Board board) {
		Vector2 path = null;
		int between = position.distance(destination) - 1;

		for (int step = 0; step < between; step++) {
			path = path.stepToward(destination);
			if (board.getPiece(path) != null) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @return if piece is placed in the lines:
	 * up, down, left, right
	 *
	 * logic is: if only x or y change, the piece move in a straight path
	 */
	protected boolean inStraights(Vector2 move) {
		return (
				( this.position.getX() == move.getX() && this.position.getY() != move.getY() )
						||
						( this.position.getX() != move.getX() && this.position.getY() == move.getY() )
		);
	}
	protected boolean inDiagonals(Vector2 newPos) {
		return Math.abs(this.position.getX() - newPos.getX()) == Math.abs(this.position.getY() - newPos.getY());
	}

	@Override
    public String toString() {
	    return piece() + "\t|\t" + alliance;
    }
}