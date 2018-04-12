package management;

import resources.*;

import java.util.*;

public class ChessComputerMedium extends ChessComputer {

    private final int DEPTH = 3;
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
               score(x,y, chessB, DEPTH, turn, 0);
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
    private int rook(int x, int y, int[][] chessB, int depth, int turn, int score) {
        return inStraights(x,y, chessB, depth, turn, score);
    }
    private int bishop(int x, int y, int[][] chessB, int depth, int turn, int score) {
        return diagonals(x,y, chessB, depth, turn, score);
    }

    private int diagonals(int x, int y, int[][] chessB, int depth, int turn, int score) {
        int sum = score;
        int[][] clone;
        for (int i = 0; i < size ; i++) {
            clone = chessB.clone();
            sum += move(clone,x,y,x,y);
            sum += calcOtherSide(clone, depth, turn * -1, 0);
            clone = chessB.clone();
            sum += move(clone,x,y,x++,y++);
            sum += calcOtherSide(clone, depth, turn * -1, 0);
            clone = chessB.clone();
            sum += move(clone,x,y,x++,y--);
            sum += calcOtherSide(clone, depth, turn * -1, 0);
            clone = chessB.clone();
            sum += move(clone,x,y,x--,y++);
            sum += calcOtherSide(clone, depth, turn * -1, 0);
        }
        return sum;
    }
    private int inStraights(int x, int y, int[][] chessB, int depth, int turn, int score) {
        int sum = score;
        int[][] clone;
        for (int i = 0; i < size ; i++) {
            clone = chessB.clone();
            sum += move(clone,x,y,x,y--);
            sum += calcOtherSide(clone), depth, turn * -1, 0);
            sum += move(clone,x,y,x,y++);
            sum += calcOtherSide(chessB.clone(), depth, turn * -1, 0);
            sum += move(clone,x,y,x++,y);
            sum += calcOtherSide(chessB.clone(), depth, turn * -1, 0);
            sum += move(clone,x,y,x--,y);
            sum += calcOtherSide(chessB.clone(), depth, turn * -1, 0);
        }
        return sum;
    }

    private int calcOtherSide(int[][] chessB, int depth, int turn, int score) {
        int sum = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                sum += score(x,y, chessB, depth, turn, score);
            }
        }
        return sum + score;
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
