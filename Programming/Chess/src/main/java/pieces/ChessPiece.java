package pieces;

import management.AbstractBoard;
import management.Board;
import resources.Alliance;
import resources.Move;
import resources.Piece;
import resources.Vector2;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class ChessPiece implements IChessPiece {

	protected Vector2 position;

	protected final Alliance alliance;
	protected final boolean canJump;
	protected final Piece piece;
	protected final Board board;
	protected final int value;

	private boolean hasMoved = false;
	private int moveI = -1;

	protected Set<Vector2> destinationBuffer = new HashSet<>();

    /**
     *
     * @param position The piece's initial position on the board
     */
    public ChessPiece(Vector2 position, Alliance alliance, AbstractBoard board, boolean canJump, Piece piece, int value) {
        this.position = position;
        this.alliance = alliance;
        this.board = (Board)board;
        this.canJump = canJump;
        this.piece = piece;
        this.value = value;
    }
    protected ChessPiece(ChessPiece other) {
    	//if(!getClass().isInstance(other.getClass())) { Will always run, even if instances are equal
		//	Console.printWarning("Attempting to clone " + other.getClass().getCanonicalName() + " into " + getClass().getCanonicalName());
		//}

    	position = other.position;
    	alliance = other.alliance;
    	canJump = other.canJump;
    	piece = other.piece;
    	board = other.board.clone();
    	value = other.value;

    	hasMoved = other.hasMoved;
    	moveI = other.moveI;

    	Set<Vector2> tempBuffer = new HashSet<>();
		tempBuffer.addAll(destinationBuffer);
    	destinationBuffer = tempBuffer;
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
		// Check if victim is of opposite alliance
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
	 * @param destination
	 */
	public boolean move(Vector2 destination, int moveI) {
		System.out.println("Attempting to move " + alliance + " " + piece + " from " + position + " to " + destination);

		getPossibleDestinations(moveI);
		if(!destinationBuffer.contains(destination))
			return false;

		position = new Vector2(destination.getX(), destination.getY());
		hasMoved = true;

		System.out.println("Move performed. New pos: " + position);

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

	public Set<Vector2> getPossibleDestinations() {
		return getPossibleDestinations(board.moveI());
	}
	public Set<Vector2> getPossibleDestinations(int moveI) {
		if(this.moveI != moveI) {
			System.out.println("Calculating destinations for " + toString() + " [" + this.moveI + " -> " + moveI + "]");
			this.moveI = moveI;

			destinationBuffer.clear();
			calculatePossibleDestinations();
		}

		Set<Vector2> tempBuffer = new HashSet<>();
		for(Vector2 v : destinationBuffer)
			tempBuffer.add(v);
		return tempBuffer;
	}

	protected abstract void calculatePossibleDestinations();

	@Override
    public String toString() {
	    return alliance + " " + piece;
    }

    @Override
	public ChessPiece clone() { return clonePiece(); }
}