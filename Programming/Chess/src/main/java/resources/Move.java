package resources;

public class Move {
    public final Vector2 start, end;

    public Move(Vector2 start, Vector2 end) {
        this.start = start;
        this.end = end;
    }

    /*@Override
    public String toString() {
        return start + "->" + end;
    }*/
}
