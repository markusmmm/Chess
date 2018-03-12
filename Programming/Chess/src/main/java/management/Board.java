package management;

import pieces.*;
import resources.*;

public class Board {

    private int size;
	private Player[] players = null;
	private ChessClock clock = null;
	private IChessPiece lastPiece = null;

    /**
     *
     * @param nPlayers
     * @param size Using just one size parameter ensures a square board
     * @param useClock Whether or not a chess clock should be used
     * @throws IllegalArgumentException if ({@code nPlayers < 2 || size < 2})   //Pre-conditions
     */
    public Board(int nPlayers, int size, boolean useClock) {
        // TODO - implement Board.Board
        // TODO - Add pre-condition nPlayers >= 2
        // TODO - Add pre-condition size >= 2
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return size of the square board
     */
    public int getSize() {
        return size;
    }

    /**
     * Calls 'getPiece' on all players, until a match is found (if it exists)
     * @param pos
     * @return Type of piece at the given location (Piece.EMPTY if no match is found)
     */
	public Piece getPiece(Vector2 pos) {
        // TODO - implement Board.getPiece
        throw new UnsupportedOperationException();
	}

	public boolean vacant(Vector2 pos) {
        // TODO - implement Board.getPiece
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return the piece that was last successfully moved
     */
	public IChessPiece getLastPiece() {
		// TODO - implement Board.getLastPiece
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param playerI
	 * @param start
	 * @param end
	 */
	public boolean movePiece(int playerI, Vector2 start, Vector2 end) {
		// TODO - implement Board.movePiece
		throw new UnsupportedOperationException();
	}
}