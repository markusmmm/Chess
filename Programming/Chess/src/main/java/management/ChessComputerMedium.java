package management;

import com.sun.org.apache.xpath.internal.SourceTree;
import pieces.IChessPiece;
import resources.*;

import java.util.*;


public class ChessComputerMedium extends ChessComputer {
    private final long THINKING_TIME = 3000;
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
        long startTime = System.currentTimeMillis();

        for(IChessPiece piece: board.getUsablePieces(alliance()).values()) {
            for(Vector2 move: piece.getPossibleDestinations()) {
                sim = board.clone();
                sim.movePiece(piece.position(),move);
                moveChart.add(new MoveScore(scoreBoard(sim), new Move(piece.position(), move)));
                System.out.println("mediumAI " + piece.toString() + piece.position().getY());
               /* if(startTime + THINKING_TIME < System.currentTimeMillis()){
                    printPossibleMoves();
                    return Collections.max(moveChart).getMove();

                }*/
               printBoard(sim);
            }
        }
        printPossibleMoves();

        Collections.sort(moveChart);

        Move m = moveChart.get(moveChart.size() - 1).getMove();
        System.out.println("mediumAI" + m.start.toString() + m.end.toString());
        return moveChart.get(moveChart.size() - 1).getMove();


    }
    private void printPossibleMoves() {
        for(MoveScore s: moveChart) {
            System.out.println(s.getMove().start.toString() + " " + s.getMove().end.toString());
        }
    }private void printBoard(Board board) {
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                if(board.getPiece(new Vector2(x,y)) != null) {
                    System.out.print(board.getPiece(new Vector2(x, y)).getValue());
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    /**
     *
     * @param sim the board that gets scored
     * @return gives score to current state of board
     */
    private int scoreBoard(Board sim) {
        return  diffPieceValue(sim) + diffPossibleMoves(sim);
    }

    /**
     *
     * @param sim
     * @return gives the difference between the total value of black and white's pieces
     */
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

    /**
     *
     * @param sim
     * @return gives the difference between the possible moves black and white have
     */
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
