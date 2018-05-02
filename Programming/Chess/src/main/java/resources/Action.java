package resources;

import management.Board;
import pieces.ChessPiece;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

public class Action extends Tuple<ChessPiece, Vector2> {
    public enum Type { STEP, LINE }

    public Action(ChessPiece piece, Vector2 dir) {
        super(piece, dir);
    }

    public Set<Vector2> evaluate() {
        throw new NotImplementedException();

        /*
        Board board = fst.getBoard();
        Vector2 current = fst.position();
        final int n = board.size();

        while(current.getX() >= 0 && current.getX() < n && current.getY() >= 0 && current.getY() < n) {
            Vector2 next = current.add(snd);

            //if(board.)
        }
        */
    }
}
