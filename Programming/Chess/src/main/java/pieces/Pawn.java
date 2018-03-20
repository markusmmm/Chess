package pieces;

import resources.*;
import static resources.Alliance.BLACK;
import static resources.Alliance.WHITE;
import management.*;

import java.util.ArrayList;
import java.util.List;

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
	 * @param move
	 */
	public boolean legalMove(Vector2 move)
	{
		return (
				positiveCoordinates(move) &&
				((whiteNegative2(move) || whiteNegative(move) || whiteLDiag(move) || whiteRDiag(move)) ||
				(blackPositive2(move) || blackPositive(move) || blackLDiag(move) || blackRDiag(move))) &&
				noTurnBack(move)
		);
	}

	public boolean whiteNegative(Vector2 move)
	{
		Vector2 white = new Vector2(this.position().getX(), this.position().getY() - 1);
		if (move.equals(white) && board.vacant(move)) {
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

	public boolean noTurnBack(Vector2 move)
	{
		Vector2 turnBackWhite1 = new Vector2(this.position.getX(), this.position.getY() + 1);
		Vector2 turnBackWhite2 = new Vector2(this.position.getX() + 1, this.position.getY() + 1);
		Vector2 turnBackWhite3 = new Vector2(this.position.getX() - 1, this.position.getY() + 1);
		Vector2 turnBackBlack1 = new Vector2(this.position.getX(), this.position.getY() - 1);
		Vector2 turnBackBlack2 = new Vector2(this.position.getX() + 1, this.position.getY() - 1);
		Vector2 turnBackBlack3 = new Vector2(this.position.getX() - 1, this.position.getY() - 1);
		if(move.equals(turnBackWhite1) || move.equals(turnBackWhite2) || move.equals(turnBackWhite3) ||
			move.equals(turnBackBlack1) || move.equals(turnBackBlack2) || move.equals(turnBackBlack3))
		{
			return false;
		}
		return true;
	}

	public List<Vector2> getPossibleMoves() {
		List<Vector2> possibleMoves = new ArrayList<>();
		int row = this.position.getX();
		int column = this.position.getY();

		if(this.alliance.equals(WHITE))
		{
			if(legalMove(new Vector2(row, column - 1)))
			{
				possibleMoves.add(new Vector2(row, column - 1));
			}
			if(legalMove(new Vector2(row, column - 2)))
			{
				possibleMoves.add(new Vector2(row, column - 2));
			}
			if(legalMove(new Vector2(row - 1, column - 1)))
			{
				possibleMoves.add(new Vector2(row - 1, column - 1));
			}
			if(legalMove(new Vector2(row + 1, column - 1)))
			{
				possibleMoves.add(new Vector2(row + 1, column - 1));
			}
		}
		else if(this.alliance.equals(BLACK))
		{
			if(legalMove(new Vector2(row, column + 1)))
			{
				possibleMoves.add(new Vector2(row, column + 1));
			}
			if(legalMove(new Vector2(row, column + 2)))
			{
				possibleMoves.add(new Vector2(row, column + 2));
			}
			if(legalMove(new Vector2(row - 1, column + 1)))
			{
				possibleMoves.add(new Vector2(row - 1, column + 1));
			}
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

	private boolean positiveCoordinates(Vector2 pos) {
		return 0 <= pos.getX() && 0 <= pos.getY();
	}
}