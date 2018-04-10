package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public class Dummy extends ChessPiece {

    public Dummy(Vector2 position, Alliance alliance, AbstractBoard board) {
        super(position, alliance, board, false, Piece.DUMMY, 0);
    }

    @Override
    public Set<Vector2> getPossibleDestinations(String caller) {
        return new HashSet<>();
    }

    public ChessPiece clonePiece() {
        return new Dummy(position, alliance, board);
    }
}
