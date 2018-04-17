package management;

import main.Main;
import pieces.ChessPiece;
import pieces.IChessPiece;
import pieces.King;
import pieces.Pawn;
import resources.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Board extends AbstractBoard {
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
    public Board(int size) {
        super(size, false);
    }

    /**
     * Creates a new square board
     * @param size The board's dimensions (One size parameter ensures a square board)
     * @param useClock Whether or not the game should be timed
     * @param mode How the board's initial state should be generated
     */
    public Board(int size, boolean useClock, BoardMode mode) {
        super(size, useClock);

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
            generateRandomBoard();
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
     * Generates a random board with random amount of pieces.
     *
     * @return ChessBoard
     */
    private void generateRandomBoard() {
        int bRooks = 0, bPawns = 0, bQueens = 0, bKings = 0, bBishops = 0, bKnights = 0;
        int wRooks = 0, wPawns = 0, wQueens = 0, wKings = 0, wBishops = 0, wKnights = 0;
        //Keeps track of how many of each pieces is added, so that we don't get to many of one.
        int w = 0;
        int b = 0;
        //Counters for black and white pieces
        int bishopX = 0;
        int bishopY = 0;

        int bishopWX = 0;
        int bishopWY = 0;
        //X and Y positions of bishops. This is to make sure that bishops from the same alliance don't controll the same colored tiles.
        Random random = new Random();
        while (b < 16) {
            Piece aPiece = randomPiece();
            int x = random.nextInt(7 - 0 + 1) + 0;
            int y = random.nextInt(7 - 0 + 1) + 0;

            Vector2 pos = new Vector2(x, y);
            //Creates a random position
            if (!vacant(pos)) {
                b++;
                continue;
                //Checks if the position is vacant.
            }
            if (b == 15 && bKings == 0) {
                addPiece(pos, Piece.KING, Alliance.BLACK);
                if (getKing(Alliance.BLACK).inCheck()) {
                    removePiece(pos);
                    continue;
                }
                w++;
                wKings++;
                //If a black king has not been added by the last count, a black king will be added.
                continue;
            }

            if (aPiece.equals(Piece.ROOK) && bRooks < 2) {
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bRooks++;
                continue;
            }
            if (aPiece.equals(Piece.PAWN) && bPawns < 8) {
                if (y == 0) {
                    //Checks that the pawn isn't put in an invalid position on the board.
                    continue;
                }
                if (y == 7) {
                    addPiece(pos, Piece.QUEEN, Alliance.BLACK);
                    b++;
                    continue;
                    //If the pawn is placed on the opposite side of the board, there will be pawn promotion
                }
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bPawns++;
                continue;
            }
            if (aPiece.equals(Piece.QUEEN) && bQueens < 1) {
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bQueens++;
                continue;
            }
            if (aPiece.equals(Piece.KING) && bKings < 1) {
                addPiece(pos, aPiece, Alliance.BLACK);
                if (getKing(Alliance.BLACK).inCheck()) {
                    removePiece(pos);
                    continue;
                }
                b++;
                bKings++;
                continue;
            }
            if (aPiece.equals(Piece.BISHOP) && bBishops < 2) {
                if (bBishops == 0) {
                    bishopX = x;
                    bishopY = y;
                }
                if (bBishops == 1) {
                    boolean b1 = bishopX % 2 == bishopY % 2;
                    boolean b2 = x % 2 == y % 2;
                    if (b1 == b2) {
                        continue;
                    }
                    //Makes sure that if there's two bishops to be added, they will control their own colored tiles.
                }
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bBishops++;
                continue;
            }
            if (aPiece.equals(Piece.KNIGHT) && bKnights < 2) {
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bKnights++;
                continue;
            }
            if (aPiece.equals(Piece.EMPTY)) {
                b++;
                continue;
            }
        }
        while (w < 16) {
            //Same conditions and method as the black pieces
            Piece aPiece = randomPiece();
            int x = random.nextInt(7 - 0 + 1) + 0;
            int y = random.nextInt(7 - 0 + 1) + 0;

            Vector2 invPos = new Vector2(x, y);
            if (!vacant(invPos)) {
                w++;
                continue;
            }
            if (w == 15 && wKings == 0) {
                addPiece(invPos, Piece.KING, Alliance.WHITE);
                if (getKing(Alliance.WHITE).inCheck()) {
                    removePiece(invPos);
                    continue;
                }
                w++;
                wKings++;
                continue;
            }
            if (aPiece.equals(Piece.ROOK) && wRooks < 2) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wRooks++;
                continue;
            }
            if (aPiece.equals(Piece.PAWN) && wPawns < 8) {
                if (y == 7) {
                    continue;
                }
                if (y == 0) {
                    addPiece(invPos, Piece.QUEEN, Alliance.WHITE);
                    w++;
                    continue;
                }
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wPawns++;
                continue;
            }
            if (aPiece.equals(Piece.QUEEN) && wQueens < 1) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wQueens++;
                continue;
            }
            if (aPiece.equals(Piece.KING) && wKings < 1) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                if (getKing(Alliance.WHITE).inCheck()) {
                    removePiece(invPos);
                    continue;
                }
                w++;
                wKings++;
                continue;
            }
            if (aPiece.equals(Piece.BISHOP) && wBishops < 2) {
                if (wBishops == 0) {
                    bishopWX = x;
                    bishopWY = y;
                }
                if (wBishops == 1) {
                    boolean b1 = bishopWX % 2 == bishopWY % 2;
                    boolean b2 = x % 2 == y % 2;
                    if (b1 == b2) {
                        continue;
                    }
                }
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wBishops++;
                continue;
            }
            if (aPiece.equals(Piece.KNIGHT) && wKnights < 2) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wKnights++;
                continue;
            }
            if (aPiece.equals(Piece.EMPTY)) {
                b++;
                continue;
            }
        }
    }

    /**
     * Gives all active pieces of a given alliance
     * @param alliance The alliance of the pieces to find
     * @return All matching pieces on this board
     */
    public HashMap<Vector2, ChessPiece> getPieces(Alliance alliance) {
        HashMap<Vector2, ChessPiece> pieces = getPieces();
        HashMap<Vector2, ChessPiece> temp = new HashMap<>();

        for(Vector2 pos : pieces.keySet()) {
            ChessPiece piece = pieces.get(pos);
            if (pieces.get(pos).alliance().equals(alliance))
                temp.put(pos, piece);
        }

        return temp;
    }
    public HashMap<Vector2, ChessPiece> getPieces() {
        HashMap<Vector2, ChessPiece> temp = new HashMap<>();

        for(Vector2 pos : getPositions()) {
            ChessPiece piece = getPiece(pos);
            if (piece == null) continue;

            if (insideBoard(pos)) {
                temp.put(pos, piece);
            }
        }

        return temp;
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
     *
     * @param alliance The alliance of the king to find
     * @return The king on this board with the given alliance
     */
    public King getKing(Alliance alliance) {
        HashMap<Vector2, ChessPiece> temp = getPieces(alliance);
        for (Vector2 pos : temp.keySet())
            if (temp.get(pos) instanceof King)
                return (King) temp.get(pos);

        return null;
    }

    /**
     * Attempts to move a piece from 'start' to 'end'
     * @param start Position of the piece to be moved
     * @param end Destination of the attempted move
     * @return If the move was successful
     */
    public boolean movePiece(Vector2 start, Vector2 end) {
        if (!insideBoard(start)) return advanceMove(false);

        // Save board state, before changes are made (Enables undo)
        saveFile(new File(Main.logsDir, "log" + moveI() + ".txt"), true);

        ChessPiece piece = getPiece(start);

        if (piece == null) {
            return advanceMove(false); // Check if a piece exists at the given position
        }
        if (!piece.alliance().equals(activePlayer)) {
            return advanceMove(false); // Checks if the active player owns the piece that is being moved
        }

        // pawn promotion
        if (piece instanceof Pawn) {
            Vector2 piecePos = piece.position();
            int x = piecePos.getX();
            int y = piecePos.getY();
            if(((Pawn) piece).legalMove(end)) {
                if (y == 1 && piece.alliance() == Alliance.WHITE && end.getY() == 0) {

                    removePiece(piecePos);


                    addPiece(end, Piece.QUEEN,Alliance.WHITE);

                    return advanceMove(true);

                }

                if (y == 6 && piece.alliance() == Alliance.BLACK && end.getY() == 7) {
                    removePiece(piecePos);


                    addPiece(end, Piece.QUEEN,Alliance.BLACK);


                    return advanceMove(true);
                }
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

                logMove(new MoveNode(piece, start, end, (ChessPiece) getPiece(end)));

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

                logMove(new MoveNode(piece, start, end, getPiece(end)));

                return advanceMove(true);
            }

        }


        boolean moveSuccessful = piece.move(end);

        if (!moveSuccessful) {
            return advanceMove(false);
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

        //resources.Console.println("Local after: " + piece.position() + ", has moved: " + piece.hasMoved());
        //resources.Console.println("Move successful!");

        return advanceMove(true);
    }

    /**
     * Overload that takes in a Move object
     * @param move The move to perform
     * @return Whether or not the move was successful
     */
    public boolean movePiece(Move move) {
        return movePiece(move.start, move.end);
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
     * Saves the board's state to a text-file
     * @param file Name of the save (No path/file-extension)
     */
	public void saveFile(File file, boolean deleteOnExit) {
        String path = file.getAbsolutePath();
        try {
            FileWriter save = new FileWriter(path);
            int n = size();

            Stack<MoveNode> gameLog = getGameLog();
            save.write(n + " 0 " + gameLog.size() + " " + moveI + "\n");
            for (int y = 0; y < n; y++) {
                String line = "";
                for (int x = 0; x < n; x++) {
                    ChessPiece p = getPiece(new Vector2(x, y));
                    char s = 'e';
                    if (p != null)
                        s = PieceManager.toSymbol(p);

                    line += s;
                }
                save.write(line + "\n");
            }

            for(MoveNode node : gameLog) {
                int x0 = node.start.getX(), y0 = node.start.getY(),
                        x1 = node.end.getX(), y1 = node.end.getY();
                save.write(PieceManager.toSymbol(node.piece) + " " + x0 + " " + y0 + " " + x1 + " " + y1 + " " + PieceManager.toSymbol(node.victimPiece) + "\n");
            }

            // Save internal data for each piece on board
            save.write(Main.DATA_SEPARATOR + "\n");
            HashMap<Vector2, ChessPiece> pieces = getPieces();
            for(Vector2 pos : pieces.keySet()) {
                ChessPiece piece = pieces.get(pos);
                save.write(pos.getX() + " " + pos.getY() + " " + (piece.hasMoved() ? 1 : 0));

                if(piece instanceof Pawn)
                    save.write(" " + ( ((Pawn)piece).hasDoubleStepped() ? 1 : 0));

                save.write("\n");
            }

            save.close();
            Console.printSuccess("Board saved to " + path);

            if(deleteOnExit)
                new File(path).deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveFile(File file) {
	    saveFile(file, false);
    }
    public void saveFile(String saveName, boolean deleteOnExit) {
	    saveFile(new File(Main.savesDir, saveName + ".txt"), deleteOnExit);
    }
    public void saveFile(String saveName) {
	    saveFile(saveName, false);
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