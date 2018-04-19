package resources;

public class Vector2 implements Comparable<Vector2> {
	private final int x, y;

	public Vector2 (int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Vector2 add(Vector2 v) {
		return new Vector2(x + v.x, y + v.y);
	}

	public Vector2 subtract(Vector2 v) { return new Vector2(x - v.x, y - v.y); }

    /**
     *
     * @return returns the position one step closer to given destination
     */
    public Vector2 stepToward(Vector2 destination) {
		int stepX = this.x;
		int stepY = this.y;

		if (this.x < destination.x) {
		    stepX++;
        } else if (this.x > destination.x){
		    stepX--;
        }

        if (this.y < destination.y) {
            stepY++;
        } else if (this.y > destination.y) {
            stepY--;
        }

        return new Vector2(stepX,stepY);
    }

    /**
     *
     * @return gives distance between two positions
     */
    public int distance(Vector2 destination) {
	    return Math.max(   Math.abs( this.x - destination.x ),   Math.abs( this.y - destination.y )   );
    }

    @Override
	public int hashCode() {
        //This process ensures that two Vector2-objects with the same x and y-values are assigned to the same hash-code.
        //This should also create different hash-codes for symmetric vector pairs (e.g. (2,1) and (1,2))
        int hash = 1;
        hash = hash * 17 + x;
        hash = hash * 31 + y;
    	return hash;
	}

	@Override
    public boolean equals(Object o) {
        Vector2 v = (Vector2)o;

        return x == v.x && y == v.y;
    }

    @Override
	public int compareTo(Vector2 v) {
    	if(y == v.y) {
			if (x == y) return 0;
			return x - v.x;
		}
		return y - v.y;
	}

    @Override
    public String toString() {
    	return "(" + x + "," + y + ")";
	}
}