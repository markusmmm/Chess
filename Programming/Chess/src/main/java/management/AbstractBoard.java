package management;

import pieces.*;
import resources.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Semaphore;

public class AbstractBoard {
    private Semaphore mutex = new Semaphore(1);
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

    protected Alliance activePlayer = Alliance.WHITE;

    private HashMap<Vector2, ChessPiece> pieces = new HashMap<>();
    private Stack<Vector2> drawPositions = new Stack<>();
    private HashMap<Vector2, ChessPiece> suspendedPieces = new HashMap<>();
    private HashSet<ChessPiece> capturedPieces = new HashSet<>();

    private Stack<MoveNode> gameLog = new Stack<>();

    protected AbstractBoard(AbstractBoard other, boolean isLive) {
        this.isLive = isLive;

        size = other.size;
        player1 = other.player1;
        player2 = other.player2;

        if(clock != null) clock = other.clock.clone();
        if(lastPiece != null) lastPiece = other.lastPiece.clonePiece();

        activePlayer = other.activePlayer;
        pieces = (HashMap<Vector2, ChessPiece>) other.pieces.clone();
        capturedPieces = (HashSet<ChessPiece>) other.capturedPieces.clone();

        gameLog = (Stack<MoveNode>) other.gameLog.clone();
    }

    protected AbstractBoard(int size, boolean useClock, Piece[] initialSetup, boolean symmetric, boolean isLive) {
        if(size < 2) throw new IllegalArgumentException("The board size must be at least 2");

        int p = 0;

        this.size = size;
        this.isLive = isLive;

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
    public int nPieces() {
        return pieces.size();
    }
    /**
     *
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
    public Set<Vector2> getPositions() { return pieces.keySet(); }

    public Stack<Vector2> clearDrawPieces() {
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by clearDrawPieces");

            Stack<Vector2> result = (Stack<Vector2>) drawPositions.clone();
            drawPositions.clear();

            mutex.release();
            System.out.println("Mutex released");
            return result;
        } catch (InterruptedException e) {
            System.err.println("clearDrawPieces was interrupted");
            e.printStackTrace();

            mutex.release();
            System.out.println("Mutex released");
            return null;
        }

    }

    public boolean addPiece(Vector2 pos, Piece type, Alliance alliance) {
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by addPiece");

            ChessPiece piece = createPiece(pos, type, alliance);
            if(piece == null) {
                mutex.release();
                System.out.println("Mutex released");
                return false;
            }

            pieces.put(pos, piece);
            drawPositions.push(pos);

            mutex.release();
            System.out.println("Mutex released");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            System.out.println("Mutex released");
            return false;
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
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by transformPiece");

            ChessPiece piece = pieces.get(pos);
            if(piece == null) {
                mutex.release();
                return false;
            }

            pieces.remove(pos);

            ChessPiece newPiece = createPiece(pos, newType, piece.alliance());

            pieces.put(pos, newPiece);
            drawPositions.push(pos);

            mutex.release();
            System.out.println("Mutex released");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            System.out.println("Mutex released");
            return false;
        }
    }

    public void suspendPiece(Vector2 pos) {
        if(!pieces.containsKey(pos)) return;
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by suspendPiece");

            suspendedPieces.put(pos, pieces.get(pos));
            pieces.remove(pos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            System.out.println("Mutex released");
        }
    }
    public void releasePiece(Vector2 pos) {
        if(!suspendedPieces.containsKey(pos)) return;
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by releasePiece");

            pieces.put(pos, suspendedPieces.get(pos));
            suspendedPieces.remove(pos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            System.out.println("Mutex released");
        }
    }

    public boolean vacant(Vector2 pos) {
        return !pieces.containsKey(pos);
    }

    /**
     * Calls 'getPiece' on all players, until a match is found (if it exists)
     * @param pos
     * @return Type of piece at the given location (Piece.EMPTY if no match is found)
     */
    public IChessPiece getPiece(Vector2 pos) {
        if(vacant(pos)) return null;
        if(!mutex.tryAcquire()) return null;
        //System.out.println("Mutex tryAcquired by getPiece");

        IChessPiece piece = pieces.get(pos).clonePiece();
        mutex.release();
        //System.out.println("Mutex released");
        return piece;
    }

    protected void putPiece(Vector2 pos, ChessPiece piece) {
        try {
            System.out.println("Attempting to put " + piece + " at " + pos);
            mutex.acquire();

            pieces.put(pos, piece);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            System.out.println("Mutex released");
        }

    }
    protected void setLastPiece(ChessPiece piece) {
        lastPiece = piece;
    }

    protected void capturePiece(ChessPiece piece) {
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by capturePiece");

            capturedPieces.add(piece);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            System.out.println("Mutex released");
        }
    }

    protected void addDrawPos(Vector2 pos) {
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by addDrawPos");

            drawPositions.add(pos);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            System.out.println("Mutex released");
        }
    }

    protected void logMove(MoveNode node) {
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by logMove");

            gameLog.push(node);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            System.out.println("Mutex released");
        }
    }

    /**
     *
     * @return the piece that was last successfully moved
     */
    public IChessPiece getLastPiece() {
        return lastPiece.clonePiece();
    }

    public HashSet<ChessPiece> getCapturedPieces() {
        return (HashSet<ChessPiece>) capturedPieces.clone();
    }

    protected boolean removePiece(Vector2 pos) {
        if(!pieces.containsKey(pos)) return false;
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by removePiece");

            ChessPiece piece = pieces.get(pos);
            pieces.remove(pos);
            capturedPieces.add(piece);
            drawPositions.push(pos);

            mutex.release();
            System.out.println("Mutex released");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            mutex.release();
            return false;
        }
    }
    public boolean performAttack(Vector2 start, Vector2 end, Vector2 victim) {
        try {
            mutex.acquire();
            System.out.println("Mutex acquired by performAttack");

            MoveNode node = new MoveNode(pieces.get(start), start, end, pieces.get(victim));
            System.out.println("Performing attack: " + node);

            removePiece(victim);
            gameLog.add(node);

            mutex.release();
            System.out.println("Mutex released");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            System.out.println("Mutex released");
            return false;
        }
    }

    public Stack<MoveNode> getGameLog() {
        return (Stack<MoveNode>) gameLog.clone();
    }
}
