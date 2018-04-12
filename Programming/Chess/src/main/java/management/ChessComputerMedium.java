package management;

import resources.*;

import java.util.*;

public class ChessComputerMedium extends ChessComputer {

    private final int DEPTH = 3000;
    private Alliance enemy;
    private ArrayList<MoveScore> moveChart = new ArrayList<>();
    private int size;
    private int store;


    public ChessComputerMedium(Board board) {
        super(board);
        size = board.size();
    }

    public Move getMove() {
        int turn = 1;
        int[][] chessB = translateBoard();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                score(x,y, chessB, DEPTH, 1, 0);
                turn *= -1;
            }
        }
        return null;
    }

    private int score(int x, int y, int[][] chessB, int depth, int turn, int score) {
                if(depth <= 0) return score;
                else if(chessB[x][y] == 1 * turn) return pawn(x,y, chessB.clone(), depth, turn, score);
                else if (chessB[x][y] == 3 * turn) return Knight(x,y, chessB.clone(), depth, turn, score);
                else if (chessB[x][y] == 4 * turn) return bishop(x,y, chessB.clone(), depth, turn, score);
                else if (chessB[x][y] == 5 * turn) return rook(x,y, chessB.clone(), depth, turn, score);
                else if (chessB[x][y] == 2 * turn) return king(x,y, chessB.clone(), depth, turn, score);
                else if (chessB[x][y] == 9 * turn) return queen(x,y, chessB.clone(), depth, turn, score);
    }

    private int queen(int x, int y, int[][] chessB, int depth, int turn, int score) {
        return diagonals(x,y, chessB, depth, turn, score) + inStraights(x,y, chessB, depth, turn, score);
    }

    private int diagonals(int x, int y, int[][] chessB, int depth, int turn, int score) {
        for (int i = 0; ; i++) {

        }
        score(move(x,y,toX,ToY))
    }

    private int move(int[][] chessB, int fromX, int fromY, int toX, int toY) {
        store = chessB[toX][toY];
        chessB[toX][toY] = chessB[fromX][fromY];
        chessB[fromX][fromY] = 0;
        return store;//store is the killed piece's value
    }

    private int[][] translateBoard() {
        int[][] chessB = new int[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

            }
        }
        return chessB;
    }


}
