package management;

import resources.BiMap;
import resources.Piece;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PieceManager {
    private static final BiMap<Character, Piece> symbolTable = new BiMap().putAll(
            new Character[] { 'b', 'k', 'n', 'p', 'q', 'r' },
            new Piece[] { Piece.BISHOP, Piece.KING, Piece.KNIGHT, Piece.PAWN, Piece.QUEEN, Piece.ROOK }
    );

    public static Piece toPiece(char c) {
        c = Character.toLowerCase(c);

        for(char s : symbolTable.leftKeys())
            if(s == c)
                return symbolTable.getRight(s);

        return Piece.EMPTY;
    }
    public static char toSymbol(Piece piece) {
        for(Piece p : symbolTable.rightKeys())
            if(piece.equals(p))
                return symbolTable.getLeft(p);

        return 'e';
    }
}
