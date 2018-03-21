package management;

import pieces.ChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Piece;
import resources.Vector2;

import java.util.ArrayList;

public class ChessComputerMedium extends ChessComputer {
    private Alliance enemy;
    private ArrayList<Vector2> enemyMoves = new ArrayList<>();
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
        //for (ChessPiece p : (ChessPiece) (board.getUsablePieces(enemy).values()) {
        //    enemyMoves.addAll(p.getPossibleDestinations());
        //}
        throw new
    }
}
