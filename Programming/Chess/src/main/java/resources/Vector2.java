package resources;

public class Vector2 {
	private int x, y;

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
}