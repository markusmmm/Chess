package pieces;

import management.Board;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

		Vector2 delta = destination.subtract(position);
		return moves.contains(delta);
	}

	public Set<Vector2> getPossibleDestinations(String caller) {
		logActionPossibleDestinations(caller);

		Set<Vector2> possibleDestinations = new HashSet<>();

		for (Vector2 move : moves) {
			Vector2 endPos = position.add(move);
			if(legalMove(endPos))
				possibleDestinations.add(endPos);
		}

		return possibleDestinations;
	}
}