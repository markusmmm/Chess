package management;

import pieces.*;
import resources.*;

public class Player {

	private String name;
	private Color color;
	private IChessPiece[] pieces;

	public String getName() {
		return this.name;
	}

	public Color getColor() {
		return this.color;
	}

	/**
	 * 
	 * @param start
	 * @param end
	 * @param board
	 */
	public boolean movePiece(Vector2 start, Vector2 end, Board board) {
		// TODO - implement Player.movePiece
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param color
	 * @param pieces
	 */
	public Player(Color color, Piece[] pieces) {
		// TODO - implement Player.Player
		throw new UnsupportedOperationException();
	}

}