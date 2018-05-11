package resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Vector2 implements Comparable<Vector2> {
	private final int x, y;

	public final static Vector2
			NW = new Vector2(-1,-1), N =    new Vector2(0,-1), NE = new Vector2(1,-1),
			W  = new Vector2(-1, 0), ZERO = new Vector2(0, 0), E  = new Vector2(1, 0),
			SW = new Vector2(-1, 1), S =    new Vector2(0, 1), SE = new Vector2(1, 1);

	public final static HashSet<Vector2> TOP = new HashSet<>(Arrays.asList(NW, N, NE));
	public final static HashSet<Vector2> BOTTOM = new HashSet<>(Arrays.asList(SW, S, SE));

	public final static HashSet<Vector2> DIAGONAL = new HashSet<>(Arrays.asList(NE, SE, SW, NW));
    public final static HashSet<Vector2> VERTICAL = new HashSet<>(Arrays.asList(NE, SE, SW, NW));
    public final static HashSet<Vector2> HORIZONTAL = new HashSet<>(Arrays.asList(W, E));
    public final static HashSet<Vector2> STRAIGHT = new HashSet<>(Arrays.asList(N, E, S, W));
    public final static HashSet<Vector2> UNIT = new HashSet<>(Arrays.asList(N, NE, E, SE, S, SW, W, NW));

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

	public Vector2 sub(Vector2 v) { return new Vector2(x - v.x, y - v.y); }

	public Vector2 mult(int m) { return new Vector2(x * m, y * m); }
    public Vector2 mult(Vector2 v) { return new Vector2(x * v.x, y * v.y); }

    public Vector2 abs() { return new Vector2(Math.abs(x), Math.abs(y)); }
    public Vector2 sign() { return new Vector2(Tools.sign(x), Tools.sign(y)); }

	/**
	 * Normalize both vectors, and see if the result is equal
	 * @param v Other vector to check
	 * @return Whether or not the vectors are parallel
	 */
	public boolean isParallelTo(Vector2 v) {
		if(equals(v)) return true;

		double xRatio = x == 0 || v.x == 0 ? 0 : (double)x / v.x,
			   yRatio = y == 0 || v.y == 0 ? 0 : (double)y / v.y;

		return xRatio == yRatio;
    }

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

    public double dot(Vector2 v) {
    	return x * v.x + y * v.y;
	}

	public double magnitude() {
    	return Math.sqrt(x*x + y*y);
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