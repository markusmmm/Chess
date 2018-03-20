package pieces;

import resources.*;
import management.*;

import java.util.List;

import static resources.Alliance.BLACK;
import static resources.Alliance.WHITE;

public class Pawn extends ChessPiece<Pawn> {

	public boolean hasDoubleStepped = false;
	public boolean enPassantable = false;
    private Vector2[] attacks = new Vector2[] {};

	/**
	 *
	 * @param position
	 * @param alliance
	 */
	public Pawn(Vector2 position, Alliance alliance, Board board){
		super(position, alliance, board, false, Piece.PAWN);
	}
	public Pawn clonePiece() {
        return new Pawn(position, alliance, board);
    }

	/**
	 * 
	 * @param move
	 */
	public boolean legalMove(Vector2 move) {
		return (
				positiveCoordinates(move) &&
				position.distance(move) == 1 && freePath(move) &&
				(whiteNegative(move) || blackPositive(move))
		);
	}

	public boolean whiteNegative(Vector2 move)
	{
		Vector2 white = new Vector2(this.position().getX(), this.position().getY() - 1);
		if(move.equals(white))
		{
			return true;
		}
		return false;
	}

	public boolean blackPositive(Vector2 move)
	{
		Vector2 black = new Vector2(this.position().getX(), this.position().getY() + 1);
		if(move.equals(black))
		{
			return true;
		}
		return false;
	}

	public List<Vector2> getPossibleMoves() {
		//TODO Pawn.getPossibleMoves
		throw new UnsupportedOperationException();
	}

	public boolean canDoubleStep(Vector2 move)
	{
		if((hasMoved() == false) && (position.distance(move) == 2))
		{
			return true;
		}
		return false;
	}

	public void doubleStep(Vector2 move)
	{
		if(canDoubleStep(move))
		{
			if(this.alliance().equals(WHITE))
			{
				Vector2 wPawnMove = new Vector2(this.position().getX(), this.position().getY() - 2);
				if(wPawnMove.equals(move))
				{
					hasDoubleStepped = true;
					enPassantable = true;
					board.movePiece(this.position(), move);
				}

			}
			else if(this.alliance().equals(BLACK))
			{
				Vector2 bPawnMove = new Vector2(this.position().getX(), this.position().getY() + 2);
				if(bPawnMove.equals(move))
				{
					hasDoubleStepped = true;
					enPassantable = true;
					board.movePiece(this.position(), move);
				}
			}
		}
	}

	public void oneStep(Vector2 move)
	{
		if(this.alliance().equals(WHITE))
		{
			Vector2 wPawnMove = new Vector2(this.position().getX(), this.position().getY() - 1);
			if(wPawnMove.equals(move))
			{
				enPassantable = false;
				board.movePiece(this.position(), move);
			}
		}
		else if(this.alliance().equals(BLACK))
		{
			Vector2 bPawnMove = new Vector2(this.position().getX(), this.position().getY() + 1);
			if(bPawnMove.equals(move))
			{
				enPassantable = false;
				board.movePiece(this.position(), move);
			}
		}
	}

	public void enPassant()
	{
		Vector2 toLeft = new Vector2(this.position().getX() - 1, this.position().getY());
		Vector2 toRight = new Vector2(this.position().getX() + 1, this.position().getY());
		if(!board.vacant(toLeft))
		{
			Pawn enemyPawn = (Pawn) board.getPiece(toLeft);
			if (enemyPawn.hasDoubleStepped && enemyPawn.enPassantable)
			{
				if (this.alliance().equals(BLACK))
				{
					Vector2 blackEnPassantToLeft = new Vector2(this.position().getX() - 1, this.position().getY() + 1);
					board.movePiece(this.position(), blackEnPassantToLeft);
					//DELETE PIECE?!?!
				}
				else if (this.alliance().equals(WHITE))
				{
					Vector2 whiteEnPassantToLeft = new Vector2(this.position().getX() - 1, this.position().getY() - 1);
					board.movePiece(this.position(), whiteEnPassantToLeft);
					//DELETE PIECE?!?!
				}
			}
		}
		else if(!board.vacant(toRight))
		{
			Pawn enemyPawn = (Pawn) board.getPiece(toRight);
			if (enemyPawn.hasDoubleStepped && enemyPawn.enPassantable)
			{
				if (this.alliance().equals(BLACK))
				{
					Vector2 blackEnPassantToRight = new Vector2(this.position().getX() + 1, this.position().getY() + 1);
					board.movePiece(this.position(), blackEnPassantToRight);
					//DELETE PIECE?!?!
				}
				else if (this.alliance().equals(WHITE))
				{
					Vector2 whiteEnPassantToRight = new Vector2(this.position().getX() + 1, this.position().getY() - 1);
					board.movePiece(this.position(), whiteEnPassantToRight);
					//DELETE PIECE?!?!
				}
			}
		}
	}

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}
}