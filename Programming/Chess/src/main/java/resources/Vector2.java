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

    public Vector2 stepToward(Vector2 destination) {
		int stepX = this.x;
		int stepY = this.y;

		if (this.x < destination.x) {
		    stepX++;
        } else {
		    stepX--;
        }

        if (this.y < destination.y) {
            stepY++;
        } else {
            stepY--;
        }
        return new Vector2(stepX,stepY);
    }
}