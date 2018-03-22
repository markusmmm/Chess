package management;

import pieces.ChessPiece;
import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.MoveScore;
import resources.Vector2;

import java.util.*;


public class ChessComputerMedium extends ChessComputer {
    private Alliance enemy;
    private ArrayList<MoveScore> moveChart = new ArrayList<>();

    public ChessComputerMedium(Board board) {
        super(board);
        if (alliance() == Alliance.WHITE) {
            enemy = Alliance.BLACK;
        } else {
            enemy = Alliance.WHITE;
        }
    }


    @Override
    public Move getMove() {
        Board sim;
        for(IChessPiece piece: board.getUsablePieces(alliance()).values()) {
            for(Vector2 move: piece.getPossibleDestinations()) {
                sim = board.clone();
                sim.movePiece(piece.position(),move);
                moveChart.add(new MoveScore(scoreBoard(sim), new Move(piece.position(), move)));
            }
        }
        return Collections.max(moveChart).getMove();

    }

    private int scoreBoard(Board sim) {
        return diffPossibleMoves(sim) + diffPieceValue(sim);
    }

    private int diffPieceValue(Board sim) {
        return pieceValue(alliance(), sim) - pieceValue(enemy, sim);
    }

    private int pieceValue(Alliance alliance, Board sim) {
        int sum = 0;
        for(IChessPiece piece: sim.getUsablePieces(alliance).values()) {
            sum += piece.getValue();
        }
        return sum;
    }

    private int diffPossibleMoves(Board sim) {
        return possibleMoves(alliance(), sim) - possibleMoves(enemy, sim);
    }

    private int possibleMoves(Alliance alliance, Board sim) {
        int sum = 0;
        for(IChessPiece piece: sim.getPieces(alliance).values()) {
            for (Vector2 move: piece.getPossibleDestinations()) {
                sum++;
            }
        }
        return sum;
    }

    private IChessPiece getRandomPiece(ArrayList<IChessPiece> pieces) {
        return pieces.get(fromZeroTo(pieces.size() - 1));
    }

    private int fromZeroTo(int num) {
        return (int) (Math.random() * num * 1.0);
    }
}
