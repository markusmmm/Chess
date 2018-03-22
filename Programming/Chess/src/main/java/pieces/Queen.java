package pieces;

import management.Board;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public class Queen extends ChessPiece {

    private Set<Vector2> possibleMoves = new HashSet<>();

    public Queen(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board, false, Piece.QUEEN, 9);
	}
    public Queen clonePiece() {
        return new Queen(position, alliance, board);
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

	public Set<Vector2> getPossibleDestinations(String caller) {
        logActionPossibleDestinations(caller);

	    possibleMoves.clear();
        for (int variable = 0; variable < board.getSize(); variable++) {
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