package management;

import pieces.ChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Piece;
import resources.Vector2;

public class ChessComputerHard extends ChessComputer {
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
        ai.getBestMove(generateFen(),THINK_TIME);
        //TODO ChessComputerHard.getMove
        throw new UnsupportedOperationException();
    }

    private String generateFen() {
        fen.delete(0,fen.length());
        translateBoard();

        return fen.toString();
    }

    private void translateBoard() {
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
