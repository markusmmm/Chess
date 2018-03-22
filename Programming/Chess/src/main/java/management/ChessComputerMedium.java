package management;

import pieces.ChessPiece;
import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Vector2;

import java.util.*;


public class ChessComputerMedium extends ChessComputer {
    private Alliance enemy;

    public ChessComputerMedium(Board board) {
        super(board);
        //get enemy alliance
        if(alliance() == Alliance.WHITE) {
            enemy = Alliance.BLACK;
        } else {
            enemy = Alliance.WHITE;
        }
    }


    @Override
    public Move getMove() {

    }

    private IChessPiece getRandomPiece(ArrayList<IChessPiece> pieces) {
        return pieces.get(fromZeroTo(pieces.size() - 1));
    }

    private int fromZeroTo(int num) {
        return (int) (Math.random() * num * 1.0);
    }
}
