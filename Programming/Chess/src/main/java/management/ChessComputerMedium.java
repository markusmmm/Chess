package management;

import pieces.ChessPiece;
import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Piece;
import resources.Vector2;

import java.util.ArrayList;

/**
 * gets a map over enemy's possible moves which is avoided.
 * always kill a piece if possible, prefer kills where you can't be caught back
 * last resort is random move
 */
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
<<<<<<< HEAD
        calcEnemyMoves();

    }

    private void calcEnemyMoves() {
        enemyMoves.clear();
        for (IChessPiece p: board.getUsablePieces(enemy).values()) {
            enemyMoves.addAll(p.getPossibleDestinations());
        }
=======
        //for (ChessPiece p : (ChessPiece) (board.getUsablePieces(enemy).values()) {
        //    enemyMoves.addAll(p.getPossibleDestinations());
        //}
        //TODO ChessComputerMedium.getMove
        throw new UnsupportedOperationException();
>>>>>>> 648461a4a6694c92e11336022f84a8304d757f50
    }
}
