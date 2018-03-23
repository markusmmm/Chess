package management;

import pieces.IChessPiece;
import resources.Move;
import resources.Vector2;

import java.util.*;

public class ChessComputerEasy extends ChessComputer {
    public ChessComputerEasy(Board board) {
        super(board);
    }

    public Move getMove() {
        Random rand = new Random();

        HashMap<Vector2, IChessPiece> pieces = board.getUsablePieces(alliance());

        List<Move> moves = new ArrayList<>();

        for(Vector2 key : pieces.keySet()) {
            IChessPiece piece = pieces.get(key);
            Set<Vector2> destinations = piece.getPossibleDestinations("ChessComputerEasy");

            for(Vector2 destination : destinations) {
                IChessPiece endPiece = board.getPiece(destination);

                Move move = new Move(piece.position(), destination);

                moves.add(move);
            }
        }

        return null;
    }
}
