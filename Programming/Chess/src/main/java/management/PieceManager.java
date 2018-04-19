package management;

import pieces.ChessPiece;
import resources.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PieceManager {
    private static final char EMPTY_CHAR = 'e';

    private static final BiMap<Character, PieceNode> symbolTable = new BiMap<Character, PieceNode>().putAll(
            new Character[] {
                    'b', 'k', 'n', 'p', 'q', 'r',
                    'B', 'K', 'N', 'P', 'Q', 'R'},
            new PieceNode[] {
                    new PieceNode(Piece.BISHOP, Alliance.BLACK), new PieceNode(Piece.KING, Alliance.BLACK), new PieceNode(Piece.KNIGHT, Alliance.BLACK),
                    new PieceNode(Piece.PAWN, Alliance.BLACK), new PieceNode(Piece.QUEEN, Alliance.BLACK), new PieceNode(Piece.ROOK, Alliance.BLACK),
                    new PieceNode(Piece.BISHOP, Alliance.WHITE), new PieceNode(Piece.KING, Alliance.WHITE), new PieceNode(Piece.KNIGHT, Alliance.WHITE),
                    new PieceNode(Piece.PAWN, Alliance.WHITE), new PieceNode(Piece.QUEEN, Alliance.WHITE), new PieceNode(Piece.ROOK, Alliance.WHITE)
            }
    );

    public static PieceNode toPiece(char c) {
        for(char s : symbolTable.leftKeys())
            if(s == c)
                return symbolTable.getRight(s);

        return new PieceNode(Piece.EMPTY, Alliance.NONE);
    }
    public static PieceNode toPiece(ChessPiece piece) {
        if(piece == null) return new PieceNode(Piece.EMPTY, Alliance.NONE);
        return new PieceNode(piece.piece(), piece.alliance());
    }
    public static char toSymbol(PieceNode piece) {
        if(piece == null) return EMPTY_CHAR;

        for(PieceNode p : symbolTable.rightKeys())
            if(piece.equals(p))
                return symbolTable.getLeft(p);

        return EMPTY_CHAR;
    }
    public static char toSymbol(ChessPiece piece) {
        return toSymbol(toPiece(piece));
    }
}
