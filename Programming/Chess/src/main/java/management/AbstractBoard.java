package management;

import pieces.ChessPiece;
import resources.Vector2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.Semaphore;

public abstract class AbstractBoard {
    private Semaphore piecesMutex = new Semaphore(1);

    private HashMap<Vector2, ChessPiece> pieces = new HashMap<>();
    private Stack<Vector2> drawPieces = new Stack<>();
    private HashMap<Vector2, ChessPiece> suspendedPieces = new HashMap<>();
    private HashSet<ChessPiece> inactivePieces = new HashSet<>();
}
