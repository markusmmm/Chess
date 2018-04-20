package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.List;

public abstract class ChessPiece implements IChessPiece {
	private MediaHelper media = new MediaHelper();

	private Vector2 position;
	protected final Alliance alliance;
	protected Board board;
	protected final boolean canJump;
	protected final Piece piece;
	protected final int value;

	private boolean hasMoved;

    /**
     *
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
    protected ChessPiece(ChessPiece other) {
    	position = other.position;
    	alliance = other.alliance;
    	board = other.board;
    	canJump = other.canJump;
    	piece = other.piece();
    	value = other.value;
    	hasMoved = other.hasMoved;
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
		//resources.Console.println("(ChessPiece) Board is live: " + board.isLive());
		IChessPiece endPiece = board.getPiece(destination);
		// Prevents attack on an allied piece
		if(endPiece != null && endPiece.alliance().equals(alliance)) return false;

		if(!board.insideBoard(position()) || !board.insideBoard(destination)) return false;

		if(!board.hasKing(alliance))
			return true;	// Special-case check for boards where a king was never created

		King king = board.getKing(alliance);
		if(king == null) return false;

		// Lastly, check if king is in check, and whether or not the move resolves it (SHOULD OCCUR LAST, FOR OPTIMIZATION)
		return king.resolvesCheck(position(), destination);
	}

	/**
	 * @return Whether or not the piece has been moved during the game
	 */
	public boolean hasMoved() {
		return hasMoved;
	}


	/**
	 * 
	 * @param destination
	 */
	public boolean move(Vector2 destination, Board board) {
		this.board = board;

		//resources.Console.println("Attempting to move " + alliance + " " + piece + " from " + position + " to " + move);
		if (!legalMove(destination)) return false; // If the destination is unreachable, the move fails

		position = destination;
		media.playSound("move.mp3");
		hasMoved = true;

		//resources.Console.println("Move performed. New pos: " + position);
		return true;
	}

	public void loadData(List<Boolean> vals) {
		hasMoved = vals.get(0);
	}

	/**
	 * checks one by one position from this position
	 * toward destination
	 * @return false if runs into another piece
	 */
	protected boolean freePath(Vector2 destination) {
		Vector2 position = position();

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
		Vector2 position = position();

		return (
				( position.getX() == move.getX() && position.getY() != move.getY() )
						||
						( position.getX() != move.getX() && position.getY() == move.getY() )
		);
	}
	protected boolean inDiagonals(Vector2 newPos) {
		Vector2 position = position();
		return Math.abs(position.getX() - newPos.getX()) == Math.abs(position.getY() - newPos.getY());
	}

	@Override
    public String toString() {
	    return alliance + " " + piece;
    }

    @Override
	public boolean equals(Object o) {
		if(!(o instanceof ChessPiece)) return false;
		ChessPiece other = (ChessPiece)o;

		return position == other.position;
	}

    public ChessPiece clone() {
		return clonePiece();
	}
}