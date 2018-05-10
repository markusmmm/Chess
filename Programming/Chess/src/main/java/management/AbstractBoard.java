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

    private int size;
    private int difficulty;
    private Player player1, player2;

    private ChessClock clock = null;
    private IChessPiece lastMovedPiece = null;

    protected Alliance activePlayer = Alliance.WHITE;

    private HashMap<Vector2, IChessPiece> pieces = new HashMap<>();          // All active pieces on the board
    private Stack<Vector2> drawPositions = new Stack<>();                    // Positions where graphics has to be re-drawn
    private HashMap<Vector2, IChessPiece> suspendedPieces = new HashMap<>(); // Used to ignore pieces that are still on the board
    private HashSet<PieceNode> capturedPieces = new HashSet<>();             // Pieces that are taken off the board

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

    /**
     * Constructs a board from a given save file
     * @param saveName Name of save file (without file extension)
     * @throws FileNotFoundException
     */
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

    protected void sync(AbstractBoard other) {
        try {
            mutex.acquire();

            moveI = other.moveI;

            size = other.size;
            difficulty = other.difficulty;
            player1 = other.player1;
            player2 = other.player2;

            if (other.clock != null) clock = other.clock.clone();
            if (other.lastMovedPiece != null) lastMovedPiece = other.lastMovedPiece.clonePiece();

            activePlayer = other.activePlayer;

            pieces = (HashMap<Vector2, IChessPiece>) other.pieces.clone();
            drawPositions = (Stack<Vector2>) other.drawPositions.clone();
            suspendedPieces = (HashMap<Vector2, IChessPiece>) other.suspendedPieces.clone();
            capturedPieces = other.getCapturedPieces();

            gameLog = (Stack<MoveNode>) other.gameLog.clone();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }

    public int moveI() {
        return moveI;
    }

    public int difficulty() { return difficulty; }

    public ChessClock getClock() {
        return clock == null ? null : clock.clone();
    }

    /**
     * Loads the board from a given save-file
     * @param file Save-file to load board from
     * @throws FileNotFoundException if file doesn't exist, or is null
     */
    private void loadBoard(File file) throws FileNotFoundException {
        Scanner reader;
        if (file.getName().equals("default" + Main.SAVE_EXTENSION)) {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("default" + Main.SAVE_EXTENSION);
            reader = new Scanner(is);
        } else {
            reader = new Scanner(file);
        }

        // Load board data
        size = reader.nextInt();
        int diff = reader.nextInt();
        if(diff >= 0 && diff <= 3) difficulty = diff;

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

        lastMovedPiece = getPiece(new Vector2(lastX, lastY));

        // Load gameLog
        for(int i = 0; i < logSize; i++) {
            String p = reader.next();
            int x0 = reader.nextInt(), y0 = reader.nextInt(),
                    x1 = reader.nextInt(), y1 = reader.nextInt();
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
        // Read next line of data, if it exists
        if(reader.hasNextLine()) {
            nextData = reader.nextLine();
        }

        // Load capturedPieces
        if(!nextData.equals(Main.DATA_SEPARATOR)) {
            for (int i = 0; i < nextData.length(); i++) {
                char c = nextData.charAt(i);
                capturedPieces.add(PieceManager.toPiece(c));
            }
        }

        loadPieceData:
        while(reader.hasNextLine()) {
            if (nextData.equals(Main.DATA_SEPARATOR)) {
                // Data separator found. Read all data
                while (reader.hasNextInt()) {
                    int x = reader.nextInt(),
                            y = reader.nextInt();
                    Vector2 pos = new Vector2(x, y);

                    List<Boolean> values = new ArrayList<>();
                    boolean hasMoved = reader.nextInt() == 1;
                    values.add(hasMoved);

                    IChessPiece piece = getPiece(pos);
                    if (piece != null) {
                        if (piece instanceof Pawn) {
                            boolean hasDoubleStepped = reader.nextInt() == 1;
                            values.add(hasDoubleStepped);
                        }
                        piece.loadData(values);

                        removePiece(pos);
                        putPiece(pos, piece);
                    }
                }

                // All data loaded. Break the outermost loop
                break loadPieceData;
            } else
                nextData = reader.nextLine();
        }

        reader.close();
    }

    /**
     * Saves the board's state to a text-file
     * @param file Target file
     */
    public void saveBoard(File file) {
        if(!file.exists()) {
            try {
                if(!file.createNewFile()) Console.printError("Save creation was interrupted");
            } catch (IOException e) {
                throw new IllegalStateException("An error occurred while creating save file " + file.getName());
            }
        }

        String path = file.getAbsolutePath();
        try {
            FileWriter save = new FileWriter(path);
            int n = size();

            Stack<MoveNode> gameLog = getGameLog();

            // Save board data
            // Chess-clock state is always saved as 0, until it is fully implemented
            save.write(n + " " + difficulty + " 0 " + gameLog.size() + " " + moveI + "\n");
            Vector2 lastPos = getLastMovedPiece() == null ? new Vector2(-1, -1) : getLastMovedPiece().position();
            save.write(lastPos.getX() + " " + lastPos.getY() + "\n");
            for (int y = 0; y < n; y++) {
                String line = "";
                for (int x = 0; x < n; x++) {
                    IChessPiece p = getPiece(new Vector2(x, y));
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

            save.write("\n");

            // Save internal data for each piece on board
            save.write(Main.DATA_SEPARATOR + "\n");
            HashMap<Vector2, IChessPiece> pieces = getPieces();
            for(Vector2 pos : pieces.keySet()) {
                IChessPiece piece = pieces.get(pos);
                save.write(pos.getX() + " " + pos.getY() + " " + (piece.hasMoved() ? 1 : 0));

                if(piece instanceof Pawn)
                    save.write(" " + ( ((Pawn)piece).hasDoubleStepped() ? 1 : 0));

                save.write("\n");
            }

            save.close();
            //Console.printSuccess("Board saved to " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the board's state in a log
     * Logs are a sequence of save files, indexed in chronological order, which portrays change of the board's state over time
     */
    protected void saveLog() {
        // Save board state, before changes are made (Enables undo)
        File logFile = new File(Main.LOGS_DIR, "log" + moveI() + Main.SAVE_EXTENSION);
        saveBoard(logFile);
        logFile.deleteOnExit();
    }

    /**
     * @return A random piece type
     */
    public static Piece randomPiece() {
        int pick = new Random().nextInt(Piece.values().length);
        return Piece.values()[pick];
    }

    private void generateClock(boolean doGenerate) {
        if (!doGenerate) return;
        clock = new ChessClock(2, 300, 5);
    }

    /**
     * @return If the board's state is ready to be altered
     */
    public boolean ready() {
        return mutex.availablePermits() == 1;
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

    public boolean hasKing(Alliance alliance) {
        return getKing(alliance) != null;
    }

    public boolean vacant(Vector2 pos) {
        return !pieces.containsKey(pos);
    }

    protected IChessPiece addPiece(Vector2 pos, Piece type, Alliance alliance) {
        try {
            mutex.acquire();

            IChessPiece piece = createPiece(pos, type, alliance);
            if (piece == null) {
                mutex.release();
                return null;
            }

            pieces.put(pos, piece);
            drawPositions.push(pos);

            mutex.release();
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return null;
        }
    }
    protected IChessPiece addPiece(Vector2 pos, PieceNode node) {
        return addPiece(pos, node.piece, node.alliance);
    }

    private IChessPiece createPiece(Vector2 pos, Piece type, Alliance alliance) {
        switch (type) {
            case BISHOP:
                return new Bishop(pos, alliance, this, false);
            case KNIGHT:
                return new Knight(pos, alliance, this, false);
            case QUEEN:
                return new Queen(pos, alliance, this, false);
            case KING:
                return new King(pos, alliance, this, false);
            case PAWN:
                return new Pawn(pos, alliance, this, false, false);
            case ROOK:
                return new Rook(pos, alliance, this, false);
        }
        return null;
    }

    protected boolean forceMovePiece(Vector2 start, Vector2 end) {
        if(!(pieces.containsKey(start) && insideBoard(end))) return false;

        try {
            mutex.acquire();
            // Extract the piece before removing it from the map, so that it can be added back at a different position
            IChessPiece piece = pieces.get(start);

            pieces.remove(end);
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

    public void suspendPieces(Vector2... positions) {
        try {
            mutex.acquire();
            for (Vector2 pos : positions) {
                if (!pieces.containsKey(pos)) continue;
                suspendedPieces.put(pos, pieces.get(pos));
                pieces.remove(pos);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }

    public void releasePieces(Vector2... positions) {
        try {
            mutex.acquire();
            for (Vector2 pos : positions) {
                if (!suspendedPieces.containsKey(pos)) continue;

                pieces.put(pos, suspendedPieces.get(pos));
                suspendedPieces.remove(pos);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }

    protected void putPiece(Vector2 pos, IChessPiece piece) {
        try {
            mutex.acquire();
            pieces.put(pos, piece.clone());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }

    }

    protected boolean removePiece(Vector2 pos) {
        if (!pieces.containsKey(pos)) return false;
        try {
            mutex.acquire();
            pieces.remove(pos);
            drawPositions.push(pos);
            mutex.release();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            mutex.release();
            return false;
        }
    }

    protected void setLastMovedPiece(IChessPiece piece) {
        lastMovedPiece = piece;
    }

    protected void capturePiece(IChessPiece piece) {
        try {
            mutex.acquire();
            Console.printNotice("Captured piece " + piece);
            capturedPieces.add(new PieceNode(piece.piece(), piece.alliance()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }

    public Stack<Vector2> clearDrawPieces() {
        try {
            mutex.acquire();
            Stack<Vector2> result = (Stack<Vector2>) drawPositions.clone();
            drawPositions.clear();

            mutex.release();
            return result;
        } catch (InterruptedException e) {
            System.err.println("clearDrawPieces was interrupted");
            e.printStackTrace();

            mutex.release();
            return null;
        }

    }

    protected void addDrawPos(Vector2... positions) {
        try {
            mutex.acquire();
            for (Vector2 pos : positions)
                drawPositions.add(pos);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }

    protected void logMove(MoveNode node) {
        try {
            mutex.acquire();
            gameLog.push(node);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
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

            for(IChessPiece p : pieces.values()) {
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

    /**
     * @return the piece that was last successfully moved
     */
    public IChessPiece getLastMovedPiece() {
        return lastMovedPiece == null ? null : lastMovedPiece.clonePiece();
    }

    public HashSet<PieceNode> getCapturedPieces() {
        return (HashSet<PieceNode>) capturedPieces.clone();
    }

    public Stack<MoveNode> getGameLog() {
        return (Stack<MoveNode>) gameLog.clone();
    }

    /**
     * Calls 'getPiece' on all players, until a match is found (if it exists)
     *
     * @param pos
     * @return Type of piece at the given location (Piece.EMPTY if no match is found)
     */
    public IChessPiece getPiece(Vector2 pos) {
        if (vacant(pos)) return null;

        try {
            mutex.acquire();
            ////resources.Console.println("Mutex acquired by getPiece");

            IChessPiece piece = pieces.get(pos).clonePiece();
            mutex.release();
            ////resources.Console.println("Mutex released");
            return piece;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return null;
        }
    }

    public HashMap<Vector2, IChessPiece> getPieces() {
        try {
            mutex.acquire();
            HashMap<Vector2, IChessPiece> temp = (HashMap<Vector2, IChessPiece>)pieces.clone();

            mutex.release();
            return temp;
        } catch (InterruptedException e) {
            e.printStackTrace();

            mutex.release();
            return null;
        }
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
}
