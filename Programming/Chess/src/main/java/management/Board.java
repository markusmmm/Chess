package management;

import javafx.scene.media.MediaPlayer;
import main.GameBoard;
import pieces.*;
import resources.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board extends AbstractBoard {
    MediaHelper media = new MediaHelper();
    private BoardMode mode = null;

    private static final Piece[] defaultBoard = new Piece[]{
            Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
            Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN,
            Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
            Piece.PAWN, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
            Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN,
            Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK
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
        super(size, difficulty,false);
    }
    //public Board(int size) { super(size, 0, false); }

    /**
     * Creates a new square board
     * @param size The board's dimensions (One size parameter ensures a square board)
     * @param useClock Whether or not the game should be timed
     * @param mode How the board's initial state should be generated
     */
    public Board(int size, int difficulty, boolean useClock, BoardMode mode) {
        super(size, difficulty, useClock);

        if (mode == BoardMode.DEFAULT) {
            int p = 0;

            for (Piece type : defaultBoard) {
                int x = p % size;
                int y = p / size;

                Vector2 pos = new Vector2(x, y);
                Vector2 invPos = new Vector2(x, size - y - 1);

                if (type.equals(Piece.EMPTY)) continue;

                addPiece(pos, type, Alliance.BLACK);
                //resources.Console.println(pos + ": " + getPiece(pos));

                addPiece(invPos, type, Alliance.WHITE);
                //resources.Console.println(invPos + ": " + getPiece(invPos));

                p++;
            }
        } else if (mode == BoardMode.RANDOM) {
            sync(new RandomBoard(size, difficulty, useClock));
        }
    }

    /**
     * Loads this board's content from a given file
     * @param fileName Name of the save file
     * @throws FileNotFoundException
     */
    public Board(String fileName) throws FileNotFoundException {
        super(fileName);
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
    public Board(File file, int difficulty) throws FileNotFoundException {
        super(file, difficulty);
    }

    /**
     * Gives all active pieces of a given alliance
     * @param alliance The alliance of the pieces to find
     * @return All matching pieces on this board
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

    public Set<Move> getAllPossibleMoves(Alliance alliance) {
        Set<Move> moves = new HashSet<>();

        HashMap<Vector2, AbstractChessPiece> pieces = getPieces(alliance);
        for(Vector2 pos : pieces.keySet()) {
            AbstractChessPiece piece = pieces.get(pos);
            for(Vector2 end : piece.getPossibleDestinations())
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
                Set<Vector2> possibleDestinations = piece.getPossibleDestinations();
                if (possibleDestinations.size() == 0) continue; // If the piece has no valid moves, ignore it

                usablePieces.put(pos, piece);
            }
        }

        return usablePieces;
    }

    /**
     * @param piece Piece to evaluate
     * @param end End-position of attempted move
     * @return If the move is an attempt of pawn promotion
     */
    public boolean pawnPromotion(AbstractChessPiece piece, Vector2 end){
        if (piece instanceof Pawn) {
            int y = piece.position().getY();
            if(piece.legalMove(end)) {
                if (y == 1 && piece.alliance() == Alliance.WHITE && end.getY() == 0) {
                    return true;
                }

                if (y == 6 && piece.alliance() == Alliance.BLACK && end.getY() == 7){
                    return true;
                }
            }
            return false;
        }
        return false;

    }
    /**
     * Attempts to move a piece from 'start' to 'end'
     * @param start Position of the piece to be moved
     * @param end Destination of the attempted move
     * @return If the move was successful
     */
    public boolean movePiece(Vector2 start, Vector2 end) {
        if (!insideBoard(start)) return advanceMove(false);

        saveLog();

        AbstractChessPiece piece = getPiece(start);

        if (piece == null) {
            return advanceMove(false); // Check if a piece exists at the given position
        }
        if (!piece.alliance().equals(activePlayer)) {
            return advanceMove(false); // Checks if the active player owns the piece that is being moved
        }

        if(pawnPromotion(piece, end)) {
            performPawnPromotion((Pawn)piece, start, end);
            advanceMove(true);
        }

        boolean moveSuccessful = piece.move(end);

        if (!moveSuccessful) {
            return advanceMove(false);
        }

        setLastPiece(piece);
        AbstractChessPiece endPiece = getPiece(end);

        AbstractChessPiece victim = null;
        if (endPiece != null) {
            //Remove hostile attacked piece
            if (!endPiece.alliance().equals(piece.alliance())) {
                capturePiece(endPiece);
                removePiece(end);
                victim = endPiece;
            }
        }

        // Assert if the piece' position was updated internally
        assert(piece.position().equals(end));

        logMove(new MoveNode(piece, start, end, victim));

        // Update the piece's position on the board
        removePiece(start);
        putPiece(end, piece);
        // Prompt the GUI to re-draw the changed squares
        addDrawPos(start);
        addDrawPos(end);

        // End move and advance to the next player
        return advanceMove(true);
    }

    /**
     * Overload that takes in a Move object
     * @param move The move to perform
     * @return Whether or not the move was successful
     */
    public boolean movePiece(Move move) {
        //return movePiece(move.start, move.end);
    	return movePiece(move.start,move.end);
    }

    /**
     * Uses internally by board do advance to the next turn
     * @param state Whether or not the move should be advanced
     * @return 'state'
     */
    private boolean advanceMove(boolean state) {
        if (state) {
            ChessClock clock = getClock();

            if(clock != null && !clock.endTurn(moveI % 2))
                return false;

            activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;
            moveI++;

            MediaPlayer np = media.playSound("move.mp3");
            np.play();
        }

        return state;
    }

    /**
     * Moves a piece from 'start' to 'end', and removes 'victim' from this board
     * @param start Position of the attacking piece
     * @param end Destination of the attack
     * @param victim Attacked piece
     */
    public void performAttack(Vector2 start, Vector2 end, Vector2 victim) {
        MoveNode node = new MoveNode(getPiece(start), start, end, getPiece(victim));
        //resources.Console.println("Performing attack: " + node);

        removePiece(victim);
        logMove(node);
    }

    private void performPawnPromotion(Pawn piece, Vector2 start, Vector2 end) {
        Alliance alliance = piece.alliance();

        char c = new GameBoard().pawnPromotion().charAt(0);

        removePiece(start);
        addPiece(end, PieceManager.toPiece(c).piece, alliance);
        logMove(new MoveNode(piece, start, end, getPiece(end)));
    }

    /**
     *
     * @return Clone of this board
     */
    @Override
    public Board clone() {
        return new Board(this);
    }
}