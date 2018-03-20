package management;

import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Vector2;

import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Random;

public class ChessComputerEasy extends ChessComputer {

    public ChessComputerEasy(Alliance alliance, Board board) {
        super(alliance, board);
    }

    public Move getMove() {
        Random rand = new Random();

        HashMap<Vector2, IChessPiece> pieces = board.getUsablePieces(alliance());

        Set<Vector2> keys = pieces.keySet();
        int r = rand.nextInt(pieces.size());
        int i = 0;

        for(Vector2 key : keys) {
            if(r == i++) {
                IChessPiece piece = pieces.get(key);
                List<Vector2> moves = piece.getPossibleDestinations();

                Vector2 destination = moves.get(rand.nextInt(moves.size()));

                return new Move(piece.position(), destination);
            }
        }
        return null;
    }
}
