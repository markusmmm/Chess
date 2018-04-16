package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.Set;

public abstract class ChessPiece implements IChessPiece {

	protected Vector2 position;

	protected final Alliance alliance;
	protected final boolean canJump;
	protected final Piece piece;
	protected final Board board;
	protected final int value;

	private boolean hasMoved;

    /**
     *
     * @param position The piece's initial position on the board
     */
    public ChessPiece(Vector2 position, Alliance alliance, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
        this.position = position;
        this.alliance = alliance;
        this.board = (Board)board;
        this.canJump = canJump;
        this.piece = piece;
        this.value = value;
        this.hasMoved = hasMoved;

    }

	public Vector2 position() { return position; }
	public Alliance alliance() { return alliance; }
	public Piece piece() { return piece; }
	public int getValue() { return value; }

	public Alliance otherAlliance() {
    	return alliance.equals(Alliance.BLACK) ? Alliance.WHITE : Alliance.BLACK;
	}

	/**
	 * Checks if the piece can go to the given destination
	 * super.legalMove checks if the destination is within the board's boundaries, and if the piece at the given destination is hostile
	 * @param destination
	 * @return Whether or not the move can be performed
	 */
	protected boolean legalMove(Vector2 destination) {
		//System.out.println("(ChessPiece) Board is live: " + board.isLive());
		IChessPiece endPiece = board.getPiece(destination);
		// Prevents attack on an allied piece
		if(endPiece != null && endPiece.alliance().equals(alliance)) return false;

		if(!board.insideBoard(position) || !board.insideBoard(destination)) return false;

		if(!board.hasKing(alliance))
			return true;	// Special-case check for boards where a king was never created

		King king = board.getKing(alliance);
		if(king == null) return false;

		// Lastly, check if king is in check, and whether or not the move resolves it (SHOULD OCCUR LAST, FOR OPTIMIZATION)
		return king.resolvesCheck(position, destination);
	}

	/**
	 * @return Whether or not the piece has been moved during the game
	 */
	public boolean hasMoved() {
		return hasMoved;
	}


	/**
	 * 
	 * @param move
	 */
	public boolean move(Vector2 move) {
		//System.out.println("Attempting to move " + alliance + " " + piece + " from " + position + " to " + move);
		if (!legalMove(move)) return false; // If the destination is unreachable, the move fails

		MediaHelper media = new MediaHelper();
		media.playSound39("move.mp3");
		position = new Vector2(move.getX(), move.getY());
		hasMoved = true;

		//System.out.println("Move performed. New pos: " + position);
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

	@Override
    public String toString() {
	    return alliance + " " + piece;
    }



    @Override
	public ChessPiece clone() { return clonePiece(); }
}