package pieces;

import resources.*;
import management.*;

import java.util.ArrayList;
import java.util.List;

public class Queen extends ChessPiece {

    private ArrayList<Vector2> possibleMoves = new ArrayList<>();

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
        if(insideBoard(vector) && freePath(vector)) possibleMoves.add(vector);
    }

    private boolean insideBoard(Vector2 vector) {
        return (
                0 <= vector.getX() && vector.getX() < board.getSize() &&
                0 <= vector.getY() && vector.getY() < board.getSize());
    }
}