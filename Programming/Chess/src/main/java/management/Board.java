package management;

import pieces.*;
import resources.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class Board {
	private final boolean isLive;

	private static final Piece[] defaultBoard = new Piece[] {
			Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
			Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
			Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
			Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN
	};

    private final int size;
	private Player player1, player2;
	private ChessClock clock = null;
	private ChessPiece lastPiece = null;

	private Alliance activePlayer = Alliance.WHITE;

	public HashMap<Vector2, ChessPiece> pieces = new HashMap<>();
	public HashSet<ChessPiece> inactivePieces = new HashSet<>();

	private Stack<MoveNode> gameLog = new Stack<>();

	private Board(Board template, boolean isLive) {
		this.isLive = isLive;

		size = template.size;
		player1 = template.player1;
		player2 = template.player2;

		if(clock != null) clock = template.clock.clone();
		if(lastPiece != null) lastPiece = template.lastPiece.clonePiece();

		activePlayer = template.activePlayer;
		pieces = (HashMap<Vector2, ChessPiece>)template.pieces.clone();
		inactivePieces = (HashSet<ChessPiece>)template.inactivePieces.clone();

		gameLog = (Stack<MoveNode>)template.gameLog.clone();
	}

	public Board(int size, boolean useClock) {
		if(size < 2) throw new IllegalArgumentException("The board size must be at least 2");

		isLive = true;

		this.size = size;
		setup(useClock, defaultBoard, true);
	}

    /**
     *
     * @param size Using just one size parameter ensures a square board
     * @param useClock Whether or not a chess clock should be used
     * @throws IllegalArgumentException if ({@code size < 2})   //Pre-conditions
     */
    public Board(int size, boolean useClock, Piece[] initialSetup) {
		if(size < 2) throw new IllegalArgumentException("The board size must be at least 2");

		isLive = true;

    	this.size = size;
    	setup(useClock, initialSetup, false);
    }

    public Board(int size, boolean useClock, Piece[] initialSetup, boolean symmetric) {
		if(size < 2) throw new IllegalArgumentException("The board size must be at least 2");

		isLive = true;

		this.size = size;

		setup(useClock, initialSetup, symmetric);
	}

	private void setup(boolean useClock, Piece[] initialSetup, boolean symmetric) {
		int p = 0;

		if(useClock) {
			clock = new ChessClock(2, 900, 12, -1);
		}

		for(Piece type : initialSetup) {
			int x = p % size;
			int y = p / size;

			Vector2 pos = new Vector2(x, y);
			Vector2 invPos = new Vector2(x, size - y - 1);

			if(type.equals(Piece.EMPTY)) continue;

			addPiece(pos, type, Alliance.BLACK);
			System.out.println(pos + ": " + pieces.get(pos));

			if (symmetric) {
				addPiece(invPos, type, Alliance.WHITE);
				System.out.println(invPos + ": " + pieces.get(invPos));
			}

			p++;
		}
	}

	public boolean isLive() { return isLive; }

    public Alliance getActivePlayer() {
    	return activePlayer;
	}

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

	public boolean addPiece(Vector2 pos, Piece type, Alliance alliance) {
		ChessPiece piece = createPiece(pos, type, alliance);
		if(piece == null) return false;

		pieces.put(pos, piece);
		return true;
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

    public boolean insideBoard(Vector2 pos) {
    	return pos.getX() >= 0 && pos.getX() < size &&
				pos.getY() >= 0 && pos.getY() < size;
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
			if(piece == null) continue;

			if(insideBoard(pos) && piece.alliance().equals(alliance)) {
				temp.put(pos, piece);
			}
		}

		return temp;
	}

	public HashMap<Vector2, IChessPiece> getUsablePieces(Alliance alliance) {
		HashMap<Vector2, IChessPiece> temp = new HashMap<>();

		for(Vector2 pos : pieces.keySet()) {
			IChessPiece piece = pieces.get(pos);
			if(piece == null) continue;

			if(insideBoard(pos) && piece.alliance().equals(alliance)) {
				if(piece.getPossibleDestinations("Board").size() == 0) continue; // If the piece has no valid moves, ignore it

				temp.put(pos, piece);
			}
		}

		return temp;
	}

	public King getKing(Alliance alliance) {
		HashMap<Vector2, IChessPiece> temp = getPieces(alliance);
		for(Vector2 pos : temp.keySet())
			if(temp.get(pos) instanceof King)
				return (King)temp.get(pos);

		return null;
	}

	public boolean vacant(Vector2 pos) {
		return !pieces.containsKey(pos);
    }

    /**
     *
     * @return the piece that was last successfully moved
     */
	public IChessPiece getLastPiece() {
		return lastPiece.clonePiece();
	}

	public HashSet<ChessPiece> getInactivePieces() {
		return inactivePieces;
	}

	/**
	 *
	 * @param start
	 * @param end
	 */
	public boolean movePiece(Vector2 start, Vector2 end) {
		if(!insideBoard(start)) return false;

		ChessPiece piece = pieces.get(start);

		System.out.println("Currently " + activePlayer + "'s turn");

		if(piece == null) return false; // Check if a piece exists at the given position
		if(!piece.alliance().equals(activePlayer)) return false; // Checks if the active player owns the piece that is being moved

		System.out.println("Local before: " + piece.position() + ", has moved: " + piece.hasMoved());
		if(!piece.move(end)) return false; // Attempt to move the piece

		lastPiece = piece;
		ChessPiece endPiece = pieces.get(end);

		ChessPiece victim = null;
		if(endPiece != null) {
			//Remove hostile attacked piece
			if(!endPiece.alliance().equals(piece.alliance())) {
				inactivePieces.add(endPiece);
				removePiece(end);
				victim = endPiece;
			}
		}

		//assert(piece.position().equals(end));

		gameLog.push(new MoveNode(piece, start, end, victim));

		pieces.remove(start);
		pieces.put(end, piece);

		//After a successful move, advance to the next player
		activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;

		System.out.println("Local after: " + piece.position() + ", has moved: " + piece.hasMoved());
		System.out.println("Move successful!");

		return true;
	}

	public boolean movePiece(Move move) {
		return movePiece(move.start, move.end);
	}

	private boolean removePiece(Vector2 pos) {
		if(pieces.containsKey(pos)) return false;

		ChessPiece piece = pieces.get(pos);
		pieces.remove(pos);
		inactivePieces.add(piece);

		return true;
	}
	public boolean performAttack(Vector2 start, Vector2 end, Vector2 victim) {
		if(!pieces.keySet().contains(end) || !pieces.keySet().contains(victim)) return false;

		MoveNode node = new MoveNode(pieces.get(end), start, end, pieces.get(victim));
		System.out.println("Performing attack: " + node);

		removePiece(victim);

		gameLog.add(node);
		return true;
	}

	public Stack<MoveNode> getGameLog() {
		return (Stack<MoveNode>) gameLog.clone();
	}

	@Override
	public Board clone() {
		return clone(true);
	}

	public Board clone(boolean isLive) {
		return new Board(this, isLive);
	}
}