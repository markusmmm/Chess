package resources;

public class Move {
    public final Vector2 start, end;

    public Move(Vector2 start, Vector2 end) {
        this.start = start;
        this.end = end;
    }
    public Move(int x0, int y0, int x1, int y1) {
        start = new Vector2(x0, y0);
        end = new Vector2(x1, y1);
    }
    
    public Vector2 difference() {
        return end.sub(start);
    }

    @Override
    public String toString() {
        return start + " -> " + end;
    }
}
