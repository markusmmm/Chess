package pieces;

import resources.*;
import management.*;

import java.util.List;

public class Queen extends ChessPiece<Queen> {

	public Queen(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board, false, Piece.QUEEN);
	}
    public Queen clonePiece() {
        return new Queen(position, alliance, board);
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

	private boolean positiveCoordinates(Vector2 pos) {
	    return 0 <= pos.getX() && 0 <= pos.getY();
    }
}