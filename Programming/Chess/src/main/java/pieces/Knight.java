package pieces;

import resources.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import management.*;

public class Knight  extends ChessPiece {

	Vector2[] moves = new Vector2[] {
			new Vector2( 2, 1), new Vector2( 1, 2),
			new Vector2(-2, 1), new Vector2(-1, 2),
			new Vector2( 2,-1), new Vector2( 1,-2),
			new Vector2(-2,-1), new Vector2(-1,-2),
	};

	/**
	 * 
	 * @param position
	 */
	public Knight (Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board, true, Piece.KNIGHT);
	}
    public Knight clonePiece() {
        return new Knight(position, alliance, board);
    }
	
	/**
	 * 
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		return positiveCoordinates(move) && getPossibleMoves().contains(position.add(move));
	}

	public List<Vector2> getPossibleMoves() {
		List<Vector2> possibleMoves = new ArrayList<Vector2>();
		for (Vector2 move : moves)
			possibleMoves.add(position.add(move));

		return possibleMoves;
	}

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}
}