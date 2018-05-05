package management;

import main.GameController;
import pieces.*;
import resources.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board extends AbstractBoard {
    MediaHelper media = new MediaHelper();

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

    /**
     * @param alliance Alliance of pieces to get actions from
     * @param action Type of action
     * @return All legal actions of a given type the given alliance can perform
     */
    private Set<Move> getAllPossibleActionsOfType(Alliance alliance, Action action) {
        Set<Move> actions = new HashSet<>();

        HashMap<Vector2, AbstractChessPiece> pieces = getPieces(alliance);
        for(Vector2 pos : pieces.keySet()) {
            AbstractChessPiece piece = pieces.get(pos);
            Set<Vector2> set = action == Action.ACTION ? piece.getPossibleActions() : (action == Action.MOVE ? piece.getPossibleMoves() : piece.getPossibleAttacks());
            //Console.printNotice("Possible " + action + "S for " + piece + " " + piece.position() + ":\t" + set);

            for(Vector2 end : set)
                actions.add(new Move(pos, end));
        }

        return actions;
    }

    /**
     * @param alliance What alliance to get moves from
     * @return All legal moves of a given type the given alliance can perform
     */
    public Set<Move> getAllPossibleMoves(Alliance alliance) {
        return getAllPossibleActionsOfType(alliance, Action.MOVE);
    }
    /**
     * @param alliance What alliance to get attacks from
     * @return All legal attacks of a given type the given alliance can perform
     */
    public Set<Move> getAllPossibleAttacks(Alliance alliance) {
        return getAllPossibleActionsOfType(alliance, Action.ATTACK);
    }
    /**
     * @param alliance What alliance to get actions from
     * @return All actions moves of a given type the given alliance can perform
     */
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

    /**
     * Attempts to move a piece from 'start' to 'end'
     * @param start Position of the piece to be moved
     * @param end Destination of the attempted move
     * @return If the move was successful
     */
    public boolean movePiece(Vector2 start, Vector2 end) {
        AbstractChessPiece piece = getPiece(start);
        if (!insideBoard(start)) return advanceMove(piece, start, end,  false);

        if (piece == null)
            return advanceMove(piece, start, end, false); // Check if a piece exists at the given position
        if (!piece.alliance().equals(activePlayer))
            return advanceMove(piece, start, end, false); // Checks if the active player owns the piece that is being moved

        // Enables undo
        saveLog();

        if(performPromotion(piece, end))
            return advanceMove(piece, start, end, true);

        performCastling(piece, end);

        boolean moveSuccessful = piece.move(end, this);
        if(moveSuccessful) Console.printSuccess("Move " + start + " -> " + end + " successful");
        return advanceMove(piece, start, end, moveSuccessful);
    }

    /**
     * Uses internally by board do advance to the next turn
     * @param state Whether or not the move should be advanced
     * @return 'state'
     */
    private boolean advanceMove(AbstractChessPiece piece, Vector2 start, Vector2 end, boolean state) {
        if (state) {
            activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;
            moveI++;

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

            logMove(new MoveNode(piece, start, end, victim));

            removePiece(start);
            putPiece(end, piece);
            addDrawPos(start,end);


            // Assert if the piece's position was updated internally
            AbstractChessPiece movedPiece = getPiece(end);
            if(!movedPiece.position().equals(end)) {
                throw new IllegalStateException(movedPiece + " has position " + movedPiece.position() + " should be " + end);
            }
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
     * @param piece Piece to check
     * @param end End position of attempted move
     * @return If the piece is a pawn, and move is legal
     */
    private boolean canPromotePiece(AbstractChessPiece piece, Vector2 end){
        if (piece instanceof Pawn) {
            Pawn pawn = (Pawn)piece;

            if(pawn.legalAction(end)) {
                int y = pawn.position().getY();
                return (y == 1 && pawn.alliance() == Alliance.WHITE && end.getY() == 0) ||
                        (y == 6 && pawn.alliance() == Alliance.BLACK && end.getY() == 7);
            }
        }
        return false;
    }
    private boolean performPromotion(AbstractChessPiece piece, Vector2 end) {
        if((canPromotePiece(piece, end))) {
            Pawn pawn = (Pawn)piece;
            GameController gameController = new GameController();
            Alliance alliance = pawn.alliance();

            char c = gameController.pawnPromotion().charAt(0);

            // Remove the pawn, and add in the selected promotion
            removePiece(piece.position());
            addPiece(end, PieceManager.toPiece(c).piece, alliance);

            media.playSound("move.mp3");

            if(!end.equals(piece.position())) {
                throw new IllegalStateException("Position in " + piece + " was not updated internally!");
            }
            return true;
        }
        return false;
    }

    private void performCastling(AbstractChessPiece piece, Vector2 end) {
        if (piece instanceof King) {
            King king = (King)piece;

            int kingSideRookX = end.getX() + 1;
            int queenSideRookX = end.getX() - 2;
            boolean kingSideCastling = king.castling(new Vector2(kingSideRookX, end.getY())),
                    queenSideCastling = king.castling(new Vector2(queenSideRookX, end.getY()));

            Vector2 rookPos = null, newRookPos = null;

            // Evaluate castling king-side
            if (kingSideCastling) {
                rookPos = new Vector2(kingSideRookX, end.getY());
                newRookPos = new Vector2(kingSideRookX - 2, end.getY());
            }
            // Evaluate castling queen-side
            else if (queenSideCastling) {
                rookPos = new Vector2(queenSideRookX, end.getY());
                newRookPos = new Vector2(queenSideRookX + 3, end.getY());
            }

            if(kingSideCastling || queenSideCastling) {
                IChessPiece rook = getPiece(rookPos);
                if(rook.alliance() == king.alliance()) {
                    boolean rookMoved = forceMovePiece(rookPos, newRookPos);
                    if(!rookMoved) throw new IllegalStateException("An error occurred while castling");

                    media.playSound("move.mp3");
                    addDrawPos(rookPos, newRookPos);
                }
            }
        }
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