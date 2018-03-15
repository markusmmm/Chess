package pieces;

import resources.MoveType;
import resources.Vector2;

public class Move {
    private Vector2 vect;
    private MoveType type;

    public Move(Vector2 vect, MoveType type) {
        this.vect = vect;
        this.type = type;
    }

    public Vector2 getVect() {
        return vect;
    }
    public MoveType getMoveType() {
        return type;
    }
}
