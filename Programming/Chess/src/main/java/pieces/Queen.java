package pieces;

import resources.*;
import management.*;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Queen extends ChessPiece {

    private ArrayList<Vector2> possibleMoves = new ArrayList(Vector2);

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
	    possibleMoves.clear();
	    Vector2 mem;
        for (int variable = 0; variable < board.getSize(); variable++) {
            //Straights
            mem = new Vector2(position.getX(), position.getY() + variable);
            if(legalMove(mem)) possibleMoves.add(mem);
            mem = new Vector2(position.getX(), position.getY() - variable);
            if(legalMove(mem)) possibleMoves.add(mem);
            mem = new Vector2(position.getX() + variable, position.getY());
            if(legalMove(mem)) possibleMoves.add(mem);
            mem = new Vector2(position.getX() - variable, position.getY());
            if(legalMove(mem)) possibleMoves.add(mem);

            //diagonals
            mem = new Vector2(position.getX() + variable, position.getY() + variable);
            if(legalMove(mem)) possibleMoves.add(mem);
            mem = new Vector2(position.getX() + variable, position.getY() - variable);
            if(legalMove(mem)) possibleMoves.add(mem);
            mem = new Vector2(position.getX() - variable, position.getY() + variable);
            if(legalMove(mem)) possibleMoves.add(mem);
            mem = new Vector2(position.getX() - variable, position.getY() - variable);
            if(legalMove(mem)) possibleMoves.add(mem);
        }
        return possibleMoves;
    }

	private boolean positiveCoordinates(Vector2 pos) {
	    return 0 <= pos.getX() && 0 <= pos.getY();
    }
}