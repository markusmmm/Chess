package resources;

public class Move {
    public final Vector2 start, end;

    public Move(Vector2 start, Vector2 end) {
        this.start = start;
        this.end = end;
    }
    
    public Vector2 getStart(){
    	return this.start;
    }
    
    public Vector2 getEnd(){
    	return this.end;
    }
    ///kommentar

    @Override
    public String toString() {
        return start + " -> " + end;
    }
}
