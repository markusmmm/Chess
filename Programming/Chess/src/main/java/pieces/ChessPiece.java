package pieces;

import management.AbstractBoard;
import resources.*;

import java.util.HashSet;

public abstract class ChessPiece extends AbstractChessPiece {

    protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, ActionType actionType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
        super(position, alliance, moves, actionType, board, canJump, piece, value, hasMoved);
    }
    protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, HashSet<Vector2> attacks, ActionType actionType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
        super(position, alliance, moves, attacks, actionType, board, canJump, piece, value, hasMoved);
    }
    protected ChessPiece(Vector2 position, AbstractChessPiece other) { super(position, other); }

    @Override
    /**
     * Checks if the piece can either move to or attack the given destination
     * @param destination End position of attempted move
     * @return If the action is legal
     */
    public boolean legalAction(Vector2 destination) {
        Vector2 delta = destination.sub(position());

        AbstractChessPiece other = board.getPiece(destination);
        boolean validMove = other == null,
                validAttack = other != null && other.alliance() != alliance();

        if(super.legalAction(destination)) {
            if(actionType == ActionType.STEP) {
                return (validMove && moves.contains(delta)) ||
                        (validAttack && attacks.contains(delta));
            } else {
                if(validMove)
                    for (Vector2 move : moves)
                        if (move.isParallelTo(delta))
                            return true;
                if(validAttack)
                    for (Vector2 attack : attacks)
                        if (attack.isParallelTo(delta))
                            return true;
            }
        }

        return false;
    }
}
