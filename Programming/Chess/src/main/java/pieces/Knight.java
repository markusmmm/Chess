package pieces;

import resources.*;

import java.util.*;

import management.*;

public class Knight  extends ChessPiece {

	Set<Vector2> moves = new HashSet<>(Arrays.asList(
			new Vector2( 2, 1), new Vector2( 1, 2),
			new Vector2(-2, 1), new Vector2(-1, 2),
			new Vector2( 2,-1), new Vector2( 1,-2),
			new Vector2(-2,-1), new Vector2(-1,-2)));

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
		if(!super.legalMove(destination)) return false;

		return moves.contains(destination);
	}

	public Set<Vector2> getPossibleDestinations() {
		Set<Vector2> possibleDestinations = new HashSet<>();

		for (Vector2 move : moves) {
			Vector2 endPos = position.add(move);
			if(legalMove(endPos))
				possibleDestinations.add(endPos);
		}

		return possibleDestinations;
	}
}