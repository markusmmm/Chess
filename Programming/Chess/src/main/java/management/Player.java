package management;

import pieces.*;
import resources.*;

import java.awt.*;

public class Player {

	private String name;
	private Color color;
	private IChessPiece[] pieces;

    /**
     * An IChessPiece array is generated, based on 'pieces' and the given board size
     * @param color
     * @param pieces
     * @param boardSize
     */
    public Player(String name, Color color, Piece[] pieces, int boardSize) {
        // TODO - implement Player.Player
        throw new UnsupportedOperationException();
    }

	public String getName() {
		return this.name;
	}

	public Color getColor() {
		return this.color;
	}

    /**
     *
     * @param pos
     * @return
     */
	public Piece getPiece(Vector2 pos) {
        // TODO - implement Player.getPiece
        throw new UnsupportedOperationException();
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
}