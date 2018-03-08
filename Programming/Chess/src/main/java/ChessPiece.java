import java.util.ArrayList;
import java.util.Vector;

public abstract class ChessPiece implements IChessPiece {
    private Vector2 position;
    protected ArrayList<Vector2> moveLog = new ArrayList<Vector2>();
    private boolean moved = false;

    /**
     * @return if piece has been moved from it's start position
     */
    public boolean hasMoved() {
        return moved;
    }

    /**
     *
     * @param move new position
     * @return if piece was moved
     */
    public boolean move(Vector2 move, Board board) {
        if (legalMove(move, board)) {
            position = new Vector2(move.getX(), move.getY());
            moved = true;
            return true;
        }
        return false;
    }
}
