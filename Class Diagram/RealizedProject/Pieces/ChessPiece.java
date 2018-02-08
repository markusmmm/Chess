package Pieces;

public abstract class ChessPiece implements IChessPiece {

	protected Color color;
	protected Vector2 position;
	protected List<Vector2> moveLog = new ArrayList<>();
	private int hasMoved;

	public Color getColor() {
		return this.color;
	}

	public Vector2 getPosition() {
		return this.position;
	}

	public int getHasMoved() {
		return this.hasMoved;
	}

	public String name() {
		// TODO - implement ChessPiece.name
		throw new UnsupportedOperationException();
	}

	public boolean legalMove() {
		// TODO - implement ChessPiece.legalMove
		throw new UnsupportedOperationException();
	}

	public boolean canJump() {
		// TODO - implement ChessPiece.canJump
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param move
	 */
	public boolean move(Vector2 move) {
		// TODO - implement ChessPiece.move
		throw new UnsupportedOperationException();
	}

	public void remove() {
		// TODO - implement ChessPiece.remove
		throw new UnsupportedOperationException();
	}

}