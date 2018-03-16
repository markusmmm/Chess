package pieces;

import resources.*;
import management.*;

import java.util.List;

public class Pawn extends ChessPiece {

	private final boolean canJump;
	private final Piece piece;
	private Vector2 position;
	public boolean hasDoubleStepped = false;
    private Vector2[] attacks = new Vector2[] {};

	/**
	 *
	 * @param position
	 * @param alliance
	 */
	public Pawn(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board);
		canJump = false;
		piece = Piece.PAWN;
		this.position = position;
	}

	public Piece piece() {
		return piece;
	}

	/**
	 * 
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		// TODO - implement Pawn.legalMove
		throw new UnsupportedOperationException();
	}

	public List<Vector2> getPossibleMoves() {
		//TODO Pawn.getPossibleMoves
		throw new UnsupportedOperationException();
	}

	public boolean canJump() {
		// TODO - implement Pawn.canJump
		throw new UnsupportedOperationException();
	}

	public boolean canDoubleStep()
	{
		if(hasMoved() == false)
		{
			return true;
		}
		return false;
	}

	/*public boolean clearPathOneStep() {
		if(board.vacant(move))
		{
			return true;
		}
		return false;
	}*/

	public boolean clearPathTwoStep(){return false;}

	/*public boolean(Vector2 move, Board board)
	{
		if(canDoubleStep())
		{
			Vector2 firstStep = new Vector2(move.getX(), move.getY());
			if(board.vacant(move))
			{
				return true;
			}
		}
		return false;
	}
	*/


	public Pawn clone() {
		return new Pawn(position, alliance, board);
	}
}