package management;

import pieces.*;
import resources.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class AbstractBoard {
    private Semaphore mutex = new Semaphore(1);
    private final boolean isLive;

    private boolean hasWhiteKing, hasBlackKing;

    private static final Piece[] defaultBoard = new Piece[] {
            Piece.ROOK,   Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN,  Piece.KING,   Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
            Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,
            Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,
            Piece.PAWN,   Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,
            Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,
            Piece.ROOK,   Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN,  Piece.KING,   Piece.BISHOP, Piece.KNIGHT, Piece.ROOK
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

    protected AbstractBoard(AbstractBoard other, boolean isLive) {
        this.isLive = isLive;

        hasBlackKing = other.hasBlackKing;
        hasWhiteKing = other.hasWhiteKing;

        size = other.size;
        player1 = other.player1;
        player2 = other.player2;

        if(clock != null) clock = other.clock.clone();
        if(lastPiece != null) lastPiece = other.lastPiece.clonePiece();

        activePlayer = other.activePlayer;
        pieces = (HashMap<Vector2, ChessPiece>) other.pieces.clone();
        drawPositions = (Stack<Vector2>) other.drawPositions.clone();
        suspendedPieces = (HashMap<Vector2, ChessPiece>) other.suspendedPieces.clone();
        capturedPieces = (HashSet<ChessPiece>) other.capturedPieces.clone();

        gameLog = (Stack<MoveNode>) other.gameLog.clone();
    }

    protected AbstractBoard(int size, boolean useClock, BoardMode mode, boolean isLive) {
        if(size < 2) throw new IllegalArgumentException("The board size must be at least 2");

        this.size = size;
        this.isLive = isLive;

        generateClock(useClock);

        if(mode == BoardMode.DEFAULT) {
            int p = 0;

            for (Piece type : defaultBoard) {
                int x = p % size;
                int y = p / size;

                Vector2 pos = new Vector2(x, y);
                Vector2 invPos = new Vector2(x, size - y - 1);

                if (type.equals(Piece.EMPTY)) continue;

                addPiece(pos, type, Alliance.BLACK);
                System.out.println(pos + ": " + pieces.get(pos));

                addPiece(invPos, type, Alliance.WHITE);
                System.out.println(invPos + ": " + pieces.get(invPos));

                p++;
            }
        } else {
            if (mode == BoardMode.RANDOM) {
                int bRooks = 0, bPawns = 0, bQueens = 0, bKings = 0, bBishops = 0, bKnights = 0;
                int wRooks = 0, wPawns = 0, wQueens = 0, wKings = 0, wBishops = 0, wKnights = 0;
                int w = 0;
                int b = 0;
                int q = 0;

                int p = 0;
                Random random = new Random();
                while (b < 16) {
                    Piece aPiece = randomPiece();
                    int x =random.nextInt(7 - 0 + 1) + 0;
                    int y = random.nextInt(7 - 0 + 1) + 0;

                    Vector2 pos = new Vector2(x, y);
                    if(!vacant(pos)){
                        p++;
                        continue;
                    }
                    if (aPiece.equals(Piece.ROOK) && bRooks < 2) {
                        addPiece(pos, aPiece, Alliance.BLACK);
                        p++;
                        b++;
                        bRooks++;
                        continue;
                    }
                    if (aPiece.equals(Piece.PAWN) && bPawns < 8) {
                        addPiece(pos, aPiece, Alliance.BLACK);
                        p++;
                        b++;
                        bPawns++;
                        continue;
                    }
                    if (aPiece.equals(Piece.QUEEN) && bQueens < 1) {
                        addPiece(pos, aPiece, Alliance.BLACK);
                        p++;
                        b++;
                        bQueens++;
                        continue;
                    }
                    if (aPiece.equals(Piece.KING) && bKings == 0) {
                        addPiece(pos, aPiece, Alliance.BLACK);
                        p++;
                        b++;
                        bKings++;
                        continue;
                    }
                    if (aPiece.equals(Piece.BISHOP) && bBishops < 2) {
                        addPiece(pos, aPiece, Alliance.BLACK);
                        p++;
                        b++;
                        bBishops++;
                        continue;
                    }
                    if (aPiece.equals(Piece.KNIGHT) && bKnights < 2) {
                        addPiece(pos, aPiece, Alliance.BLACK);
                        p++;
                        b++;
                        bKnights++;
                        continue;
                    }
                    if(aPiece.equals(Piece.EMPTY)) {
                        b++;
                        continue;
                    }
                }
                while (w < 16) {
                    Piece aPiece = randomPiece();

                    int x =random.nextInt(7 - 0 + 1) + 0;
                    int y = random.nextInt(7 - 0 + 1) + 0;

                    Vector2 invPos = new Vector2(x, y);
                    if(!vacant(invPos)){
                        q++;
                        continue;
                    }
                    if (aPiece.equals(Piece.ROOK) && wRooks < 2) {
                        addPiece(invPos, aPiece, Alliance.WHITE);
                        q++;
                        w++;
                        wRooks++;
                        continue;
                    }
                    if (aPiece.equals(Piece.PAWN) && wPawns <8) {
                        addPiece(invPos, aPiece, Alliance.WHITE);
                        q++;
                        w++;
                        wPawns++;
                        continue;
                    }
                    if (aPiece.equals(Piece.QUEEN) && wQueens < 1) {
                        addPiece(invPos, aPiece, Alliance.WHITE);
                        q++;
                        w++;
                        wQueens++;
                        continue;
                    }
                    if (aPiece.equals(Piece.KING) && wKings < 1) {
                        addPiece(invPos, aPiece, Alliance.WHITE);
                        q++;
                        w++;
                        wKings++;
                        continue;
                    }
                    if (aPiece.equals(Piece.BISHOP) && wBishops < 2) {
                        addPiece(invPos, aPiece, Alliance.WHITE);
                        q++;
                        w++;
                        wBishops++;
                        continue;
                    }
                    if (aPiece.equals(Piece.KNIGHT) && wKnights < 2) {
                        addPiece(invPos, aPiece, Alliance.WHITE);
                        q++;
                        w++;
                        wKnights++;
                        continue;
                    }
                    if(aPiece.equals(Piece.EMPTY)) {
                        b++;
                        continue;
                    }
                }
            }
        }
    }

    protected AbstractBoard(String fileName) throws FileNotFoundException {
        fileName += ".txt";

        Scanner file = new Scanner(new File(fileName));
        size = file.nextInt();
        generateClock(file.nextInt() != 0);
        isLive = true;

        System.out.println("Size read from file: " + size);

        for (int y = 0; y < size; y++) {
            String line = file.next();
            for (int x = 0; x < size; x++) {
                char c = line.charAt(x);

                Vector2 pos = new Vector2(x, y);
                Piece type = PieceManager.toPiece(c);
                Alliance alliance = Character.isLowerCase(c) ? Alliance.BLACK : Alliance.WHITE;

                System.out.println("Setting piece at " + pos);
                addPiece(pos, type, alliance);
            }
        }

        file.close();
    }

    private static Piece randomPiece() {
        int pick = new Random().nextInt(Piece.values().length);
        return Piece.values()[pick];
    }

    private void generateClock(boolean doGenerate) {
        if(!doGenerate) return;
        clock = new ChessClock(2, 900, 12, -1);
    }

    public boolean ready() {
        return mutex.availablePermits() == 1;
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
    public Set<Vector2> getPositions() {
        try {
            mutex.acquire();

            Set<Vector2> temp = pieces.keySet();
            Set<Vector2> positions = new HashSet<>();
            for(Vector2 p : temp)
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
            //System.out.println("Mutex acquired by clearDrawPieces");

            Stack<Vector2> result = (Stack<Vector2>) drawPositions.clone();
            drawPositions.clear();

            mutex.release();
            //System.out.println("Mutex released");
            return result;
        } catch (InterruptedException e) {
            System.err.println("clearDrawPieces was interrupted");
            e.printStackTrace();

            mutex.release();
            //System.out.println("Mutex released");
            return null;
        }

    }

    public ChessPiece addPiece(Vector2 pos, Piece type, Alliance alliance) {
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by addPiece");

            ChessPiece piece = createPiece(pos, type, alliance);
            if(piece == null) {
                mutex.release();
                //System.out.println("Mutex released");
                return null;
            }

            pieces.put(pos, piece);
            drawPositions.push(pos);

            mutex.release();
            //System.out.println("Mutex released");
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            //System.out.println("Mutex released");
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
                if(alliance == Alliance.WHITE)
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

    public boolean hasKing(Alliance alliance) {
        return alliance == Alliance.WHITE ? hasWhiteKing : hasBlackKing;
    }

    public boolean transformPiece(Vector2 pos, Piece newType) {
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by transformPiece");

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
            //System.out.println("Mutex released");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //System.out.println("Mutex released");
            return false;
        }
    }

    public void suspendPieces(Vector2... positions) {
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by suspendPieces");

            for(Vector2 pos : positions) {
                if(!pieces.containsKey(pos)) continue;
                suspendedPieces.put(pos, pieces.get(pos));
                pieces.remove(pos);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //System.out.println("Mutex released");
        }
    }
    public void releasePieces(Vector2... positions) {
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by releasePieces");

            for(Vector2 pos : positions) {
                if(!suspendedPieces.containsKey(pos)) continue;

                pieces.put(pos, suspendedPieces.get(pos));
                suspendedPieces.remove(pos);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //System.out.println("Mutex released");
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
        //if(!mutex.tryAcquire()) return null;
        ////System.out.println("Mutex tryAcquired by getPiece");

        try {
            mutex.acquire();
            ////System.out.println("Mutex acquired by getPiece");

            IChessPiece piece = pieces.get(pos).clonePiece();
            mutex.release();
            ////System.out.println("Mutex released");
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return null;
        }


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
            //System.out.println("Mutex released");
        }

    }
    protected void setLastPiece(ChessPiece piece) {
        lastPiece = piece;
    }

    protected void capturePiece(ChessPiece piece) {
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by capturePiece");

            capturedPieces.add(piece);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //System.out.println("Mutex released");
        }
    }

    public void addDrawPos(Vector2... positions) {
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by addDrawPos");

            for(Vector2 pos : positions)
                drawPositions.add(pos);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //System.out.println("Mutex released");
        }
    }

    protected void logMove(MoveNode node) {
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by logMove");

            gameLog.push(node);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            //System.out.println("Mutex released");
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

    public boolean removePiece(Vector2 pos) {
        if(!pieces.containsKey(pos)) return false;
        try {
            mutex.acquire();
            //System.out.println("Mutex acquired by removePiece");

            ChessPiece piece = pieces.get(pos);
            pieces.remove(pos);
            capturedPieces.add(piece);
            drawPositions.push(pos);

            mutex.release();
            //System.out.println("Mutex released");
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
            //System.out.println("Mutex acquired by removePieces");

            for(Vector2 pos : positions) {
                if(!pieces.containsKey(pos)) continue;

                ChessPiece piece = pieces.get(pos);
                pieces.remove(pos);
                capturedPieces.add(piece);
                drawPositions.push(pos);
            }

            mutex.release();
            //System.out.println("Mutex released");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }

    public void performAttack(Vector2 start, Vector2 end, Vector2 victim) {

            //System.out.println("Mutex acquired by performAttack");

            MoveNode node = new MoveNode(pieces.get(start), start, end, pieces.get(victim));
            System.out.println("Performing attack: " + node);

            removePiece(victim);
            gameLog.add(node);




    }

    public Stack<MoveNode> getGameLog() {
        return (Stack<MoveNode>) gameLog.clone();
    }
}
