package management;

import pieces.*;
import resources.*;

import java.util.HashMap;

public class Board {
	private static final Piece[] defaultBoard = new Piece[] {
			Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK
	};

    private int size;
	private Player player1, player2;
	private ChessClock clock = null;
	private IChessPiece lastPiece = null;

	HashMap<Vector2, IChessPiece> pieces = new HashMap<Vector2, IChessPiece>();

    /**
     *
     * @param size Using just one size parameter ensures a square board
     * @param useClock Whether or not a chess clock should be used
     * @throws IllegalArgumentException if ({@code nPlayers < 2 || size < 2})   //Pre-conditions
     */
    public Board(int size, boolean useClock) {
    	//TODO Create players
		//TODO Create pieces based on defaultBoard
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

	/**
	 *
	 * @param pos
	 * @return
	 */
	public Alliance getTile(Vector2 pos) {
		// TODO - implement Board.getTile
		throw new UnsupportedOperationException();
	}

	public boolean vacant(Vector2 pos) {
		return pieces.containsKey(pos);
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