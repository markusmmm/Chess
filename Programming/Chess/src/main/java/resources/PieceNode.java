package resources;

public class PieceNode {
    public final Piece piece;
    public final Alliance alliance;

    public PieceNode(Piece piece, Alliance alliance) {
        this.piece = piece;
        this.alliance = alliance;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof PieceNode)) return false;

        PieceNode other = (PieceNode) o;
        return other.piece.equals(piece) && other.alliance.equals(alliance);
    }

    @Override
    public String toString() {
        return alliance + " " + piece;
    }
}
