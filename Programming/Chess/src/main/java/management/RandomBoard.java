package management;

import resources.Alliance;
import resources.BoardMode;
import resources.Piece;
import resources.Vector2;

import java.util.Random;

public class RandomBoard extends Board {

    public RandomBoard(int size, int difficulty, boolean useClock) {
        super(size, difficulty, useClock, BoardMode.EMPTY);
        generateRandomBoard();
    }

    /**
     * Generates a random board with random amount of pieces.
     *
     * @return ChessBoard
     */
    private void generateRandomBoard() {
        int bRooks = 0, bPawns = 0, bQueens = 0, bKings = 0, bBishops = 0, bKnights = 0;
        int wRooks = 0, wPawns = 0, wQueens = 0, wKings = 0, wBishops = 0, wKnights = 0;
        //Keeps track of how many of each pieces is added, so that we don't get to many of one.
        int w = 0;
        int b = 0;
        //Counters for black and white pieces
        int bishopX = 0;
        int bishopY = 0;

        int bishopWX = 0;
        int bishopWY = 0;
        //X and Y positions of bishops. This is to make sure that bishops from the same alliance don't controll the same colored tiles.
        Random random = new Random();
        while (b < 16) {
            Piece aPiece = randomPiece();
            int x = random.nextInt(7 - 0 + 1) + 0;
            int y = random.nextInt(7 - 0 + 1) + 0;

            Vector2 pos = new Vector2(x, y);
            //Creates a random position
            if (!vacant(pos)) {
                b++;
                continue;
                //Checks if the position is vacant.
            }
            if (b == 15 && bKings == 0) {
                addPiece(pos, Piece.KING, Alliance.BLACK);
                /*if (getKing(Alliance.BLACK).inCheck()) {
                    removePiece(pos);
                    continue;
                }*/
                b++;
                bKings++;
                //If a black king has not been added by the last count, a black king will be added.
                continue;
            }

            if (aPiece.equals(Piece.ROOK) && bRooks < 2) {
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bRooks++;
                continue;
            }
            if (aPiece.equals(Piece.PAWN) && bPawns < 8) {
                if (y == 0) {
                    //Checks that the pawn isn't put in an invalid position on the board.
                    continue;
                }
                if (y == 7) {
                    addPiece(pos, Piece.QUEEN, Alliance.BLACK);
                    b++;
                    continue;
                    //If the pawn is placed on the opposite side of the board, there will be pawn promotion
                }
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bPawns++;
                continue;
            }
            if (aPiece.equals(Piece.QUEEN) && bQueens < 1) {
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bQueens++;
                continue;
            }
            if (aPiece.equals(Piece.KING) && bKings < 1) {
                addPiece(pos, aPiece, Alliance.BLACK);
                if (getKing(Alliance.BLACK).inCheck()) {
                    removePiece(pos);
                    continue;
                }
                b++;
                bKings++;
                continue;
            }
            if (aPiece.equals(Piece.BISHOP) && bBishops < 2) {
                if (bBishops == 0) {
                    bishopX = x;
                    bishopY = y;
                }
                if (bBishops == 1) {
                    boolean b1 = bishopX % 2 == bishopY % 2;
                    boolean b2 = x % 2 == y % 2;
                    if (b1 == b2) {
                        continue;
                    }
                    //Makes sure that if there's two bishops to be added, they will control their own colored tiles.
                }
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bBishops++;
                continue;
            }
            if (aPiece.equals(Piece.KNIGHT) && bKnights < 2) {
                addPiece(pos, aPiece, Alliance.BLACK);
                b++;
                bKnights++;
                continue;
            }
            if (aPiece.equals(Piece.EMPTY)) {
                b++;
                continue;
            }
        }
        while (w < 16) {
            //Same conditions and method as the black pieces
            Piece aPiece = randomPiece();
            int x = random.nextInt(7 - 0 + 1) + 0;
            int y = random.nextInt(7 - 0 + 1) + 0;

            Vector2 invPos = new Vector2(x, y);
            if (!vacant(invPos)) {
                w++;
                continue;
            }
            if (w == 15 && wKings == 0) {
                addPiece(invPos, Piece.KING, Alliance.WHITE);
               /* if (getKing(Alliance.WHITE).inCheck()) {
                    removePiece(invPos);
                    continue;
                }*/
                w++;
                wKings++;
                continue;
            }
            if (aPiece.equals(Piece.ROOK) && wRooks < 2) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wRooks++;
                continue;
            }
            if (aPiece.equals(Piece.PAWN) && wPawns < 8) {
                if (y == 7) {
                    continue;
                }
                if (y == 0) {
                    addPiece(invPos, Piece.QUEEN, Alliance.WHITE);
                    w++;
                    continue;
                }
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wPawns++;
                continue;
            }
            if (aPiece.equals(Piece.QUEEN) && wQueens < 1) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wQueens++;
                continue;
            }
            if (aPiece.equals(Piece.KING) && wKings < 1) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                if (getKing(Alliance.WHITE).inCheck()) {
                    removePiece(invPos);
                    continue;
                }
                w++;
                wKings++;
                continue;
            }
            if (aPiece.equals(Piece.BISHOP) && wBishops < 2) {
                if (wBishops == 0) {
                    bishopWX = x;
                    bishopWY = y;
                }
                if (wBishops == 1) {
                    boolean b1 = bishopWX % 2 == bishopWY % 2;
                    boolean b2 = x % 2 == y % 2;
                    if (b1 == b2) {
                        continue;
                    }
                }
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wBishops++;
                continue;
            }
            if (aPiece.equals(Piece.KNIGHT) && wKnights < 2) {
                addPiece(invPos, aPiece, Alliance.WHITE);
                w++;
                wKnights++;
                continue;
            }
            if (aPiece.equals(Piece.EMPTY)) {
                b++;
                continue;
            }
        }
    }
}
