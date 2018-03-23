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

        //List<Move> attacks = new ArrayList<>();
        List<Move> moves = new ArrayList<>();

        for(Vector2 key : pieces.keySet()) {
            IChessPiece piece = pieces.get(key);
            Set<Vector2> destinations = piece.getPossibleDestinations("ChessComputerEasy");

            for(Vector2 destination : destinations) {
                IChessPiece endPiece = board.getPiece(destination);

                Move move = new Move(piece.position(), destination);

                //if(endPiece != null && !piece.alliance().equals(endPiece))
                //    attacks.add(move);
                //else
                    moves.add(move);
            }
        }

        //if(attacks.size() != 0)
        //    return attacks.get(rand.nextInt(attacks.size()));
        //else if(moves.size() != 0)
        //    return moves.get(rand.nextInt(moves.size()));

        return null;
    }
}
