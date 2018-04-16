package management;

import pieces.*;
import resources.*;
import resources.Console;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Semaphore;

public class AbstractBoard {
    private Semaphore mutex = new Semaphore(1);

    private boolean hasWhiteKing, hasBlackKing;


    private static final Piece[] defaultBoard = new Piece[]{
            Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
            Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN,
            Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
            Piece.PAWN, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
            Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN,
            Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK
    };

    private final int size;
    private Player player1, player2;
    private ChessClock clock = null;
    private ChessPiece lastPiece = null;

    protected Alliance activePlayer = Alliance.WHITE;

    private HashMap<Vector2, ChessPiece> pieces = new HashMap<>();
    private Stack<Vector2> drawPositions = new Stack<>();
    private HashMap<Vector2, ChessPiece> suspendedPieces = new HashMap<>(); // Used to ignore pieces that are still on the board
    private HashSet<ChessPiece> capturedPieces = new HashSet<>();

    private Stack<MoveNode> gameLog = new Stack<>();

    protected AbstractBoard(AbstractBoard other) {
        hasBlackKing = other.hasBlackKing;
        hasWhiteKing = other.hasWhiteKing;

        size = other.size;
        player1 = other.player1;
        player2 = other.player2;

        if (clock != null) clock = other.clock.clone();
        if (lastPiece != null) lastPiece = other.lastPiece.clonePiece();

        activePlayer = other.activePlayer;
        pieces = (HashMap<Vector2, ChessPiece>) other.pieces.clone();
        drawPositions = (Stack<Vector2>) other.drawPositions.clone();
        suspendedPieces = (HashMap<Vector2, ChessPiece>) other.suspendedPieces.clone();
        capturedPieces = (HashSet<ChessPiece>) other.capturedPieces.clone();

        gameLog = (Stack<MoveNode>) other.gameLog.clone();
    }


    protected AbstractBoard(int size, boolean useClock) {

        if (size < 2) throw new IllegalArgumentException("The board size must be at least 2");

        this.size = size;

        generateClock(useClock);

    }

    protected AbstractBoard(String fileName) throws FileNotFoundException {
        fileName += ".txt";

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(fileName);
        Scanner file = new Scanner(is);
        size = file.nextInt();
        generateClock(file.nextInt() != 0);

        Console.println("Size read from file: " + size);

        for (int y = 0; y < size; y++) {
            String line = file.next();
            for (int x = 0; x < size; x++) {
                char c = line.charAt(x);

                Vector2 pos = new Vector2(x, y);
                Piece type = PieceManager.toPiece(c);
                Alliance alliance = Character.isLowerCase(c) ? Alliance.BLACK : Alliance.WHITE;

                //resources.Console.println("Setting piece at " + pos);
                addPiece(pos, type, alliance);
            }
        }

        file.close();
    }

    protected static Piece randomPiece() {
        int pick = new Random().nextInt(Piece.values().length);
        return Piece.values()[pick];
    }

    private void generateClock(boolean doGenerate) {
        if (!doGenerate) return;
        clock = new ChessClock(2, 900, 12, -1);
    }

    public boolean ready() {
        return mutex.availablePermits() == 1;
    }


    public int nPieces() {
        return pieces.size();
    }

    /**
     * @return size of the square board
     */
    public int size() {
        return size;
    }

    public boolean insideBoard(Vector2 pos) {
        return pos.getX() >= 0 && pos.getX() < size &&
                pos.getY() >= 0 && pos.getY() < size;
    }

    public Alliance getActivePlayer() {
        return activePlayer;
    }

    public Set<Vector2> getPositions() {
        try {
            mutex.acquire();

            Set<Vector2> temp = pieces.keySet();
            Set<Vector2> positions = new HashSet<>();
            for (Vector2 p : temp)
                positions.add(p);

            mutex.release();
            return positions;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return null;
        }
    }

    public Stack<Vector2> clearDrawPieces() {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by clearDrawPieces");

            Stack<Vector2> result = (Stack<Vector2>) drawPositions.clone();
            drawPositions.clear();

            mutex.release();
            //resources.Console.println("Mutex released");
            return result;
        } catch (InterruptedException e) {
            System.err.println("clearDrawPieces was interrupted");
            e.printStackTrace();

            mutex.release();
            //resources.Console.println("Mutex released");
            return null;
        }

    }

    public ChessPiece addPiece(Vector2 pos, Piece type, Alliance alliance) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by addPiece");

            ChessPiece piece = createPiece(pos, type, alliance);
            if (piece == null) {
                mutex.release();
                //resources.Console.println("Mutex released");
                return null;
            }

            pieces.put(pos, piece);
            drawPositions.push(pos);

            mutex.release();
            //resources.Console.println("Mutex released");
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            //resources.Console.println("Mutex released");
            return null;
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
                if (alliance == Alliance.WHITE)
                    hasWhiteKing = true;
                else
                    hasBlackKing = true;
                return new King(pos, alliance, this, false);
            case PAWN:
                return new Pawn(pos, alliance, this, false, false);
            case ROOK:
                return new Rook(pos, alliance, this, false);
        }
        return null;
    }

    public boolean forceMovePiece(Vector2 start, Vector2 end) {
        if(!(pieces.containsKey(start) && insideBoard(end))) return false;

        try {
            mutex.acquire();

            ChessPiece piece = pieces.get(start);
            pieces.remove(start);
            pieces.put(end, piece);

            mutex.release();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return false;
        }
    }

    public boolean undoMove() {
        // TODO AbstractBoard.undoMove
        throw new NotImplementedException();

        //if(gameLog.size() == 0) return false;
        //MoveNode lastMove = gameLog.peek();
    }

    public boolean hasKing(Alliance alliance) {
        return alliance == Alliance.WHITE ? hasWhiteKing : hasBlackKing;
    }

    public boolean transformPiece(Vector2 pos, Piece newType) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by transformPiece");

            ChessPiece piece = pieces.get(pos);
            if (piece == null) {
                mutex.release();
                //resources.Console.println("Mutex released");
                return false;
            }

            pieces.remove(pos);

            ChessPiece newPiece = createPiece(pos, newType, piece.alliance());

            pieces.put(pos, newPiece);
            drawPositions.push(pos);

            mutex.release();
            //resources.Console.println("Mutex released");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //resources.Console.println("Mutex released");
            return false;
        }
    }

    public void suspendPieces(Vector2... positions) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by suspendPieces");

            for (Vector2 pos : positions) {
                if (!pieces.containsKey(pos)) continue;
                suspendedPieces.put(pos, pieces.get(pos));
                pieces.remove(pos);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //resources.Console.println("Mutex released");
        }
    }

    public void releasePieces(Vector2... positions) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by releasePieces");

            for (Vector2 pos : positions) {
                if (!suspendedPieces.containsKey(pos)) continue;

                pieces.put(pos, suspendedPieces.get(pos));
                suspendedPieces.remove(pos);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //resources.Console.println("Mutex released");
        }
    }

    public boolean vacant(Vector2 pos) {
        return !pieces.containsKey(pos);
    }

    /**
     * Calls 'getPiece' on all players, until a match is found (if it exists)
     *
     * @param pos
     * @return Type of piece at the given location (Piece.EMPTY if no match is found)
     */
    public ChessPiece getPiece(Vector2 pos) {
        if (vacant(pos)) return null;

        try {
            mutex.acquire();
            ////resources.Console.println("Mutex acquired by getPiece");

            ChessPiece piece = pieces.get(pos).clonePiece();
            mutex.release();
            ////resources.Console.println("Mutex released");
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return null;
        }


    }

    protected void putPiece(Vector2 pos, ChessPiece piece) {
        try {
            //resources.Console.println("Attempting to put " + piece + " at " + pos);
            mutex.acquire();

            pieces.put(pos, piece.clone());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //resources.Console.println("Mutex released");
        }

    }

    protected void setLastPiece(ChessPiece piece) {
        lastPiece = piece;
    }

    protected void capturePiece(ChessPiece piece) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by capturePiece");

            capturedPieces.add(piece);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //resources.Console.println("Mutex released");
        }
    }

    public void addDrawPos(Vector2... positions) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by addDrawPos");

            for (Vector2 pos : positions)
                drawPositions.add(pos);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //resources.Console.println("Mutex released");
        }
    }

    protected void logMove(MoveNode node) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by logMove");

            gameLog.push(node);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //resources.Console.println("Mutex released");
        }
    }

    /**
     * @return the piece that was last successfully moved
     */
    public IChessPiece getLastPiece() {
        return lastPiece.clonePiece();
    }

    public HashSet<ChessPiece> getCapturedPieces() {
        return (HashSet<ChessPiece>) capturedPieces.clone();
    }

    public boolean removePiece(Vector2 pos) {
        if (!pieces.containsKey(pos)) return false;
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by removePiece");

            ChessPiece piece = pieces.get(pos);
            pieces.remove(pos);
            //capturedPieces.add(piece);
            drawPositions.push(pos);

            mutex.release();
            //resources.Console.println("Mutex released");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            mutex.release();
            return false;
        }
    }

    public void removePieces(Vector2... positions) {
        try {
            mutex.acquire();
            //resources.Console.println("Mutex acquired by removePieces");

            for (Vector2 pos : positions) {
                if (!pieces.containsKey(pos)) continue;

                ChessPiece piece = pieces.get(pos);
                pieces.remove(pos);
                //capturedPieces.add(piece);
                drawPositions.push(pos);
            }

            mutex.release();
            //resources.Console.println("Mutex released");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }

    public Stack<MoveNode> getGameLog() {
        return (Stack<MoveNode>) gameLog.clone();
    }
}
