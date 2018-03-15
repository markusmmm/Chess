package pieces;

import resources.*;
import management.*;

import java.util.List;

public class Queen extends ChessPiece {
	private final boolean canJump = false;
	private final Piece piece = Piece.QUEEN;

	public Queen(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board);
	}
	public Piece piece() {
		return piece;
	}

	/**
	 * 
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		return (
		        positiveCoordinates(move) &&
                        inDiagonals(move) &&
                        inStraights(move) &&
                        freePath(move)
        );
	}

	public List<Vector2> getPossibleMoves() {
		//TODO Queen.getPossibleMoves
		throw new UnsupportedOperationException();
	}

	public boolean canJump() {
		return canJump;
	}

	private boolean positiveCoordinates(Vector2 pos) {
	    return 0 <= pos.getX() && 0 <= pos.getY();
    }

	public Queen clone() {
		return new Queen(position, alliance, board);
	}
}