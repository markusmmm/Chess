package pieces.movement;

import resources.Vector2;

public class HostileMove extends Rule {
    public HostileMove(Vector2 move) {
        super(move, move);
    }
}
