package management;

import resources.Piece;

import java.util.HashMap;

public class PieceManager {
    private static final HashMap<Character, Piece> symbolTable = createSymbolTable();

    private static HashMap<Character, Piece> createSymbolTable() {
        HashMap<Character, Piece> map = new HashMap<>();
        map.put('b', Piece.BISHOP);
        map.put('k', Piece.KING);
        map.put('n', Piece.KNIGHT);
        map.put('p', Piece.PAWN);
        map.put('q', Piece.QUEEN);
        map.put('r', Piece.ROOK);

        return map;
    }

    public static Piece toPiece(char c) {
        c = Character.toLowerCase(c);

        for(char s : symbolTable.keySet())
            if(s == c)
                return symbolTable.get(s);

        return Piece.EMPTY;
    }
}
