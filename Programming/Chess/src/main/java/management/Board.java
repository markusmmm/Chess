package management;

import pieces.*;
import resources.*;

import java.util.HashMap;
import java.util.Set;

public class Board extends AbstractBoard {
	private static final Piece[] defaultBoard = new Piece[] {
			Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
			Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
			Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
			Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN
	};

	private Board(Board template, boolean isLive) {
		super(template, isLive);
	}

	public Board(int size, boolean useClock) {
		super(size, useClock, defaultBoard, true, true);
	}

    /**
     *
     * @param size Using just one size parameter ensures a square board
     * @param useClock Whether or not a chess clock should be used
     * @throws IllegalArgumentException if ({@code size < 2})   //Pre-conditions
     */
    public Board(int size, boolean useClock, Piece[] initialSetup) {
    	super(size, useClock, initialSetup == null ? defaultBoard : initialSetup, false, true);
    }

    public Board(int size, boolean useClock, Piece[] initialSetup, boolean symmetric) {
		super(size, useClock, initialSetup, symmetric, true);
	}

	public Board(int size, boolean useClock, BoardMode mode, boolean symmetric) {
    	super(size, useClock, mode == BoardMode.DEFAULT ? defaultBoard : new Piece[] {}, symmetric, true);

		if(mode == BoardMode.RANDOM) {
    		// TODO Implement random board generation
		}
	}

	/*
	public boolean undoMove() {
    	if(gameLog.size() == 0) return false;

    	MoveNode lastMove = gameLog.pop();

    	pieces.remove(lastMove.end);
    	pieces.put(lastMove.start, lastMove.piece);

		ChessPiece victim = lastMove.victimPiece;
		if(victim != null)
			pieces.put(victim.position(), victim);

    	return true;
	}
	*/

	public HashMap<Vector2, IChessPiece> getPieces(Alliance alliance) {
		HashMap<Vector2, IChessPiece> temp = new HashMap<>();

		for(Vector2 pos : getPositions()) {
			IChessPiece piece = getPiece(pos);
			if(piece == null) continue;

			if(insideBoard(pos) && piece.alliance().equals(alliance)) {
				temp.put(pos, piece);
			}
		}

		return temp;
	}

	public HashMap<Vector2, IChessPiece> getUsablePieces(Alliance alliance) {
		HashMap<Vector2, IChessPiece> usablePieces = new HashMap<>();

		Set<Vector2> positions = getPositions();
		for(Vector2 pos : positions) {
			IChessPiece piece = getPiece(pos);
			if(piece == null) continue; // Ignore empty squares

			if(insideBoard(pos) && piece.alliance().equals(alliance)) {
				Set<Vector2> possibleDestinations = piece.getPossibleDestinations("Board");
				if(possibleDestinations.size() == 0) continue; // If the piece has no valid moves, ignore it

				usablePieces.put(pos, piece);
			}
		}

		return usablePieces;
	}

	public King getKing(Alliance alliance) {
		HashMap<Vector2, IChessPiece> temp = getPieces(alliance);
		for(Vector2 pos : temp.keySet())
			if(temp.get(pos) instanceof King)
				return (King)temp.get(pos);

		return null;
	}

	/**
	 *
	 * @param start
	 * @param end
	 */
	public boolean movePiece(Vector2 start, Vector2 end) {
		if(!insideBoard(start)) return false;

		ChessPiece piece = (ChessPiece)getPiece(start);

		System.out.println("Currently " + activePlayer + "'s turn");

		if(piece == null) {
			return false; // Check if a piece exists at the given position
		}
		if(!piece.alliance().equals(activePlayer)) {
			return false; // Checks if the active player owns the piece that is being moved
		}
		boolean moveSuccessful = piece.move(end);

		if(!moveSuccessful) { // Attempt to move the piece
			System.out.println("piece.move failed. Mutex released");
			return false;
		}

		setLastPiece(piece);
		ChessPiece endPiece = (ChessPiece)getPiece(end);

		ChessPiece victim = null;
		if(endPiece != null) {
			//Remove hostile attacked piece
			if(!endPiece.alliance().equals(piece.alliance())) {
				capturePiece(endPiece);
				removePiece(end);
				victim = endPiece;
			}
		}

		//assert(piece.position().equals(end));

		logMove(new MoveNode(piece, start, end, victim));

		removePiece(start);
		putPiece(end, piece);
		addDrawPos(start);
		addDrawPos(end);

		//After a successful move, advance to the next player
		activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;

		System.out.println("Local after: " + piece.position() + ", has moved: " + piece.hasMoved());
		System.out.println("Move successful!");

		return true;
	}

	public boolean movePiece(Move move) {
		return movePiece(move.start, move.end);
	}


	@Override
	public Board clone() {
		return clone(true);
	}

	public Board clone(boolean isLive) {
		return new Board(this, isLive);
	}
}