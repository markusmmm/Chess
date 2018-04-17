package management;

import pieces.*;
import resources.*;

import java.util.*;

public class ChessComputerMedium extends ChessComputer {

    private final int DEPTH = 3;
    private Alliance enemy;
    private final Alliance black = Alliance.BLACK;
    private final Alliance white = Alliance.WHITE;
    private Boolean[] stillMoving = new Boolean[4];
    private ArrayList<Move> emptyMoveList = new ArrayList<>();
    private Move emptyMove = new Move(new Vector2(0,0), new Vector2(0,0));
    private int size;
    private int store;


    public ChessComputerMedium(Board board) {
        super(board);
        size = board.size();
    }

    public Move getMove() {
        int turn = 1;
        int score;
        int[][] chessB = translateBoard();
        ArrayList<Move> moveStorage = allMovesOneSide(chessB, turn);
        ArrayList<MoveScore> moveChart = new ArrayList<>();
        for (int i = 0; i < moveStorage.size(); i++) {
            if(nonEmptyMove(moveStorage, i)){
                score = scoreMove(chessB.clone(), moveStorage.get(i), DEPTH, turn);
                moveChart.add(new MoveScore(score, moveStorage.get(i)));
            }
        }
        printMoves(moveChart);
        return Collections.min(moveChart).getMove();
    }

    private boolean nonEmptyMove(ArrayList<Move> moveStorage, int i) {
        Vector2 start = moveStorage.get(i).start;
        Vector2 end = moveStorage.get(i).end;
        return start.getX() != 0 && start.getY() != 0 && end.getX() != 0 && end.getX() != 0;
    }

    private int scoreMove(int[][] chessB, Move move, int depth, int turn) {
        if(depth <= 0) return 0;
        int score;
        ArrayList<Move> moves = new ArrayList<>();
        score = PerformMove(chessB, move);
        moves = allMovesOneSide(chessB,turn * -1);

        for (int i = 0; i < moves.size(); i++) {
            score += scoreMove(chessB.clone(), moves.get(i),depth - 1, turn);
        }
        return score;
    }
    public void printMoves(ArrayList<MoveScore> m) {
        for(MoveScore move: m) {
            System.out.println(move.toString());
        }
    }


    private ArrayList<Move> allMovesOneSide(int[][] chessB, int turn) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                moves.addAll(genMovesPiece(x,y, chessB, turn));
            }
        }
        return moves;
    }

    /**
     * ads positions to moveStorage. needs to be cleared between boards
     * @param x
     * @param y
     * @param chessB
     * @param turn
     * @return
     */
    private ArrayList<Move> genMovesPiece(int x, int y, int[][] chessB, int turn) {
        if(chessB[x][y] == 1 * turn) return pawn(x,y, chessB, turn);
        else if (chessB[x][y] == 3 * turn) return knight(x,y, chessB);
        else if (chessB[x][y] == 4 * turn) return bishop(x,y, chessB);
        else if (chessB[x][y] == 5 * turn) return rook(x,y, chessB);
        else if (chessB[x][y] == 2 * turn) return king(x,y, chessB);
        else if (chessB[x][y] == 9 * turn) return queen(x,y, chessB);
        return emptyMoveList;
    }

    private ArrayList<Move> pawn(int x, int y, int[][] chessB, int turn) {
        int pawnMoveLength = 1;
        if(y == 6 || y == 1) pawnMoveLength = 2;
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < pawnMoveLength; i++) {
            moves.add(pawnForward(x, y ,x , y + 1 * turn, chessB));
            moves.add(pawnAttack(x,y,x + 1, y + 1 + 1 * turn, chessB));
            moves.add(pawnAttack(x,y,x - 1, y + 1 + 1 * turn, chessB));
        }
        return moves;
    }

    private Move pawnAttack(int fromX, int fromY, int toX, int toY, int[][] chessB) {
        if(insideBoard(toX, toY) && chessB[toX][toY] != 0){
            return new Move(new Vector2(fromX, fromY), new Vector2(toX, toY));
        }
        return emptyMove;
    }

    private Move pawnForward(int fromX, int fromY, int toX, int toY, int[][] chessB) {
        if(chessB[toX][toY] == 0) return new Move(new Vector2(fromX, fromY), new Vector2(toX, toY));
        return emptyMove;
    }

    private ArrayList<Move> king(int x, int y, int[][] chessB) {
        return new ArrayList<Move>();
    }

    private ArrayList<Move> knight(int x, int y, int[][] chessB) {
        return new ArrayList<Move>();
    }

    private ArrayList<Move> queen(int x, int y, int[][] chessB) {
        ArrayList<Move> moves = new ArrayList<>();
        moves.addAll(diagonals(x,y, chessB));
        moves.addAll(straights(x,y,chessB));
        return  moves;
    }
    private ArrayList<Move> rook(int x, int y, int[][] chessB) {
        return straights(x,y, chessB);
    }
    private ArrayList<Move> bishop(int x, int y, int[][] chessB) {
        return diagonals(x,y, chessB);
    }

    private ArrayList<Move> diagonals(int x, int y, int[][] chessB) {
        ArrayList<Move> moves = new ArrayList<>();
        activateMoves();
        while (movesLeft()) {
            if(stillMoving[0]) moves.add(evalPathMove(x, y,--x, ++y, chessB, 0));
            if(stillMoving[1]) moves.add(evalPathMove(x, y,--x, ++y, chessB, 1));
            if(stillMoving[2]) moves.add(evalPathMove(x, y,--x, --y, chessB, 2));
            if(stillMoving[3]) moves.add(evalPathMove(x, y,++x, --y, chessB, 3));
        }
        return moves;
    }
    private ArrayList<Move> straights(int x, int y, int[][] chessB) {
        ArrayList<Move> moves = new ArrayList<>();
        activateMoves();
        while (movesLeft()) {
            if(stillMoving[0]) moves.add(evalPathMove(x, y,x, ++y, chessB, 0));
            if(stillMoving[1]) moves.add(evalPathMove(x, y,x, --y, chessB, 1));
            if(stillMoving[2]) moves.add(evalPathMove(x, y,--x, y, chessB, 2));
            if(stillMoving[3]) moves.add(evalPathMove(x, y,++x, y, chessB, 3));
        }
        return moves;
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
    private Move evalPathMove(int fromX, int fromY, int toX, int  toY, int[][] chessB, int bolIndex) {
        if(insideBoard(toX,toY) && chessB[toX][toY] == 0) {
            return new Move (new Vector2(fromX,fromY), new Vector2(toX, toY));
        } else stillMoving[bolIndex] = false;
        return emptyMove;
    }

    private int PerformMove(int[][] chessB, Move move) {
        store = chessB[move.end.getX()][move.end.getY()];
        chessB[move.end.getX()][move.end.getY()] = chessB[move.start.getX()][move.start.getY()];
        chessB[move.start.getX()][move.start.getY()] = 0;
        return store;//store is the killed piece's value
    }

    private boolean insideBoard(int x, int y) {
        return (0 <= x && x < size &&
        0 <= y && y < size);
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
                if(selectedPiece == null) continue;
                color = selectedPiece.alliance();
                if (selectedPiece instanceof Pawn && color == black) chessB[x][y] = 1;
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
