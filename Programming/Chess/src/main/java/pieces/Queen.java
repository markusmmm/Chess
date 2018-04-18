package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public class Queen extends ChessPiece {

    private Set<Vector2> possibleMoves = new HashSet<>();

    public Queen(Vector2 position, Alliance alliance, AbstractBoard board){
		super(position, alliance, board, false, Piece.QUEEN, 9,false);
	}
    public Queen(Queen other) {
        super(other);
    }

    @Override
    public ChessPiece clonePiece() {
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

	public Set<Vector2> getPossibleDestinations() {
	    possibleMoves.clear();
	    Vector2 position = position();

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
        return possibleMoves;
    }
    private void evalMove(Vector2 vector) {
        if(legalMove(vector)) possibleMoves.add(vector);
    }
}