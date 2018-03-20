package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ChessPiece implements IChessPiece {

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
	protected abstract boolean legalMove(Vector2 move);

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
		System.out.println("Attempting to move " + alliance + " " + piece + " from " + position + " to " + move);

		if (!legalMove(move)) return false;
		IChessPiece other = board.getPiece(position.add(move));
		if(other != null)
			if(other.alliance().equals(alliance)) return false; // Prevents a piece from taking another piece of the same color

		moveLog.add(move);
		position = new Vector2(move.getX(), move.getY());
		return true;
	}

	public void remove() {
		moveLog.clear();
	}

	/**
	 * checks one by one position from this position
	 * toward destination, returns false if runs into another piece
	 */
	protected boolean freePath(Vector2 destination) {
		Vector2 path = position;
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

	protected boolean insideBoard(Vector2 vector) {
		return (
				0 <= vector.getX() && vector.getX() < board.getSize() &&
						0 <= vector.getY() && vector.getY() < board.getSize());
	}

	protected boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}

	protected List<Vector2> filterPossibleDestinations(List<Vector2> list) {
		for(int i = list.size() - 1; i >= 0; i--) {
			Vector2 pos = list.get(i);
			if(!insideBoard(pos))
				list.remove(i);

			IChessPiece piece = board.getPiece(pos);

			if(piece != null)
				if(piece.alliance().equals(alliance))
					list.remove(i);
		}
		return list;
	}

	public void syncContent(ChessPiece other) {
		moveLog = other.moveLog;
	}

	@Override
    public String toString() {
	    return alliance + " " + piece;
    }
}