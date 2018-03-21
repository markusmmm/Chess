package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public abstract class ChessPiece implements IChessPiece, Collection<Vector2> {

	protected Vector2 position;

	protected final Alliance alliance;
	protected final boolean canJump;
	protected final Piece piece;
	protected final Board board;

	protected Stack<Vector2> moveLog = new Stack<Vector2>();

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
	public Piece piece() { return piece; }

	/**
	 * Checks if the piece can go to the given destination
	 * super.legalMove checks if the destination is within the board's boundaries, and if the piece at the given destination is hostile
	 * @param destination
	 * @return Whether or not the move can be performed
	 */
	protected boolean legalMove(Vector2 destination) {
		IChessPiece endPiece = board.getPiece(destination);
		if(endPiece != null && endPiece.alliance().equals(alliance)) return false;

		return board.insideBoard(position) && board.insideBoard(destination);
	}

	/**
	 * @return Whether or not the piece has been moved during the game
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

		if (!legalMove(move)) return false; // If the destination is unreachable, the move fails

		position = new Vector2(move.getX(), move.getY());
		moveLog.push(move);
		return true;
	}

	/**
	 * checks one by one position from this position
	 * toward destination
	 * @return false if runs into another piece
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

	public void syncContent(ChessPiece other) {
		moveLog = other.moveLog;
	}

	@Override
    public String toString() {
	    return alliance + " " + piece;
    }
}