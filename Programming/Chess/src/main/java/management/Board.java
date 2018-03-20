package management;

import pieces.*;
import resources.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Board {
	private static final Piece[] defaultBoard = new Piece[] {
			Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
			Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN
	};

    private final int size;
	private Player player1, player2;
	private ChessClock clock = null;
	private ChessPiece lastPiece = null;

	public HashMap<Vector2, ChessPiece> pieces = new HashMap<Vector2, ChessPiece>();
	public HashSet<ChessPiece> inactivePieces = new HashSet<ChessPiece>();

    /**
     *
     * @param size Using just one size parameter ensures a square board
     * @param useClock Whether or not a chess clock should be used
     * @throws IllegalArgumentException if ({@code size < 2})   //Pre-conditions
     */
    public Board(int size, boolean useClock) {
    	if(size < 2) throw new IllegalArgumentException("The board size must be at least 2");

    	int p = 0;
		this.size = size;

		if(useClock) {
			clock = new ChessClock(2, 900, 12, -1);
		}

    	for(Piece type : defaultBoard) {
    		int x = p % size;
    		int y = p / size;

    		Vector2 pos = new Vector2(x, y);
    		Vector2 invPos = new Vector2(x, size - y - 1);

    		pieces.put(pos, createPiece(pos, type, Alliance.BLACK));
    		pieces.put(invPos, createPiece(invPos, type, Alliance.WHITE));
    		System.out.println(pos + ": " + pieces.get(pos));
			System.out.println(invPos + ": " + pieces.get(invPos));

    		p++;
    	}


    }

    private ChessPiece createPiece(Vector2 pos, Piece type, Alliance alliance) {
    	switch (type) {
			case BISHOP:
				return new Bishop(pos, alliance, this);
			case KNIGHT:
				return new Knight(pos, alliance, this);
			case QUEEN:
				return new Queen(pos, alliance, this);
			case KING:
				return new King(pos, alliance, this);
			case PAWN:
				return new Pawn(pos, alliance, this);
			case ROOK:
				return new Rook(pos, alliance, this);
		}
		return null;
	}

	public boolean transformPiece(Vector2 pos, Piece newType) {
    	ChessPiece piece = pieces.get(pos);
    	if(piece == null) return false;

    	pieces.remove(pos);

    	ChessPiece newPiece = createPiece(pos, newType, piece.alliance());
    	newPiece.syncContent(piece);

    	pieces.put(pos, newPiece);

    	return true;
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
	public IChessPiece getPiece(Vector2 pos) {
        if(vacant(pos)) return null;
		return pieces.get(pos).clonePiece();
	}
	public HashMap<Vector2, IChessPiece> getPieces(Alliance alliance) {
		HashMap<Vector2, IChessPiece> temp = new HashMap<>();

		for(Vector2 pos : pieces.keySet()) {
			IChessPiece piece = pieces.get(pos);
			if(piece.alliance().equals(alliance))
				temp.put(pos, piece);
		}

		return temp;
	}

	public boolean vacant(Vector2 pos) {
		return !pieces.containsKey(pos);
    }

    /**
     *
     * @return the piece that was last successfully moved
     */
	public IChessPiece getLastPiece() {
		return lastPiece;
	}

	/**
	 *
	 * @param start
	 * @param end
	 */
	public boolean movePiece( Vector2 start, Vector2 end) {
		ChessPiece piece = pieces.get(start);

		if(piece == null) return false;
		if(!piece.move(end)) return false;

		lastPiece = piece;
		IChessPiece endPiece = pieces.get(end);

		if(endPiece != null) {
			if(piece.alliance().equals(endPiece.alliance()))
				throw new IllegalStateException("An illegal move has been approved by " + piece.alliance() + ".legalMove()");

			removePiece(end);
		}

		pieces.remove(start);
		pieces.put(end, piece);

		return true;
	}

	private boolean removePiece(Vector2 pos) {
		if(pieces.containsKey(pos)) return false;

		ChessPiece piece = pieces.get(pos);
		pieces.remove(pos);
		inactivePieces.add(piece);

		return true;
	}
}