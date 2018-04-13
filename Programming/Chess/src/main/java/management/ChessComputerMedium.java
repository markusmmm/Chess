package management;

import pieces.*;
import resources.*;

import java.util.*;

public class ChessComputerMedium extends ChessComputer {

    private final int DEPTH = 3;
    private Alliance enemy;
    private final Alliance black = Alliance.BLACK;
    private final Alliance white = Alliance.WHITE;
    private ArrayList<MoveScore> moveChart = new ArrayList<>();
    private ArrayList<Move> moveStorage = new ArrayList<>();
    private Boolean[] stillMoving = new Boolean[4];
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
               getMoves(x,y, chessB, DEPTH, turn, 0);
            }
        }
        return null;
    }

    /**
     * ads positions to moveStorage. needs to be cleared between boards
     * @param x
     * @param y
     * @param chessB
     * @param turn
     * @return
     */
    private ArrayList<Move> getMoves(int x, int y, int[][] chessB, int turn) {
        if(chessB[x][y] == 1 * turn)  pawn(x,y, chessB);
        else if (chessB[x][y] == 3 * turn) Knight(x,y, chessB);
        else if (chessB[x][y] == 4 * turn) bishop(x,y, chessB);
        else if (chessB[x][y] == 5 * turn) rook(x,y, chessB);
        else if (chessB[x][y] == 2 * turn) king(x,y, chessB);
        else if (chessB[x][y] == 9 * turn) queen(x,y, chessB);
    }

    private void queen(int x, int y, int[][] chessB) {
        diagonals(x,y, chessB);
        straights(x,y, chessB);
    }
    private void rook(int x, int y, int[][] chessB, int depth, int turn, int score) {
        straights(x,y, chessB);
    }
    private void bishop(int x, int y, int[][] chessB) {
        diagonals(x,y, chessB);
    }

    private void diagonals(int x, int y, int[][] chessB) {
        activateMoves();
        while (movesLeft()) {
            if(stillMoving[0]) evalMoveDone(x, y,--x, ++y, chessB, 0);
            if(stillMoving[1]) evalMoveDone(x, y,--x, ++y, chessB, 1);
            if(stillMoving[2]) evalMoveDone(x, y,--x, --y, chessB, 2);
            if(stillMoving[3]) evalMoveDone(x, y,++x, --y, chessB, 3);
        }
    }
    private void straights(int x, int y, int[][] chessB) {
        activateMoves();
        while (movesLeft()) {
            if(stillMoving[0]) evalMoveDone(x, y,x, ++y, chessB, 0);
            if(stillMoving[1]) evalMoveDone(x, y,x, --y, chessB, 1);
            if(stillMoving[2]) evalMoveDone(x, y,--x, y, chessB, 2);
            if(stillMoving[3]) evalMoveDone(x, y,++x, y, chessB, 3);
        }
    }

    /**
     * sets all booleans in stillMoving to true
     */
    private void activateMoves() {
        for (int i = 0; i < stillMoving.length; i++) {
            stillMoving[i] = true;
        }
    }

    private boolean movesLeft() {
        for (int i = 0; i < stillMoving.length; i++) {
            if(stillMoving[i]) return true;
        }
        return false;
    }
    private void evalMoveDone(int fromX, int fromY, int toX, int  toY, int[][] chessB, int bolIndex) {
        if(chessB[toX][toY] == 0 && insideBoard(toX,toY)) {
            moveStorage.add(new Move (new Vector2(fromX,fromY), new Vector2(toX, toY)));
        } else stillMoving[bolIndex] = false;
    }

    private int calcOtherSide(int fromX, int fromY, int toX, int toY, int[][] chessB, int depth, int turn, int score) {
        int sum = 0;
        int[][] clone = chessB.clone();
        sum += move(clone,fromX, fromY, toX, toY);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                sum += getMoves(x,y, chessB, depth, turn, score);
            }
        }
        return sum + score;
    }

    private int move(int[][] chessB, int fromX, int fromY, int toX, int toY) {
        if(insideBoard(fromX,fromY) && insideBoard(toX,toY)) {
            return 0;
        }
        store = chessB[toX][toY];
        chessB[toX][toY] = chessB[fromX][fromY];
        chessB[fromX][fromY] = 0;
        return store;//store is the killed piece's value
    }

    private boolean insideBoard(int fromX, int fromY) {
        return (0 <= fromX && fromX <= size &&
        0 <= fromY && fromY <= size);
    }

    private int[][] translateBoard() {
        int[][] chessB = new int[size][size];
        ChessPiece selectedPiece;
        Vector2 position;
        Alliance color;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                position = new Vector2(x,y);
                selectedPiece = (ChessPiece) board.getPiece(position);
                color = selectedPiece.alliance();
                if(selectedPiece == null) continue;
                else if (selectedPiece instanceof Pawn && color == black) chessB[x][y] = 1;
                else if (selectedPiece instanceof Pawn && color == white) chessB[x][y] = -1;
                else if (selectedPiece instanceof Knight && color == black) chessB[x][y] = 3;
                else if (selectedPiece instanceof Knight && color == white) chessB[x][y] = -3;
                else if (selectedPiece instanceof Bishop && color == black) chessB[x][y] = 4;
                else if (selectedPiece instanceof Bishop && color == white) chessB[x][y] = -4;
                else if (selectedPiece instanceof Rook && color == black) chessB[x][y] = 5;
                else if (selectedPiece instanceof Rook && color == white) chessB[x][y] = -5;
                else if (selectedPiece instanceof King && color == black) chessB[x][y] = 2;
                else if (selectedPiece instanceof King && color == white) chessB[x][y] = -2;
                else if (selectedPiece instanceof Queen && color == black) chessB[x][y] = 9;
                else if (selectedPiece instanceof Queen && color == white) chessB[x][y] = -9;
            }
        }
        return chessB;
    }
    public void printChessb(int[][] chessB) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (chessB[x][y] == 1) System.out.print('p');       //pawn
                else if (chessB[x][y] == -1) System.out.print('P');
                else if (chessB[x][y] == 3) System.out.print('h');  //Knight
                else if (chessB[x][y] == -3) System.out.print('H');
                else if (chessB[x][y] == 4) System.out.print('b');  //Bishop
                else if (chessB[x][y] == -4) System.out.print('B');
                else if (chessB[x][y] == 5) System.out.print('r');  //Rook
                else if (chessB[x][y] == -5) System.out.print('R');
                else if (chessB[x][y] == 2) System.out.print('k');  //King
                else if (chessB[x][y] == -2) System.out.print('K');
                else if (chessB[x][y] == 9) System.out.print('q');  //Queen
                else if (chessB[x][y] == -9) System.out.print('Q');
            }
            System.out.println();
        }
    }
}
