package pieces;

import resources.*;
import management.*;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Queen extends ChessPiece {

    private ArrayList<Vector2> possibleMoves = new ArrayList(Vector2);
    private ArrayList<Vector2> legalMoves = new ArrayList(Vector2);

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
		        insideBoard(move) &&
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
            possibleMoves.add(new Vector2(position.getX(), position.getY() + variable));
            possibleMoves.add(new Vector2(position.getX(), position.getY() - variable));
            possibleMoves.add(new Vector2(position.getX() + variable, position.getY()));
            possibleMoves.add(new Vector2(position.getX() - variable, position.getY()));

            //diagonals
            possibleMoves.add(new Vector2(position.getX() + variable, position.getY() + variable));
            possibleMoves.add(new Vector2(position.getX() + variable, position.getY() - variable));
            possibleMoves.add(new Vector2(position.getX() - variable, position.getY() + variable));
            possibleMoves.add(new Vector2(position.getX() - variable, position.getY() - variable));
        }
        for(Vector2 vector: possibleMoves) {
            if(insideBoard(vector) && freePath(vector)) legalMoves.add(vector);
        }
        return legalMoves;
    }

    private boolean insideBoard(Vector2 vector) {
        return (
                0 <= vector.getX() && vector.getX() < board.getSize() &&
                0 <= vector.getY() && vector.getY() < board.getSize());
    }
}