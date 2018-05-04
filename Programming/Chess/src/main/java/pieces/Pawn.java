package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Pawn extends ChessPiece {

	private boolean hasDoubleStepped = false;

	public Pawn(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved, boolean hasDoubleStepped) {
		super(position, alliance,
                new HashSet<>(Arrays.asList(new Vector2(0,Tools.allianceDir(alliance)), new Vector2(0,Tools.allianceDir(alliance)*2))),
                new HashSet<>(Arrays.asList(new Vector2(-1,Tools.allianceDir(alliance)), new Vector2(1,Tools.allianceDir(alliance)))),
                ActionType.STEP, board, false, Piece.PAWN, 1, hasMoved);

		if(Tools.allianceDir(alliance) == 0) throw new IllegalStateException(this + " was not correctly assigned to an alliance");
		this.hasDoubleStepped = hasDoubleStepped;
	}
    private Pawn(Vector2 position, Pawn other, boolean hasDoubleStepped) {
	    super(position, other);
	    this.hasDoubleStepped = hasDoubleStepped;
	}

	@Override
	public AbstractChessPiece clonePiece(Vector2 position) {
		return new Pawn(position, this, hasDoubleStepped);
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
	public boolean legalAction(Vector2 destination)
	{
		if(!super.legalAction(destination)) return false;

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
     * @return Whether this pawn has performed a double-step during this game
     */
	public boolean hasDoubleStepped() {
		return hasDoubleStepped;
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