package pieces;

import resources.*;
import management.*;

public class Queen extends ChessPiece {
	private final boolean canJump = false;
	private final Piece piece = Piece.QUEEN;

	public Queen(Vector2 position, Alliance alliance) {
		super(position, alliance);
	}
	public Piece piece() {
		return piece;
	}

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public boolean legalMove(Vector2 move, Board board) {
		return (
		        positiveCoordinates(move) &&
                        inDiagonals(move) &&
                        inStraights(move) &&
                        freePath(move, board)
        );
	}

    public boolean canJump() {
		return canJump;
	}

	private boolean positiveCoordinates(Vector2 pos) {
	    return 0 <= pos.getX() && 0 <= pos.getY();
    }

}