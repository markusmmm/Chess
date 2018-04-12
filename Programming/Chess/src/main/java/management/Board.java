package management;

import pieces.ChessPiece;
import pieces.IChessPiece;
import pieces.King;
import resources.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;

public class Board extends AbstractBoard {
	private Board(Board template, boolean isLive) {
		super(template, isLive);
	}

    /**
     * @param size     Using just one size parameter ensures a square board
     * @param useClock Whether or not a chess clock should be used
     * @throws IllegalArgumentException if ({@code size < 2})   //Pre-conditions
     */
    public Board(int size, boolean useClock, BoardMode mode) {
        super(size, useClock, mode, true);
    }
    public Board(String fileName) throws FileNotFoundException {
    	super(fileName);
	}
	public Board(int size, boolean isLive) {
    	super(size, false, BoardMode.EMPTY, isLive);
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

        for (Vector2 pos : getPositions()) {
            IChessPiece piece = getPiece(pos);
            if (piece == null) continue;

            if (insideBoard(pos) && piece.alliance().equals(alliance)) {
                temp.put(pos, piece);
            }
        }

        return temp;
    }

    public HashMap<Vector2, IChessPiece> getUsablePieces(Alliance alliance) {
        HashMap<Vector2, IChessPiece> usablePieces = new HashMap<>();

        Set<Vector2> positions = getPositions();
        for (Vector2 pos : positions) {
            IChessPiece piece = getPiece(pos);
            if (piece == null) continue; // Ignore empty squares

            if (insideBoard(pos) && piece.alliance().equals(alliance)) {
                Set<Vector2> possibleDestinations = piece.getPossibleDestinations("Board");
                if (possibleDestinations.size() == 0) continue; // If the piece has no valid moves, ignore it

                usablePieces.put(pos, piece);
            }
        }

        return usablePieces;
    }

    public King getKing(Alliance alliance) {
        HashMap<Vector2, IChessPiece> temp = getPieces(alliance);
        for (Vector2 pos : temp.keySet())
            if (temp.get(pos) instanceof King)
                return (King) temp.get(pos);

        return null;
    }

    /**
     * @param start
     * @param end
     */
    public boolean movePiece(Vector2 start, Vector2 end) {
        if (!insideBoard(start)) return false;

        ChessPiece piece = (ChessPiece) getPiece(start);

        System.out.println("Currently " + activePlayer + "'s turn");

        if (piece == null) {
            return false; // Check if a piece exists at the given position
        }
        if (!piece.alliance().equals(activePlayer)) {
            return false; // Checks if the active player owns the piece that is being moved
        }

        if(piece instanceof  King){
            int kingSideRookX = end.getX()+1;
            int queenSideRookX = end.getX()-2;

            // castling kingside
            if (((King) piece).castling(new Vector2(kingSideRookX,end.getY()))){
                Vector2 rookPos = new Vector2(kingSideRookX, end.getY());
                IChessPiece rook = getPiece(rookPos);
                Alliance alliance = rook.alliance();
                Piece pieceType = rook.piece();

                IChessPiece king = getKing(alliance);
                Vector2 kingPos = king.position();

                removePiece(rookPos);
                removePiece(kingPos);


                addPiece(new Vector2(kingSideRookX-2, end.getY()), pieceType, alliance);
                addPiece(new Vector2(kingSideRookX-1, end.getY()), Piece.KING, alliance);

                logMove(new MoveNode(piece, start, end, (ChessPiece) getPiece(end)));
                activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;

                return true;

            }

            // castling queenside
            if (((King) piece).castling(new Vector2(queenSideRookX,end.getY()))){
                Vector2 rookPos = new Vector2(queenSideRookX, end.getY());
                IChessPiece rook = getPiece(rookPos);
                Alliance alliance = rook.alliance();
                Piece pieceType = rook.piece();

                IChessPiece king = getKing(alliance);
                Vector2 kingPos = king.position();

                removePiece(rookPos);
                removePiece(kingPos);


                addPiece(new Vector2(queenSideRookX+3, end.getY()), pieceType, alliance);
                addPiece(new Vector2(queenSideRookX+2, end.getY()), Piece.KING, alliance);

                logMove(new MoveNode(piece, start, end, (ChessPiece) getPiece(end)));
                activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;

                return true;


            }

        }
        boolean moveSuccessful = piece.move(end);

        if (!moveSuccessful) { // Attempt to move the piece
            System.out.println("piece.move failed. Mutex released");
            return false;
        }

        setLastPiece(piece);
        ChessPiece endPiece = (ChessPiece) getPiece(end);

        ChessPiece victim = null;
        if (endPiece != null) {
            //Remove hostile attacked piece
            if (!endPiece.alliance().equals(piece.alliance())) {
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

        //System.out.println("Local after: " + piece.position() + ", has moved: " + piece.hasMoved());
        //System.out.println("Move successful!");

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