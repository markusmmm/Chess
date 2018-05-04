package management;

import main.GameController;
import pieces.AbstractChessPiece;
import pieces.IChessPiece;
import pieces.King;
import pieces.Pawn;
import resources.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board extends AbstractBoard {
    MediaHelper media = new MediaHelper();

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

        try {
            if (mode == BoardMode.DEFAULT) {
                loadBoard("default");
            } else if (mode == BoardMode.RANDOM) {
                sync(new RandomBoard(size, difficulty, useClock));
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Default board state does not exist in the resource directory. Terminating application...");
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

    private Set<Move> getAllPossibleActionsOfType(Alliance alliance, Action action) {
        Set<Move> actions = new HashSet<>();

        HashMap<Vector2, AbstractChessPiece> pieces = getPieces(alliance);
        for(Vector2 pos : pieces.keySet()) {
            AbstractChessPiece piece = pieces.get(pos);
            Set<Vector2> set = action == Action.ACTION ? piece.getPossibleActions() : (action == Action.MOVE ? piece.getPossibleMoves() : piece.getPossibleAttacks());

            Console.printNotice("Possible " + action + "S for " + piece + " " + piece.position() + ": " + set);

            for(Vector2 end : set)
                actions.add(new Move(pos, end));
        }

        return actions;
    }

    public Set<Move> getAllPossibleMoves(Alliance alliance) {
        return getAllPossibleActionsOfType(alliance, Action.MOVE);
    }
    public Set<Move> getAllPossibleAttacks(Alliance alliance) {
        return getAllPossibleActionsOfType(alliance, Action.ATTACK);
    }
    public Set<Move> getAllPossibleActions(Alliance alliance) {
        return getAllPossibleActionsOfType(alliance, Action.ACTION);
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


    public boolean pawnPromotion(AbstractChessPiece piece, Vector2 end){


        if (piece instanceof Pawn) {
            Vector2 piecePos = piece.position();
            int x = piecePos.getX();
            int y = piecePos.getY();
            if(((Pawn) piece).legalAction(end)) {
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


        if(piece instanceof Pawn){
            if((pawnPromotion((Pawn)piece, end)))
            {

                GameController gameController = new GameController();
                Alliance alliance = piece.alliance();

                String c = gameController.pawnPromotion();

                switch (c.charAt(0)) {
                    case 'q':
                        removePiece(start);
                        addPiece(end, Piece.QUEEN, alliance);
                        logMove(new MoveNode(piece, start, end, (AbstractChessPiece) getPiece(end)));

                        return advanceMove(true);
                    case 'b':
                        removePiece(start);
                        addPiece(end, Piece.BISHOP, alliance);
                        logMove(new MoveNode(piece, start, end, (AbstractChessPiece) getPiece(end)));

                        return advanceMove(true);
                    case 'k':
                        removePiece(start);
                        addPiece(end, Piece.KNIGHT, alliance);
                        logMove(new MoveNode(piece, start, end, (AbstractChessPiece) getPiece(end)));

                        return advanceMove(true);
                    case 'r':
                        removePiece(start);
                        addPiece(end, Piece.ROOK, alliance);
                        logMove(new MoveNode(piece, start, end, (AbstractChessPiece) getPiece(end)));

                        return advanceMove(true);


                }

                media.playSound("move.mp3");

                if(!end.equals(piece.position()))
                    Console.printError("Position in " + piece + " was not updated internally!");
            }
        }

        //castling
        if (piece instanceof King) {
            int kingSideRookX = end.getX() + 1;
            int queenSideRookX = end.getX() - 2;

            // castling kingside
            if (((King) piece).castling(new Vector2(kingSideRookX, end.getY()))) {
                Vector2 rookPos = new Vector2(kingSideRookX, end.getY());
                IChessPiece rook = getPiece(rookPos);
                Alliance alliance = rook.alliance();
                Piece pieceType = rook.piece();

                IChessPiece king = getKing(alliance);
                Vector2 kingPos = king.position();

                removePiece(rookPos);
                removePiece(kingPos);


                addPiece(new Vector2(kingSideRookX - 2, end.getY()), pieceType, alliance);
                addPiece(new Vector2(kingSideRookX - 1, end.getY()), Piece.KING, alliance);
                media.playSound("move.mp3");

                logMove(new MoveNode(piece, start, end, (AbstractChessPiece) getPiece(end)));

                return advanceMove(true);

            }

            // castling queenside
            if (((King) piece).castling(new Vector2(queenSideRookX, end.getY()))) {
                Vector2 rookPos = new Vector2(queenSideRookX, end.getY());
                IChessPiece rook = getPiece(rookPos);
                Alliance alliance = rook.alliance();
                Piece pieceType = rook.piece();

                IChessPiece king = getKing(alliance);
                Vector2 kingPos = king.position();

                removePiece(rookPos);
                removePiece(kingPos);


                addPiece(new Vector2(queenSideRookX + 3, end.getY()), pieceType, alliance);
                addPiece(new Vector2(queenSideRookX + 2, end.getY()), Piece.KING, alliance);
                media.playSound("move.mp3");

                logMove(new MoveNode(piece, start, end, getPiece(end)));

                return advanceMove(true);
            }

        }

        boolean moveSuccessful = piece.move(end, this);

        if (!moveSuccessful) {
            return advanceMove(false);
        }

        setLastPiece(piece);
        AbstractChessPiece endPiece = (AbstractChessPiece) getPiece(end);

        AbstractChessPiece victim = null;
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

        //resources.Console.println("Local after: " + piece.position() + ", has moved: " + piece.hasMoved());
        //resources.Console.println("Move successful!");

        return advanceMove(true);
    }

    /**
     * Uses internally by board do advance to the next turn
     * @param state Whether or not the move should be advanced
     * @return 'state'
     */
    private boolean advanceMove(boolean state) {
        if (state) {
            activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;
            moveI++;
        }

        return state;
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

    /**
     *
     * @return Clone of this board
     */
    @Override
    public Board clone() {
        return new Board(this);
    }
}