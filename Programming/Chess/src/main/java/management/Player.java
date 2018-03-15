package management;

import pieces.*;
import resources.*;

import java.awt.*;

public class Player {

	private String name;
	private Alliance alliance;

	private Board board;

    /**
     * An IChessPiece array is generated, based on 'pieces' and the given board size
     * @param alliance
     * @param pieces
     * @param board
     */
    public Player(String name, Alliance alliance, Piece[] pieces, Board board) {
        // TODO - implement Player.Player
        throw new UnsupportedOperationException();
    }

	public String getName() {
		return this.name;
	}

	public Alliance getAlliance() { return alliance; }

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
	 */
	public boolean movePiece(Vector2 start, Vector2 end) {
		// TODO - implement Player.movePiece
		throw new UnsupportedOperationException();
	}
}