package pieces;

import main.GameBoard;
import management.AbstractBoard;
import management.PieceManager;
import resources.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pawn extends ChessPiece {

	private boolean hasDoubleStepped = false;

	public Pawn(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved, boolean hasDoubleStepped) {
		super(position, alliance, vectorTools.addAll(alliance == Alliance.BLACK ? Vector2.BOTTOM : Vector2.TOP, new Vector2(0, Tools.allianceDir(alliance) * 2)),
				MoveType.STEP, board, false, Piece.PAWN, hasMoved);

		this.hasDoubleStepped = hasDoubleStepped;
	}
	public Pawn(Pawn other) {
		super(other);
		hasDoubleStepped = other.hasDoubleStepped;
	}

	@Override
	public AbstractChessPiece clonePiece() {
		return new Pawn(this);
	}

	@Override
	public void loadData(List<Boolean> values) {
		super.loadData(values);
		hasDoubleStepped = values.get(1);
	}

	/**
	 * Checks if this piece can move to the given destination
	 * @param destination End position of the attempted move
	 * @return If the move is legal
	 */
	public boolean isLegalMove(Vector2 destination) {
		if(!super.isLegalMove(destination)) return false;
		if(isShadam(destination)) return true; // If move is shadam, it has already been approved by super.isLegalMove

		if((hasMoved() || hasDoubleStepped()) && isDoubleStep(destination)) return false;

		boolean validMove = (destination.getX() == position().getX()) == (board.getPiece(destination) == null);
		return isEnPassant(destination) || validMove;
	}

	@Override
	public boolean move(Vector2 destination) {
		// Checks for move type must occur before super.move, as super.move will update the piece's position
		boolean isDoubleStep = isDoubleStep(destination),
				isEnPassant = isEnPassant(destination),
				isPromotion = isPromotion(destination);

		if(!super.move(destination)) return false;

		if(isDoubleStep)
			hasDoubleStepped = true;
		if(isEnPassant) {
			Vector2 attackPos = destination.sub(new Vector2(0, Tools.allianceDir(alliance)));
			board.performAttack(position(), destination, attackPos);
		} else if(isPromotion) {
			char pieceSymbol = new GameBoard().pawnPromotion().charAt(0);
			board.transformPiece(position(), PieceManager.toPiece(pieceSymbol).piece);
		}

		return true;
	}

	/**
	 * @return Whether or not this pawn has performed a double step during this game
	 */
	public boolean hasDoubleStepped() {
		return hasDoubleStepped;
	}

	@Override
	public Set<Vector2> getPossibleAttacks() {
		Vector2 position = position();

		int dir = Tools.allianceDir(alliance);
		int x = position.getX(), y = position.getY();

		HashSet<Vector2> attacks = new HashSet<>(Arrays.asList(
				new Vector2(x-1, y + dir),
				new Vector2(x+1, y + dir)
		));

		if(board.getMode() == BoardMode.SHADAM)
		    for(Vector2 v : Vector2.DIAGONAL)
		        attacks.add(v.mult(2));

		return attacks;
	}

	/**
	 * @param destination End-position of move to evaluate
	 * @return If the move is an attempted double-step
	 */
	private boolean isDoubleStep(Vector2 destination) {
		return Math.abs(destination.getY() - position().getY()) > 1;
	}

	/**
	 * @param destination End-position of move to evaluate
	 * @return If the move is an attempt of en-passant
	 */
	private boolean isEnPassant(Vector2 destination)
	{
		int dir = Tools.allianceDir(alliance);
		if(dir == 0) throw new IllegalStateException(this + " is not assigned to an alliance");

		Vector2 victimPos = destination.add(new Vector2(0, -dir));
		AbstractChessPiece victim = board.getPiece(victimPos);

		return victim != null && victim.piece() == Piece.PAWN && victim.alliance() != alliance() &&
				((Pawn)victim).hasDoubleStepped() && victim.equals(board.getLastPiece());
	}

	/**
	 * @param destination End-position of move to evaluate
	 * @return If the move is an attempt of pawn promotion
	 */
	private boolean isPromotion(Vector2 destination) {
		int firstRankIndex = Tools.firstRankOfAlliance(otherAlliance());

		return destination.getY() == firstRankIndex &&
				position().getY() == firstRankIndex + Tools.allianceDir(otherAlliance());
	}
}
