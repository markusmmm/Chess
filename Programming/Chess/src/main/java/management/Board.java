package management;

import pieces.*;
import resources.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board extends AbstractBoard {
    MediaHelper media = new MediaHelper();

    private static final Piece[] defaultBoard = new Piece[]{
            Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
            Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN
    };

    /**
     * Clones all data from 'template' into this board
     * @param template The board to clone data from
     */
    private Board(Board template) {
        super(template);
    }

    /**
     * Creates a new square board
     * @param size The board's dimensions (One size parameter ensures a square board)
     */
    public Board(int size, int difficulty) {
        super(size, difficulty,false,BoardMode.DEFAULT);
    }

    /**
     * Creates a new square board
     * @param size The board's dimensions (One size parameter ensures a square board)
     * @param useClock Whether or not the game should be timed
     * @param mode How the board's initial state should be generated
     */
    public Board(int size, int difficulty, boolean useClock, BoardMode mode) {
        super(size, difficulty, useClock, mode);

        if (mode == BoardMode.DEFAULT || mode == BoardMode.SHADAM) {
            int p = 0;

            for (Piece type : defaultBoard) {
                int x = p % size;
                int y = p / size;

                Vector2 pos = new Vector2(x, y);
                Vector2 invPos = new Vector2(x, size - y - 1);

                if (type.equals(Piece.EMPTY)) continue;

                addPiece(pos, type, Alliance.BLACK);

                addPiece(invPos, type, Alliance.WHITE);

                p++;
            }
        } else if (mode == BoardMode.RANDOM) {
            sync(new RandomBoard(size, difficulty, useClock));
        }
    }

    /**
     * Loads this board's content from a given file
     * @param saveName Name of save file (without file extension)
     * @throws FileNotFoundException
     */
    public Board(String saveName) throws FileNotFoundException {
        super(saveName);
    }

    /**
     * Loads this board's content from a given file
     * @param file The file to be loaded
     * @throws FileNotFoundException
     */
    public Board(File file) throws FileNotFoundException {
        super(file);
    }
    /**
     * Loads this board's content from a given file
     * @param file The file to be loaded
     * @param difficulty Game difficulty
     * @throws FileNotFoundException
     */
    public Board(File file, int difficulty, BoardMode mode) throws FileNotFoundException {
        super(file, difficulty, mode);
    }

    /**
     * @param alliance The alliance of the pieces to find
     * @return All active pieces owned by the given alliance
     */
    public HashMap<Vector2, AbstractChessPiece> getPieces(Alliance alliance) {
        HashMap<Vector2, AbstractChessPiece> pieces = getPieces();
        HashMap<Vector2, AbstractChessPiece> temp = new HashMap<>();

        for(Vector2 pos : pieces.keySet()) {
            AbstractChessPiece piece = pieces.get(pos);
            if (piece.alliance().equals(alliance))
                temp.put(pos, piece);
        }

        return temp;
    }

    /**
     * @param alliance Alliance to get moves from
     * @return All possible actions an alliance can legally perform
     */
    public Set<Move> getAllLegalActions(Alliance alliance) {
        Set<Move> moves = new HashSet<>();

        HashMap<Vector2, AbstractChessPiece> pieces = getPieces(alliance);
        for(Vector2 pos : pieces.keySet()) {
            AbstractChessPiece piece = pieces.get(pos);
            for(Vector2 end : piece.getPossibleActions())
                moves.add(new Move(pos, end));
        }

        return moves;
    }



    /**
     * Gives all active pieces of a given alliance, that can perform at least one legal move
     * @param alliance The alliance of the pieces to find
     * @return All matching pieces on this board
     */
    public HashMap<Vector2, IChessPiece> getUsablePieces(Alliance alliance) {
        HashMap<Vector2, IChessPiece> usablePieces = new HashMap<>();

        Set<Vector2> positions = getPositions();
        for (Vector2 pos : positions) {
            IChessPiece piece = getPiece(pos);
            if (piece == null) continue; // Ignore empty squares

            if (insideBoard(pos) && piece.alliance().equals(alliance)) {
                Set<Vector2> possibleDestinations = piece.getPossibleActions();
                if (possibleDestinations.size() == 0) continue; // If the piece has no valid moves, ignore it

                usablePieces.put(pos, piece);
            }
        }

        return usablePieces;
    }

    /**
     * Attempts to move a piece from 'start' to 'end'
     * @param start Position of the piece to be moved
     * @param end Destination of the attempted move
     * @return If the move was successful
     */
    public boolean movePiece(Vector2 start, Vector2 end) {
        // Save the board's state, before attempting the move (enables undo)
        saveLog();

        // Attempt move
        boolean moveSuccess = performAdditionalAction(start, end);
        if (moveSuccess) {
            setLastPiece(getPiece(end));

            activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;
            moveI++;

            if(media != null)
                media.play("move.mp3");
        }
        return moveSuccess;
    }

    /**
     * Performs an additional action, which does not advance the game
     * @param start Position of the piece to be moved
     * @param end Destination of the attempted move
     * @return If the additional action was successful
     */
    public boolean performAdditionalAction(Vector2 start, Vector2 end) {
        if (!insideBoard(start)) return false;

        // If the game is timed, determine if the current player has any time left
        ChessClock clock = getClock();
        if(clock != null && !clock.endTurn(moveI % 2)) return false;

        AbstractChessPiece piece = getPiece(start);

        // Check if a piece exists at the given position
        if (piece == null) return false;
        // Checks if the active player owns the piece that is being moved
        if (!piece.alliance().equals(activePlayer)) return false;

        // Attempt to move the piece
        if(piece.move(end)) {
            addDrawPos(start, end);

            // Update the piece's position on the board
            removePiece(start);
            putPiece(end, piece);

            AbstractChessPiece victim = getPiece(end);
            //Remove hostile attacked piece, if any
            if (victim != null && !victim.alliance().equals(piece.alliance())) {
                capturePiece(victim);
                removePiece(end);
            }

            logMove(new MoveNode(piece, start, end, victim));

            return true;
        }
        return false;
    }

    /**
     * Overload that takes in a Move object
     * @param move The move to perform
     * @return Whether or not the move was successful
     */
    public boolean movePiece(Move move) {
        return movePiece(move.start,move.end);
    }

    /**
     * Moves a piece from 'start' to 'end', and removes 'victim' from this board
     * @param start Position of the attacking piece
     * @param end Destination of the attack
     * @param victim Attacked piece
     */
    public void performAttack(Vector2 start, Vector2 end, Vector2 victim) {
        MoveNode node = new MoveNode(getPiece(start), start, end, getPiece(victim));

        removePiece(victim);
        logMove(node);
        addDrawPos(start, end, victim);
    }

    /**
     * Transform a piece on the board into another piece
     * @param pos Position of piece to transform
     * @param newType Type of resulting piece
     */
    public void transformPiece(Vector2 pos, Piece newType) {
        AbstractChessPiece oldPiece = getPiece(pos);
        if(oldPiece == null) return;

        removePiece(pos);
        addPiece(pos, newType, oldPiece.alliance());
    }

    /**
     * Simulates a move (bypasses isLegalMove), then restores this board to it's prior state
     * @param start Start-position of simulated move
     * @param end End-position of simulated move
     * @return If king is in check after performed move
     */
    public boolean simulateCheck(Vector2 start, Vector2 end, Alliance alliance) {
        if(!hasKing(alliance)) {
            Console.printWarning("Board doesn't have a " + alliance + " KING");
            return false;
        }
        Board tempBoard = clone();

        forceMovePiece(start, end);
        King king = getKing(alliance);
        boolean checked = king.inCheck();
        sync(tempBoard);

        return checked;
    }

    /**
     *
     * @return Clone of this board
     */
    @Override
    public Board clone() {
        return new Board(this);
    }

    /**
     * @return A text-representation of the board's current state
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int y = 0; y < size(); y++) {
            for (int x = 0; x < size(); x++) {
                str.append("").append(PieceManager.toSymbol(getPiece(new Vector2(x, y))));
            }
            str.append("\n");
        }
        return str.toString();
    }
}