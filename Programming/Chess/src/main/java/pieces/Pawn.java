package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static resources.Alliance.BLACK;
import static resources.Alliance.WHITE;

public class Pawn extends ChessPiece {

	private boolean hasDoubleStepped = false;

	public Pawn(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved, boolean hasDoubleStepped) {
		super(position, alliance, vectorTools.addAll(alliance == Alliance.BLACK ? Vector2.BOTTOM : Vector2.TOP, new Vector2(0, Tools.allianceDir(alliance) * 2)),
                MoveType.STEP, board, false, Piece.PAWN, 1, hasMoved);

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
	public void loadData(List<Boolean> vals) {
		super.loadData(vals);
		hasDoubleStepped = vals.get(1);
	}

	/**
	 * Checks if this piece can move to the given destination
	 * @param destination End position of the attempted move
     * @return If the move is legal
	 */
	public boolean legalMove(Vector2 destination)
	{
		if(!super.legalMove(destination)) return false;

		AbstractChessPiece other = board.getPiece(destination);

		// Double step
		if(hasMoved() && Math.abs(destination.getY() - position().getY()) == 2)
		    return false;

		boolean validMove = (destination.getX() == position().getX()) == (other == null);

		return enPassant(destination) || validMove;
	}

	@Override
    public boolean move(Vector2 destination, Board board) {
        // EnPassant-check must occur before super.move, as it will update the piece's position
	    boolean enPassant = enPassant(destination);
	    if(!super.move(destination, board)) return false;

	    if(enPassant) {
	        Vector2 attackPos = destination.sub(new Vector2(0, Tools.allianceDir(alliance)));
            board.performAttack(position(), destination, attackPos);
        }

	    return true;
    }

    /**
     *
     * @return Whether this pawn has performed a double stepped during this game
     */
	public boolean hasDoubleStepped() {
		return hasDoubleStepped;
	}

	public Set<Vector2> getPossibleAttacks() {
		Vector2 position = position();

		int dir = Tools.allianceDir(alliance);
		int x = position.getX(), y = position.getY();

		return new HashSet<>(Arrays.asList(
			new Vector2(x-1, y + dir),
			new Vector2(x+1, y + dir)
		));
	}

	public boolean enPassant(Vector2 destination)
	{
	    int dir = Tools.allianceDir(alliance);
	    if(dir == 0) throw new IllegalStateException(this + " is not assigned to an alliance");

		Vector2 delta = destination.sub(position());
		if(Math.abs(delta.getX()) == 1 && delta.getY() == Tools.allianceDir(alliance)) {
		    AbstractChessPiece other = board.getPiece(destination.add(new Vector2(0, -dir)));
		    return other != null &&
                    other.piece() == Piece.PAWN && other.alliance() != alliance() && !((Pawn)other).hasDoubleStepped()
                    && other.equals(board.getLastPiece());
        }
        return false;
	}
}