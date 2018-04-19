package pieces;

import management.AbstractBoard;
import management.Board;
import resources.Alliance;
import resources.Console;
import resources.Piece;
import resources.Vector2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static resources.Alliance.BLACK;
import static resources.Alliance.WHITE;

public class Pawn extends ChessPiece {

	private boolean hasDoubleStepped = false;

	/**
	 *
	 * @param alliance
	 */


	public Pawn(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved, boolean hasDoubleStepped) {
		super(position, alliance, board, false, Piece.PAWN, 1, hasMoved);
		this.hasDoubleStepped = hasDoubleStepped;
	}
	public Pawn(Pawn other) {
		super(other);
		hasDoubleStepped = other.hasDoubleStepped;
	}

	@Override
	public ChessPiece clonePiece() {
		return new Pawn(this);
	}

	@Override
	public void loadData(List<Boolean> vals) {
		super.loadData(vals);
		hasDoubleStepped = vals.get(1);
	}

	/**
	 * 
	 * @param destination
	 */
	public boolean legalMove(Vector2 destination)
	{
		if(!super.legalMove(destination)) return false;

		int x = destination.getX();
		int y = destination.getY();


		Vector2 blackEnpasant = new Vector2(x , y - 1);
		Vector2 whiteEnpasant = new Vector2(x, y + 1);

		boolean blackResult = this.alliance.equals(BLACK) && enPassant(blackEnpasant);
		boolean whiteResult = this.alliance.equals(WHITE) && enPassant(whiteEnpasant);

		//resources.Console.println("Black result: " + blackResult);

		//resources.Console.println("White result: " + whiteResult);

		return (
				((blackResult ||
						(whiteResult)))  ||
						(((whiteNegative2(destination)  || whiteNegative(destination) ||  whiteLDiag(destination) || whiteRDiag(destination)) && this.alliance.equals(WHITE)) ||
			((blackPositive2(destination) || blackPositive(destination) || blackLDiag(destination) || blackRDiag(destination)) && this.alliance.equals(BLACK))) &&
			((noTurnBackBlack(destination) && this.alliance.equals(BLACK)) || (noTurnBackWhite(destination) && this.alliance.equals(WHITE)))
		);
	}


	@Override
	public boolean move(Vector2 destination, Board board) {
		this.board = board;

		Vector2 start = position();
		Vector2 blackEnpasant = new Vector2(destination.getX() , destination.getY() - 1);
		Vector2 whiteEnpasant = new Vector2(destination.getX(), destination.getY() + 1);

		boolean whiteNegative2 = whiteNegative2(destination) && alliance == alliance.WHITE;
		boolean blackPositive2 = blackPositive2(destination) && alliance == alliance.BLACK;

		boolean blackResult = this.alliance.equals(BLACK) && enPassant(blackEnpasant);
		boolean whiteResult = this.alliance.equals(WHITE) && enPassant(whiteEnpasant);

		if(!super.move(destination, board)) return false;

		if(!hasDoubleStepped && (whiteNegative2 || blackPositive2))
			hasDoubleStepped = true;

		if(blackResult) {
			board.performAttack(start, destination, blackEnpasant);
		}
		else if(whiteResult) {
			Console.println(whiteEnpasant);
			board.performAttack(start, destination, whiteEnpasant);
		}


		return true;
	}

	public boolean hasDoubleStepped() {
		return hasDoubleStepped;
	}

	public boolean withinBoard(Vector2 move)
	{
		int moveGetX = move.getX();
		int moveGetY = move.getY();
		return (moveGetX >= 0 && moveGetX <= 7 && moveGetY >= 0 && moveGetY <= 7);

	}

	public boolean whiteNegative(Vector2 move)
	{
		if(!withinBoard(move)) return false;
		Vector2 white = new Vector2(this.position().getX(), this.position().getY() - 1);
		return move.equals(white) && board.vacant(move);
	}

	public boolean whiteNegative2(Vector2 move)
	{
		if(!withinBoard(move)) return false;
		if(hasMoved()) return false;

		Vector2 white1 = new Vector2(this.position().getX(), this.position().getY() - 1);
		Vector2 white2 = new Vector2(this.position().getX(), this.position().getY() - 2);
		return move.equals(white2) && board.vacant(white1) && board.vacant(white2);
	}

	public boolean blackPositive(Vector2 move)
	{
		if(!withinBoard(move)) return false;
		Vector2 black = new Vector2(this.position().getX(), this.position().getY() + 1);
		return move.equals(black) && board.vacant(move);
	}

	public boolean blackPositive2(Vector2 move)
	{
		if(!withinBoard(move)) return false;
		if(hasMoved()) return false;

		Vector2 black1 = new Vector2(this.position().getX(), this.position().getY() + 1);
		Vector2 black2 = new Vector2(this.position().getX(), this.position().getY() + 2);
		return move.equals(black2) && board.vacant(black1) && board.vacant(black2);
	}

	public boolean whiteLDiag(Vector2 move)
	{
		if(!withinBoard(move)) return false;
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
		if(!withinBoard(move)) return false;
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
		if(!withinBoard(move)) return false;
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
		if(!withinBoard(move)) return false;
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
		Vector2 position = position();

		if(!withinBoard(move)) return false;
		Vector2 turnBackWhite1 = new Vector2(position.getX(), position.getY() + 1);
		Vector2 turnBackWhite2 = new Vector2(position.getX() + 1, position.getY() + 1);
		Vector2 turnBackWhite3 = new Vector2(position.getX() - 1, position.getY() + 1);
		if(move.equals(turnBackWhite1) || move.equals(turnBackWhite2) || move.equals(turnBackWhite3))
			return false;
		return true;
	}

	public boolean noTurnBackBlack(Vector2 move)
	{
		if(!withinBoard(move)) return false;
		Vector2 position = position();

		Vector2 turnBackBlack1 = new Vector2(position.getX(), position.getY() - 1);
		Vector2 turnBackBlack2 = new Vector2(position.getX() + 1, position.getY() - 1);
		Vector2 turnBackBlack3 = new Vector2(position.getX() - 1, position.getY() - 1);
		if(move.equals(turnBackBlack1) || move.equals(turnBackBlack2) || move.equals(turnBackBlack3))
			return false;
		return true;
	}

	public Set<Vector2> getPossibleDestinations() {
		Set<Vector2> possibleMoves = new HashSet<>();

		Vector2 position = position();

		int x = position.getX();
		int y = position.getY();

		if(this.alliance.equals(WHITE))
		{
			Vector2 end = new Vector2(x, y - 2);

			//First move 2 step
			if(legalMove(end))
				possibleMoves.add(new Vector2(x, y - 2));
			//One step
			if(legalMove(new Vector2(x, y - 1)))
				possibleMoves.add(new Vector2(x, y - 1));
			//Take out enemy diagonal to left
			if(legalMove(new Vector2(x - 1, y - 1)))
				possibleMoves.add(new Vector2(x - 1, y - 1));
			//Take out enemy diagonal to right
			if(legalMove(new Vector2(x + 1, y - 1)))
				possibleMoves.add(new Vector2(x + 1, y - 1));
			if(enPassant(new Vector2(x - 1, y)))
				possibleMoves.add(new Vector2(x - 1, y - 1));
			if(enPassant(new Vector2(x + 1, y)))
				possibleMoves.add(new Vector2(x + 1, y - 1));
			if(promotion(new Vector2(x, y +1)))
				possibleMoves.add(new Vector2(x, y - 1));
		}
		else if(this.alliance.equals(BLACK))
		{
			//First move 2 step
			if(legalMove(new Vector2(x, y + 2)))
				possibleMoves.add(new Vector2(x, y + 2));
			//One step
			if(legalMove(new Vector2(x, y + 1)))
				possibleMoves.add(new Vector2(x, y + 1));
			//Take out enemy diagonal to left
			if(legalMove(new Vector2(x - 1, y + 1)))
				possibleMoves.add(new Vector2(x - 1, y + 1));
			//take out enemy diagonal to right
			if(legalMove(new Vector2(x + 1, y + 1)))
				possibleMoves.add(new Vector2(x + 1, y + 1));
			if(enPassant(new Vector2(x - 1, y)))
				possibleMoves.add(new Vector2(x - 1, y + 1));
			if(enPassant(new Vector2(x + 1, y)))
				possibleMoves.add(new Vector2(x + 1, y + 1));
			if(promotion(new Vector2(x, y +1)))
				possibleMoves.add(new Vector2(x, y + 1));
		}

		int startY = 1;
		int dir = alliance == Alliance.BLACK ? 1 : -1;
		if(alliance == Alliance.WHITE) startY = board.size() - 1 - startY;

		if(position.getY() != startY)
			possibleMoves.remove(new Vector2(position.getX(), position.getY() + dir * 2));

		return possibleMoves;
	}

	public Set<Vector2> getPossibleAttacks() {
		Vector2 position = position();


		int dir = alliance == Alliance.BLACK ? 1 : -1;
		int x = position.getX(), y = position.getY();

		return new HashSet<Vector2>(Arrays.asList(
			new Vector2(x-1, y + dir),
			new Vector2(x+1, y + dir)
		));
	}

	//TODO transformation when pawn gets to enemy line, en passant

	public boolean enPassant(Vector2 side)
	{
		if(!withinBoard(side)) return false;
		if (!board.vacant(side))
		{
			Vector2 position = position();

			if((this.alliance.equals(WHITE) && position.getY() == 3) || (alliance.equals(BLACK) && position.getY() == 4))
			{
				IChessPiece otherPiece = board.getPiece(side);
				if(!otherPiece.piece().equals(Piece.PAWN))
					return false;

				Pawn possibleEnemyPawn = (Pawn) otherPiece;
				if (possibleEnemyPawn.hasDoubleStepped && (!possibleEnemyPawn.alliance.equals(alliance)) && board.getLastPiece().position().equals(possibleEnemyPawn.position()))
					return true;
			}
		}
		return false;
	}

	public boolean promotion(Vector2 move)
	{
		if(!withinBoard(move)) return false;
		if(board.vacant(move))
		{
			if (position().getY() == 0 && this.alliance.equals(WHITE))
				return true;
			else if (this.position().getY() == 7 && this.alliance.equals(BLACK))
				return true;
		}
		return false;
	}
}