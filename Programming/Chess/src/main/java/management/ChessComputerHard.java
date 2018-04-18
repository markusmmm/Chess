package management;

import pieces.ChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Piece;
import resources.Vector2;

import java.util.Scanner;

public class ChessComputerHard extends ChessComputer {
    private Scanner input;
    private Stockfish ai = new Stockfish();
    private final int THINK_TIME = 2500;
    private StringBuilder fen = new StringBuilder();
    private int spacecounter = 0;
    public ChessComputerHard(Board board) {
        super(board);
        ai.startEngine();
        ai.sendCommand("uci");
    }

    @Override
    public Move getMove() {
        ai.sendCommand("ucinewgame");
        return readAI(ai.getBestMove(generateFen(),THINK_TIME));
    }

    private Move readAI(String bestMove) {
        int[] in = new int[bestMove.length()];
        for (int i = 0; i < bestMove.length(); i++) {
            in[i] = toInt(bestMove.charAt(i));
        }
        return new Move(new Vector2(in[0], in[2]), new Vector2(in[3], in[4]));
    }

    /**
     *
     * @param c chess coordniate
     * @return cartesian coordinate
     */
    private int toInt(char c) {
        switch(c) {
            case 'a': return 0;
            case 'b': return 1;
            case 'c': return 2;
            case 'd': return 3;
            case 'e': return 4;
            case 'f': return 5;
            case 'g': return 6;
            case 'h': return 7;
            case '1': return 0;
            case '2': return 1;
            case '3': return 2;
            case '4': return 3;
            case '5': return 4;
            case '6': return 5;
            case '7': return 6;
            case '8': return 7;

        }
        return -1;
    }

    /**
     * https://en.wikipedia.org/wiki/Forsyth-Edwards_Notation
     * @return standard UCI FEN format for chess Engines
     */
    private String generateFen() {
        fen.delete(0,fen.length());
        boardToFen();
        fen.append(" " + activeColour());
        fen.append(" " + castlingAvailability());
        fen.append(" 0");//50 moves rule ignored
        fen.append(" " + passantTarget());
        fen.append(" " + moveNumber());
        return fen.toString();
    }

    private void boardToFen() {
        spacecounter = 0;
        for (int y = 0; y < board.size(); y++) {
            for (int x = 0; x < board.size(); x++) {
                if(board.getPiece(new Vector2(x,y)) == null) {
                    spacecounter++;
                } else {
                    addSpace();
                    fen.append(translatePiece(board.getPiece(new Vector2(x,y))));
                }
            }
            addSpace();
            fen.append("/");
        }
    }

    /**
     * places the  number of spaces between pieces in FEN-string
     */
    private void addSpace() {
        if(0 < spacecounter) {
            fen.append(spacecounter);
        }
        spacecounter = 0;
    }

    private String translatePiece(ChessPiece piece) {
        String translated = "";
        if(piece.piece() == Piece.PAWN) translated = "p";
        else if (piece.piece() == Piece.ROOK) translated = "r";
        else if (piece.piece() == Piece.BISHOP) translated = "b";
        else if (piece.piece() == Piece.KNIGHT) translated = "n";
        else if (piece.piece() == Piece.KING) translated = "k";
        else if (piece.piece() == Piece.QUEEN) translated = "q";
        if(piece.alliance() == Alliance.WHITE) translated.toUpperCase();
        return translated;
    }
}
