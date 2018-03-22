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

	@Override
	public boolean move(Vector2 destination) {
		boolean whiteNegative2 = whiteNegative2(destination) && alliance == alliance.WHITE;
		boolean blackPositive2 = blackPositive2(destination) && alliance == alliance.BLACK;

		if(!super.move(destination)) return false;

		if(!hasDoubleStepped && (whiteNegative2 || blackPositive2))
			hasDoubleStepped = true;

		return true;
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
		if(hasMoved()) return false;

		Vector2 white1 = new Vector2(this.position().getX(), this.position().getY() - 1);
		Vector2 white2 = new Vector2(this.position().getX(), this.position().getY() - 2);
		return move.equals(white2) && board.vacant(white1) && board.vacant(white2);
	}

	public boolean blackPositive(Vector2 move)
	{
		Vector2 black = new Vector2(this.position().getX(), this.position().getY() + 1);
		return move.equals(black) && board.vacant(move);
	}

	public boolean blackPositive2(Vector2 move)
	{
		if(hasMoved()) return false;

		Vector2 black1 = new Vector2(this.position().getX(), this.position().getY() + 1);
		Vector2 black2 = new Vector2(this.position().getX(), this.position().getY() + 2);
		return move.equals(black2) && board.vacant(black1) && board.vacant(black2);
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
		int x = this.position.getX();
		int y = this.position.getY();

		if(this.alliance.equals(WHITE))
		{
			//First move 2 step
			if(legalMove(new Vector2(x, y - 2)))
			{
				possibleMoves.add(new Vector2(x, y - 2));
			}
			//One step
			if(legalMove(new Vector2(x, y - 1)))
			{
				possibleMoves.add(new Vector2(x, y - 1));
			}
			//Take out enemy diagonal to left
			if(legalMove(new Vector2(x - 1, y - 1)))
			{
				possibleMoves.add(new Vector2(x - 1, y - 1));
			}
			//Take out enemy diagonal to right
			if(legalMove(new Vector2(x + 1, y - 1)))
			{
				possibleMoves.add(new Vector2(x + 1, y - 1));
			}
			if(enPassant(new Vector2(x - 1, y)))
			{
				possibleMoves.add(new Vector2(x - 1, y - 1));
			}
			if(enPassant(new Vector2(x + 1, y)))
			{
				possibleMoves.add(new Vector2(x + 1, y - 1));
			}
		}
		else if(this.alliance.equals(BLACK))
		{
			//First move 2 step
			if(legalMove(new Vector2(x, y + 2)))
			{
				possibleMoves.add(new Vector2(x, y + 2));
			}
			//One step
			if(legalMove(new Vector2(x, y + 1)))
			{
				possibleMoves.add(new Vector2(x, y + 1));
			}
			//Take out enemy diagonal to left
			if(legalMove(new Vector2(x - 1, y + 1)))
			{
				possibleMoves.add(new Vector2(x - 1, y + 1));
			}
			//take out enemy diagonal to right
			if(legalMove(new Vector2(x + 1, y + 1)))
			{
				possibleMoves.add(new Vector2(x + 1, y + 1));
			}
			if(enPassant(new Vector2(x - 1, y)))
			{
				possibleMoves.add(new Vector2(x - 1, y + 1));
			}
			if(enPassant(new Vector2(x + 1, y)))
			{
				possibleMoves.add(new Vector2(x + 1, y + 1));
			}
		}
		return possibleMoves;
	}

	//TODO transformation when pawn gets to enemy line, en passant

	public boolean enPassant(Vector2 side)
	{
		if (!board.vacant(side))
		{
			if((this.alliance.equals(WHITE) && this.position.getY() == 3) || (this.alliance.equals(BLACK) && this.position.getY() == 4))
			{
				IChessPiece otherPiece = board.getPiece(side);
				if(!otherPiece.piece().equals(Piece.PAWN))
					return false;

				Pawn possibleEnemyPawn = (Pawn) otherPiece;
				if (possibleEnemyPawn.hasDoubleStepped && (!possibleEnemyPawn.alliance.equals(this.alliance)) && board.getLastPiece().position().equals(possibleEnemyPawn))
				{
					return true;
				}
			}
		}
		return false;
	}
}