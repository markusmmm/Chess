package pieces;

import resources.*;

import java.util.ArrayList;
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
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		return positiveCoordinates(destination) && getPossibleDestinations().contains(destination);
	}

	public List<Vector2> getPossibleDestinations() {
		List<Vector2> possibleMoves = new ArrayList<Vector2>();
		for (Vector2 move : moves) {
			if (!insideBoard(move)) continue;
			possibleMoves.add(position.add(move));
		}

		return possibleMoves;
	}
}