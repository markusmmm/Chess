package management;

import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Vector2;

public class ChessComputerHard extends ChessComputer {
    private Stockfish ai = new Stockfish();
    protected int thinkTime = 2500;
    private StringBuilder fen = new StringBuilder();
    private int spacecounter;

    public ChessComputerHard(Board board) {
        super(board);
        ai.startEngine();
        ai.sendCommand("uci");
    }
    public ChessComputerHard(Board board, Alliance alliance) {
        super(board, alliance);
        ai.startEngine();
        ai.sendCommand("uci");
    }

    @Override
    public Move getMove() {
        ai.sendCommand("ucinewgame");
        String aiAnswer = ai.getBestMove(generateFen(), thinkTime);
        System.out.println(aiAnswer);
        Move best = readAI(aiAnswer);

        System.out.println(best);
        return resolveMove(best);
    }

    private Move readAI(String bestMove) {
        int[] in = new int[bestMove.length()];
        for (int i = 0; i < bestMove.length(); i++) {
            in[i] = toInt(bestMove.charAt(i));
        }
        return new Move(new Vector2(in[0], in[1]), new Vector2(in[2], in[3]));
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
            case '1': return 7;
            case '2': return 6;
            case '3': return 5;
            case '4': return 4;
            case '5': return 3;
            case '6': return 2;
            case '7': return 1;
            case '8': return 0;

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
        fen.append(" " + activeColour() + " ");
        castlingAvailability();
        fen.append(" -");       //passant
        fen.append(" 0");       //50 moves rule ignored
        fen.append(" " + 1);    //amount of moves
        return fen.toString();
    }

    /**
     * Evaluates if any form of castling move is available
     */
    private void castlingAvailability() {
        Vector2 blackKing = new Vector2(3,0);
        Vector2 blackKRook = new Vector2(0,0);
        Vector2 blackQRook = new Vector2(7,0);
        Vector2 whiteKing = new Vector2(3,7);
        Vector2 whiteKRook = new Vector2(0,7);
        Vector2 whiteQRook = new Vector2(7,7);
        castling(whiteKing,whiteKRook,"K");
        castling(whiteKing,whiteQRook,"Q");
        castling(blackKing,blackKRook,"k");
        castling(blackKing,blackQRook,"q");
    }

    /**
     * Appends a castling value to the fen, corresponding to the available castling moves
     * @param king The king's current position
     * @param rook Position of the rook that is being castled
     * @param castlingValue Value describing side and alliance of castling move
     */
    private void castling(Vector2 king, Vector2 rook, String castlingValue) {
        if(hasMoved(king) || hasMoved(rook)) fen.append("-");
        else fen.append(castlingValue);
    }

    /**
     * @param pos Position of piece to check
     * @return If the piece has been moved at least once during the game
     */
    private boolean hasMoved(Vector2 pos) {
        IChessPiece piece = board.getPiece(pos);
        if(piece == null) return true;
        return !piece.hasMoved();
    }

    /**
     *  @return String-representation of the active player's color
     */
    private String activeColour() {
        if(alliance() == Alliance.WHITE) return "w";
        return "b";
    }

    /**
     * Formats the board as a string, and appends it to fen
     */
    private void boardToFen() {
        spacecounter = 0;
        for (int y = 0; y < board.size(); y++) {
            for (int x = 0; x < board.size(); x++) {
                if(board.getPiece(new Vector2(x,y)) == null) {
                    spacecounter++;
                } else {
                    addSpace();
                    fen.append(PieceManager.toPiece(board.getPiece(new Vector2(x,y))));
                }
            }
            addSpace();
            if(y < board.size() -1) {//removes a useless '/' at the end
                fen.append("/");
            }
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
}
