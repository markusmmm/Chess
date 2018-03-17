package pieces;

import resources.*;

import java.util.ArrayList;
import java.util.List;

import management.*;

public class Knight  extends ChessPiece<Knight> {
	
	/**
	 * 
	 * @param position
	 */
	public Knight (Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board, true, Piece.KNIGHT);
	}
    public Knight clone() {
        return new Knight(position, alliance, board);
    }
	
	/**
	 * 
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		return (
				positiveCoordinates(move) &&
						freePath(move)
		);
	}

	public List<Vector2> getPossibleMoves() {
		//TODO Knight.getPossibleMoves
		throw new UnsupportedOperationException();
	}

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}
}