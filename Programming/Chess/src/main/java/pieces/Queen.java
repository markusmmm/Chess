package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public class Queen extends ChessPiece {
    public Queen(Vector2 position, Alliance alliance, AbstractBoard board){
		super(position, alliance, board, false, Piece.QUEEN, 9);
	}

	private Queen(Queen other) {
        super(other);
    }
    public Queen clonePiece() {
        return new Queen(this);
    }

	/**
	 * 
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
        if(!super.legalMove(destination)) return false;

		return (
            (inDiagonals(destination) || inStraights(destination)) &&
            freePath(destination)
        );
	}

	protected void calculatePossibleDestinations() {
        for (int variable = 0; variable < board.size(); variable++) {
            //Straights
            evalMove(new Vector2(position.getX(), position.getY() + variable));
            evalMove(new Vector2(position.getX(), position.getY() - variable));
            evalMove(new Vector2(position.getX() + variable, position.getY()));
            evalMove(new Vector2(position.getX() - variable, position.getY()));
            //diagonals
            evalMove(new Vector2(position.getX() + variable, position.getY() + variable));
            evalMove(new Vector2(position.getX() + variable, position.getY() - variable));
            evalMove(new Vector2(position.getX() - variable, position.getY() + variable));
            evalMove(new Vector2(position.getX() - variable, position.getY() - variable));
        }
    }
    private void evalMove(Vector2 vector) {
        if(legalMove(vector)) destinationBuffer.add(vector);
    }
}