package pieces;

import resources.*;
import static resources.Alliance.BLACK;
import static resources.Alliance.WHITE;
import management.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pawn extends ChessPiece {

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
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination)
	{
		if(!super.legalMove(destination)) return false;

		return (
			(((whiteNegative2(destination) || whiteNegative(destination) || whiteLDiag(destination) || whiteRDiag(destination)) && this.alliance.equals(WHITE)) ||
			((blackPositive2(destination) || blackPositive(destination) || blackLDiag(destination) || blackRDiag(destination)) && this.alliance.equals(BLACK))) &&
			((noTurnBackBlack(destination) && this.alliance.equals(BLACK)) || (noTurnBackWhite(destination) && this.alliance.equals(WHITE)))
		);
	}

	public boolean whiteNegative(Vector2 move)
	{
		Vector2 white = new Vector2(this.position().getX(), this.position().getY() - 1);
		if (move.equals(white) && board.vacant(move)) {
			this.hasDoubleStepped = true;
			return true;
		}
		return false;
	}

	public boolean whiteNegative2(Vector2 move)
	{
		Vector2 white1 = new Vector2(this.position().getX(), this.position().getY() - 1);
		Vector2 white2 = new Vector2(this.position().getX(), this.position().getY() - 2);
		if (move.equals(white2) && board.vacant(white1) && board.vacant(white2) && !this.hasDoubleStepped) {
			this.hasDoubleStepped = true;
			return true;
		}
		return false;
	}

	public boolean blackPositive(Vector2 move)
	{
		Vector2 black = new Vector2(this.position().getX(), this.position().getY() + 1);
		if (move.equals(black) && board.vacant(move)) {
			this.hasDoubleStepped = true;
			return true;
		}
		return false;
	}

	public boolean blackPositive2(Vector2 move)
	{
		Vector2 black1 = new Vector2(this.position().getX(), this.position().getY() + 1);
		Vector2 black2 = new Vector2(this.position().getX(), this.position().getY() + 2);
		if (move.equals(black2) && board.vacant(black1) && board.vacant(black2) && !this.hasDoubleStepped) {
			this.hasDoubleStepped = true;
			return true;
		}
		return false;
	}

	public boolean whiteLDiag(Vector2 move)
	{
		Vector2 whiteLD = new Vector2(this.position().getX() - 1, this.position().getY() - 1);
		if (!board.vacant(move)) {
			ChessPiece enemy = (ChessPiece) board.getPiece(move);
			if (move.equals(whiteLD) && (!enemy.alliance.equals(this.alliance))) {
				return true;
			}
		}
		return false;
	}

	public boolean whiteRDiag(Vector2 move)
	{
		Vector2 whiteRD = new Vector2(this.position().getX() + 1, this.position().getY() - 1);
		if (!board.vacant(move)) {
			ChessPiece enemy = (ChessPiece) board.getPiece(move);
			if (move.equals(whiteRD) && (!enemy.alliance.equals(this.alliance))) {
				return true;
			}
		}
		return false;
	}

	public boolean blackLDiag(Vector2 move)
	{
		Vector2 blackLD = new Vector2(this.position().getX() - 1, this.position().getY() + 1);
		if (!board.vacant(move)) {
			ChessPiece enemy = (ChessPiece) board.getPiece(move);
			if (move.equals(blackLD) && (!enemy.alliance.equals(this.alliance))) {
				return true;
			}
		}
		return false;
	}

	public boolean blackRDiag(Vector2 move)
	{
		Vector2 blackRD = new Vector2(this.position().getX() + 1, this.position().getY() + 1);
		if (!board.vacant(move)) {
			ChessPiece enemy = (ChessPiece) board.getPiece(move);
			if (move.equals(blackRD) && (!enemy.alliance.equals(this.alliance))) {
				return true;
			}
		}
		return false;
	}

	public boolean noTurnBackWhite(Vector2 move)
	{
		Vector2 turnBackWhite1 = new Vector2(this.position.getX(), this.position.getY() + 1);
		Vector2 turnBackWhite2 = new Vector2(this.position.getX() + 1, this.position.getY() + 1);
		Vector2 turnBackWhite3 = new Vector2(this.position.getX() - 1, this.position.getY() + 1);
		if(move.equals(turnBackWhite1) || move.equals(turnBackWhite2) || move.equals(turnBackWhite3))
		{
			return false;
		}
		return true;
	}

	public boolean noTurnBackBlack(Vector2 move)
	{
		Vector2 turnBackBlack1 = new Vector2(this.position.getX(), this.position.getY() - 1);
		Vector2 turnBackBlack2 = new Vector2(this.position.getX() + 1, this.position.getY() - 1);
		Vector2 turnBackBlack3 = new Vector2(this.position.getX() - 1, this.position.getY() - 1);
		if(move.equals(turnBackBlack1) || move.equals(turnBackBlack2) || move.equals(turnBackBlack3))
		{
			return false;
		}
		return true;
	}

	public Set<Vector2> getPossibleDestinations() {
		Set<Vector2> possibleMoves = new HashSet<>();
		int row = this.position.getX();
		int column = this.position.getY();

		if(this.alliance.equals(WHITE))
		{
			//One step
			if(legalMove(new Vector2(row, column - 1)))
			{
				possibleMoves.add(new Vector2(row, column - 1));
			}
			//First move 2 step
			if(legalMove(new Vector2(row, column - 2)))
			{
				possibleMoves.add(new Vector2(row, column - 1));
				possibleMoves.add(new Vector2(row, column - 2));
			}
			//Take out enemy diagonal to left
			if(legalMove(new Vector2(row - 1, column - 1)))
			{
				possibleMoves.add(new Vector2(row - 1, column - 1));
			}
			//Take out enemy diagonal to right
			if(legalMove(new Vector2(row + 1, column - 1)))
			{
				possibleMoves.add(new Vector2(row + 1, column - 1));
			}
		}
		else if(this.alliance.equals(BLACK))
		{
			//One step
			if(legalMove(new Vector2(row, column + 1)))
			{
				possibleMoves.add(new Vector2(row, column + 1));
			}
			//First move 2 step
			if(legalMove(new Vector2(row, column + 2)))
			{
				possibleMoves.add(new Vector2(row, column + 1));
				possibleMoves.add(new Vector2(row, column + 2));
			}
			//Take out enemy diagonal to left
			if(legalMove(new Vector2(row - 1, column + 1)))
			{
				possibleMoves.add(new Vector2(row - 1, column + 1));
			}
			//take out enemy diagonal to right
			if(legalMove(new Vector2(row + 1, column + 1)))
			{
				possibleMoves.add(new Vector2(row + 1, column + 1));
			}
		}
		return possibleMoves;
	}

	//TODO transformation when pawn gets to enemy line, en passant

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
					//Vector2 blackEnPassantToLeft = new Vector2(this.position().getX() - 1, this.position().getY() + 1);
					//board.movePiece(this.position(), blackEnPassantToLeft);
				}
				else if (this.alliance().equals(WHITE))
				{
					//Vector2 whiteEnPassantToLeft = new Vector2(this.position().getX() - 1, this.position().getY() - 1);
					//board.movePiece(this.position(), whiteEnPassantToLeft);
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
					//Vector2 blackEnPassantToRight = new Vector2(this.position().getX() + 1, this.position().getY() + 1);
					//board.movePiece(this.position(), blackEnPassantToRight);
				}
				else if (this.alliance().equals(WHITE))
				{
					//Vector2 whiteEnPassantToRight = new Vector2(this.position().getX() + 1, this.position().getY() - 1);
					//board.movePiece(this.position(), whiteEnPassantToRight);
				}
			}
		}
	}
}