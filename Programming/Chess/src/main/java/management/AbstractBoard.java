package management;

import main.Main;
import pieces.*;
import resources.*;
import resources.Console;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class AbstractBoard {
    private Semaphore mutex = new Semaphore(1);
    protected int moveI = 0;

    private static final Piece[] defaultBoard = new Piece[]{
            Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
            Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN,
            Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
            Piece.PAWN, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY,
            Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN, Piece.PAWN,
            Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK
    };

    private int size;
    private int difficulty;
    private Player player1, player2;
    private ChessClock clock = null;
    private ChessPiece lastPiece = null;

    protected Alliance activePlayer = Alliance.WHITE;


    private HashMap<Vector2, ChessPiece> pieces = new HashMap<>();
    private Stack<Vector2> drawPositions = new Stack<>();
    private HashMap<Vector2, ChessPiece> suspendedPieces = new HashMap<>(); // Used to ignore pieces that are still on the board
    private HashSet<PieceNode> capturedPieces = new HashSet<>();

    private Stack<MoveNode> gameLog = new Stack<>();

    protected AbstractBoard(AbstractBoard other) {
        sync(other);
    }


    protected AbstractBoard(int size, int difficulty, boolean useClock) {

        if (size < 2) throw new IllegalArgumentException("The board size must be at least 2");

        this.size = size;

        generateClock(useClock);

        this.difficulty = difficulty;
    }

    protected AbstractBoard(String saveName) throws FileNotFoundException {
        File file = new File(Main.SAVES_DIR, saveName + Main.SAVE_EXTENSION);
        loadBoard(file);
    }
    protected AbstractBoard(File file) throws FileNotFoundException {
        loadBoard(file);
    }
    protected AbstractBoard(File file, int difficulty) throws FileNotFoundException {
        loadBoard(file);
        this.difficulty = difficulty;
    }

    public void sync(AbstractBoard other) {
        moveI = other.moveI;

        size = other.size;
        player1 = other.player1;
        player2 = other.player2;

        if (other.clock != null) clock = other.clock.clone();
        if (other.lastPiece != null) lastPiece = other.lastPiece.clonePiece();

        activePlayer = other.activePlayer;
        pieces = (HashMap<Vector2, ChessPiece>) other.pieces.clone();
        drawPositions = (Stack<Vector2>) other.drawPositions.clone();
        suspendedPieces = (HashMap<Vector2, ChessPiece>) other.suspendedPieces.clone();
        capturedPieces = other.getCapturedPieces();

        gameLog = (Stack<MoveNode>) other.gameLog.clone();

        difficulty = other.difficulty;
    }

    public int moveI() {
        return moveI;
    }

    public int difficulty() { return difficulty; }

    private void loadBoard(File file) throws FileNotFoundException {
        Scanner reader;
        if (file.getName().equals("default" + Main.SAVE_EXTENSION)) {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("default" + Main.SAVE_EXTENSION);
            reader = new Scanner(is);
        } else {
            reader = new Scanner(file);
        }

        Console.printNotice("Attempting to load board from save " + file.getAbsolutePath());

        // Load board data
        size = reader.nextInt();
        int diff = reader.nextInt();
        if(diff >= 0 && diff <= 3)
            difficulty = diff;
        generateClock(reader.nextInt() != 0);
        int logSize = reader.nextInt();
        moveI = reader.nextInt();
        activePlayer = moveI % 2 == 0 ? Alliance.WHITE : Alliance.BLACK;

        int lastX = reader.nextInt(),
        lastY = reader.nextInt();

        // Load pieces on board
        for (int y = 0; y < size; y++) {
            String line = reader.next();
            for (int x = 0; x < size; x++) {
                char c = line.charAt(x);

                Vector2 pos = new Vector2(x, y);
                addPiece(pos, PieceManager.toPiece(c));
            }
        }

        lastPiece = getPiece(new Vector2(lastX, lastY));

        // Load gameLog
        for(int i = 0; i < logSize; i++) {
            String p = reader.next();
            int x0 = reader.nextInt(), y0 = reader.nextInt();
            int x1 = reader.nextInt(), y1 = reader.nextInt();
            String v = reader.next();

            PieceNode piece = PieceManager.toPiece(p.charAt(0)),
                    victim = PieceManager.toPiece(v.charAt(0));
            MoveNode node = new MoveNode(piece, new Vector2(x0, y0), new Vector2(x1, y1), victim);
            gameLog.push(node);
        }

        String nextData = "";
        // Jump to next line
        if(reader.hasNextLine())
            reader.nextLine();
        if(reader.hasNextLine()) {
            nextData = reader.nextLine();
        }

        // Load capturedPieces
        // If the save file doesn't contain capturedPieces, this operation will instead behave as a primer before loading piece data
        if(!nextData.equals(Main.DATA_SEPARATOR)) {
            for (int i = 0; i < nextData.length(); i++) {
                char c = nextData.charAt(i);

                capturedPieces.add(PieceManager.toPiece(c));
            }
        }

        // Load piece data
        while(reader.hasNextLine()) {
            if (nextData.equals(Main.DATA_SEPARATOR)) {
                Console.printNotice("Loading piece data...");

                // Data separator found. Read all data
                while (reader.hasNextInt()) {
                    int x = reader.nextInt(),
                            y = reader.nextInt();
                    Vector2 pos = new Vector2(x, y);

                    List<Boolean> vals = new ArrayList<>();
                    boolean hasMoved = reader.nextInt() == 1;
                    vals.add(hasMoved);

                    ChessPiece piece = getPiece(pos);
                    if (piece instanceof Pawn) {
                        boolean hasDoubleStepped = reader.nextInt() == 1;
                        vals.add(hasDoubleStepped);
                    }
                    piece.loadData(vals);
                    //Console.printNotice("Loading piece " + piece + "\nsource.hasMoved: " + vals.get(0) + ", target.hasMoved: " + piece.hasMoved());

                    removePiece(pos);
                    putPiece(pos, piece);
                }

                // All data loaded. Break the outer loop
                break;
            } else {
                nextData = reader.nextLine();
            }
        }

        reader.close();

        Console.printSuccess("Board successfully loaded from file " + file.getName());
    }

    /**
     * Saves the board's state to a text-file
     * @param file Name of the save (No path/file-extension)
     */
    public void saveBoard(File file) {
        String path = file.getAbsolutePath();
        try {
            FileWriter save = new FileWriter(path);
            int n = size();

            Stack<MoveNode> gameLog = getGameLog();

            // Save board data
            save.write(n + " " + difficulty + " 0 " + gameLog.size() + " " + moveI + "\n");
            Vector2 lastPos = getLastPiece() == null ? new Vector2(-1, -1) : getLastPiece().position();
            save.write(lastPos.getX() + " " + lastPos.getY() + "\n");
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

            // Save gameLog
            for(MoveNode node : gameLog) {
                int x0 = node.start.getX(), y0 = node.start.getY(),
                        x1 = node.end.getX(), y1 = node.end.getY();
                save.write(PieceManager.toSymbol(node.piece) + " " + x0 + " " + y0 + " " + x1 + " " + y1 + " " + PieceManager.toSymbol(node.victimPiece) + "\n");
            }

            // Save capturedPieces
            for(PieceNode p : capturedPieces) {
                save.write(PieceManager.toSymbol(p));
            }
            Console.printNotice(capturedPieces.size() + " captured pieces saved");

            save.write("\n");

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveBoard(String saveName) {
        saveBoard(new File(Main.SAVES_DIR, saveName + Main.SAVE_EXTENSION));
    }

    public void saveLog() {
        // Save board state, before changes are made (Enables undo)
        File logFile = new File(Main.LOGS_DIR, "log" + moveI() + Main.SAVE_EXTENSION);
        saveBoard(logFile);
        logFile.deleteOnExit();
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

    /**
     *
     * @param alliance The alliance of the king to find
     * @return The king on this board with the given alliance
     */
    public King getKing(Alliance alliance) {
        try {
            mutex.acquire();

            for(ChessPiece p : pieces.values()) {
                if(p instanceof King && p.alliance().equals(alliance)) {
                    mutex.release();
                    return (King)p;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return null;
        }

        mutex.release();
        return null;
    }

    public boolean hasKing(Alliance alliance) {
        return getKing(alliance) != null;
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
    public ChessPiece addPiece(Vector2 pos, PieceNode node) {
        return addPiece(pos, node.piece, node.alliance);
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

            pieces.remove(end);

            ChessPiece piece = pieces.get(start).clonePiece();
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

    public HashMap<Vector2, ChessPiece> getPieces() {
        try {
            mutex.acquire();
            HashMap<Vector2, ChessPiece> temp = (HashMap<Vector2, ChessPiece>)pieces.clone();

            mutex.release();
            return temp;
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

            Console.printNotice("Captured piece " + piece);
            capturedPieces.add(new PieceNode(piece.piece(), piece.alliance()));

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
        return lastPiece == null ? null : lastPiece.clonePiece();
    }

    public HashSet<PieceNode> getCapturedPieces() {
        return (HashSet<PieceNode>) capturedPieces.clone();
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
