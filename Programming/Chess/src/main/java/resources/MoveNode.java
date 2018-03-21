package resources;

public class MoveNode {
    public final Piece piece;
    public final Alliance alliance;
    public final Vector2 start, end;
    public final Piece victimPiece;

    public MoveNode(Piece piece, Alliance alliance, Vector2 start, Vector2 end, Piece victimPiece) {
        this.piece = piece;
        this.alliance = alliance;
        this.start = start;
        this.end = end;
        this.victimPiece = victimPiece;
    }

    public Alliance victimAlliance() {
        if(victimPiece == Piece.EMPTY) throw new IllegalStateException("Move node has no victim");
        return alliance == Alliance.BLACK ? Alliance.WHITE : Alliance.BLACK;
    }

    @Override
    public String toString() {
        return piece + "(" + alliance + ") from " + start + " to " + end;
    }
}
