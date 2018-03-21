package pieces;


import management.*;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Bishop extends ChessPiece {

	public Bishop(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board, false, Piece.BISHOP);
	}
    public Bishop clonePiece() {
        return new Bishop(position, alliance, board);
    }

	/**
	 *
	 * @return a list of all possible moves from this position
	 */


	public Set<Vector2> getPossibleDestinations(){

		Set<Vector2> possibleMoves = new HashSet<Vector2>();

		int row = position.getX();
		int column = position.getY();


		for(int j = column + 1, i = row + 1; j < board.getSize() && i < 8; j++, i++){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
					possibleMoves.add(new Vector2(i, j));
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
			else break;

			}



		for(int j = column - 1, i = row + 1; j > -1 && i < 8; j--, i++){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
				possibleMoves.add(new Vector2(i, j));
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
			else
				break;

		}

		for(int j = column - 1, i = row - 1; j > -1 && i > -1; j--, i--){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
				possibleMoves.add(new Vector2(i, j));
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
			else
				break;

		}

		for(int j = column + 1, i = row - 1; j < board.getSize() && i > -1; j++, i--){
			Vector2 move = new Vector2(i,j);
			if(legalMove(move))
				possibleMoves.add(new Vector2(i, j));
			if(board.getPiece(move) != null){
				if(board.getPiece(move).alliance() != this.alliance) possibleMoves.add(move);
			}
			else
				break;

		}

		return possibleMoves;
	}

	/**
	 *
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination) {
		if(!super.legalMove(destination)) return false;

		return (
			inDiagonals(destination) &&
			freePath(destination)
		);
	}
}