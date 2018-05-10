package resources;

import management.PieceManager;
import pieces.IChessPiece;
import pieces.IChessPiece;

public class MoveNode {
    public final PieceNode piece;
    public final Vector2 start, end;
    public final PieceNode victimPiece;

    public MoveNode(PieceNode piece, Vector2 start, Vector2 end, PieceNode victimPiece) {
        this.piece = piece;
        this.start = start;
        this.end = end;
        this.victimPiece = victimPiece;
    }
    public MoveNode(IChessPiece piece, Vector2 start, Vector2 end, IChessPiece victimPiece) {
        this.piece = PieceManager.toPiece(piece);
        this.start = start;
        this.end = end;
        this.victimPiece = PieceManager.toPiece(victimPiece);
    }

    @Override
    public String toString() {
        String str =  piece + "\n" + start + " -> " + end;
        if(!victimPiece.piece.equals(Piece.EMPTY))
            str += "\n[" + victimPiece + "]";

        return str;
    }
}
