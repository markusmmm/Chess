package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece<T extends ChessPiece<T>> implements IChessPiece<T> {

	protected Vector2 position;

	protected final Alliance alliance;
	protected final boolean canJump;
	protected final Piece piece;
	protected final Board board;

	protected List<Vector2> moveLog = new ArrayList<Vector2>();

    /**
     *
     * @param position The piece's initial position on the board
     */
    public ChessPiece(Vector2 position, Alliance alliance, Board board, boolean canJump, Piece piece) {
        this.position = position;
        this.alliance = alliance;
        this.board = board;
        this.canJump = canJump;
        this.piece = piece;
    }

	public Vector2 position() { return position; }
	public Alliance alliance() { return alliance; }
	public boolean canJump() { return canJump; }
	public Piece piece() { return piece; }

	/**
	 * 
	 * @param move
	 */
	public abstract boolean legalMove(Vector2 move);

	/**
	 * If the piece has been moved
	 */
	public boolean hasMoved() {
		return moveLog.size() != 0;
	}

	/**
	 * 
	 * @param move
	 */
	public boolean move(Vector2 move) {
		if (legalMove(move)) {
		    moveLog.add(move);
			position = new Vector2(move.getX(), move.getY());
			return true;
		}
		return false;
	}

	public void remove() {
		moveLog.clear();
	}

	/**
	 * checks one by one position from this position
	 * toward destination, returns false if runs into another piece
	 */
	protected boolean freePath(Vector2 destination) {
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