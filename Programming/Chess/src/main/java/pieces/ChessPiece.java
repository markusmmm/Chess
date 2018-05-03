package pieces;

import management.AbstractBoard;
import resources.Alliance;
import resources.Console;
import resources.Piece;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public abstract class ChessPiece extends AbstractChessPiece {

    public class MoveEvaluator {
        private HashSet<Vector2> possibleDestinations;

        protected MoveEvaluator() {
            possibleDestinations = new HashSet<>();
        }

        protected boolean evaluate(Vector2 move) {
            Vector2 destination = position().add(move);
            Console.printNotice(clonePiece() + " evaluates destination " + destination);

            if(legalMove(destination)) {
                possibleDestinations.add(destination);
                Console.printSuccess("Move success");
                return true;
            }
            Console.printError("Move failure");
            return false;
        }

        /**
         * Evaluates one step in each given directions
         * @param moves Move directions to evaluate
         */
        protected void evaluate(HashSet<Vector2> moves) {
            moves = (HashSet<Vector2>)moves.clone();

            for(Vector2 dir : moves)
                evaluate(dir);
        }

        /**
         * Evaluates multiple directions in parallel, until all directions fail
         * @param dirs Directions to evaluate (Set of unit vectors)
         */
        protected void evaluateContinuous(HashSet<Vector2> dirs) {
            dirs = (HashSet<Vector2>)dirs.clone();
            if(dirs.size() == 0) {
                Console.printWarning("Evaluation begun, but " + clonePiece() + " has no moves");
            }

            for (int variable = 1; variable < board.size(); variable++) {
                if (dirs.size() == 0) return;

                Set<Vector2> terminatedDirs = new HashSet<>();
                for (Vector2 d : dirs) {
                    Vector2 move = d.mult(variable);
                    AbstractChessPiece enemy = board.getPiece(position().add(move));

                    if ((!evaluate(move) || (enemy != null && enemy.alliance() != alliance())) && !canJump) {
                        // Obstacle/enemy reached. If the piece can't jump, no further evaluation is needed in direction d
                        terminatedDirs.add(d);
                    }
                }
                dirs.removeAll(terminatedDirs);
            }
        }

        protected Set<Vector2> getResult() {
            return (HashSet<Vector2>)possibleDestinations.clone();
        }
    }

    /**
     *
     */
    protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, MoveType moveType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
        super(position, alliance, moves, moveType, board, canJump, piece, value, hasMoved);
    }
    protected ChessPiece(ChessPiece other) {
        super(other);
    }

    @Override
    public Set<Vector2> getPossibleDestinations() {
        MoveEvaluator evaluator = new MoveEvaluator();
        if(moveType == MoveType.LINE)
            evaluator.evaluateContinuous(moves);
        else
            evaluator.evaluate(moves);

        Set<Vector2> result = evaluator.getResult();

        return result;
    }

    @Override
    public boolean legalMove(Vector2 destination) {
        Vector2 delta = destination.sub(position());
        //Console.printNotice(this + " checking move " + position() + " -> " + destination + "\tdelta: " + delta);

        if(super.legalMove(destination)) {

            //Console.printNotice(this + " (" + moveType + ", " + moves.size() + ") begun move check.");
            //Console.printNotice("Position: " + position() + ", destination: " + destination + ", delta: " + delta);

            if (moveType == MoveType.STEP) {
                boolean result = moves.contains(delta);
                if(result) Console.printSuccess("Move check success");
                return result;
            }
            else if (moveType == MoveType.LINE)
                for (Vector2 move : moves) {
                    Console.printNotice("\tChecking dir " + move + " to delta " + delta);
                    if (move.isParallelTo(delta)) {
                        Console.printSuccess("\tMove check success");
                        return true;
                    } else {
                        Console.printError("Move " + move + " is not parallel to " + delta);
                    }
                }
        }

        //Console.printError("Move failure");
        return false;
    }
}
