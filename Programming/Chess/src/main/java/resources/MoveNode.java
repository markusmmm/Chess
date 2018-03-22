package resources;

import pieces.ChessPiece;

public class MoveNode {
    public final ChessPiece piece;
    public final Vector2 start, end;
    public final ChessPiece victimPiece;

    public MoveNode(ChessPiece piece, Vector2 start, Vector2 end, ChessPiece victimPiece) {
        this.piece = piece.clonePiece();
        this.start = start;
        this.end = end;
        this.victimPiece = victimPiece.clonePiece();
    }

    @Override
    public String toString() {
        return piece + "(" + piece.alliance() + ") from " + start + " to " + end;
    }
}
