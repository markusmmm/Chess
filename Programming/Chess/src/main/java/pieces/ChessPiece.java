package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class ChessPiece implements IChessPiece {
    public enum MoveType { STEP, LINE }

    public class MoveEvaluator {
        private HashSet<Vector2> possibleDestinations;

        protected MoveEvaluator() {
            possibleDestinations = new HashSet<>();
        }

        protected boolean evaluate(Vector2 move) {
            Vector2 destination = position.add(move);

            if(legalMove(destination)) {
                possibleDestinations.add(destination);
                return true;
            }
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

            for (int variable = 1; variable < board.size() - 1; variable++) {
                if (dirs.size() == 0) return;

                Set<Vector2> terminatedDirs = new HashSet<>();
                for (Vector2 d : dirs) {
                    Vector2 move = d.mult(variable);
                    if (!evaluate(move) && !canJump) {
                        // Obstacle reached. If the piece can't jump, no further evaluation is needed in direction d
                        terminatedDirs.add(d);
                        Console.printError("Direction " + d + " terminated");
                    }
                }
                dirs.removeAll(terminatedDirs);
            }
        }

        protected Set<Vector2> getResult() {
            return (HashSet<Vector2>)possibleDestinations.clone();
        }
    }

	private MediaHelper media = new MediaHelper();

	private Vector2 position;
	protected final Alliance alliance;

	protected final HashSet<Vector2> moves;
	protected final MoveType moveType;

	protected Board board;

	protected final boolean canJump;
	protected final Piece piece;
	protected final int value;

	private boolean hasMoved = false;

    /**
     *
     */
    protected ChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, MoveType moveType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
    	this.position = position;
        this.alliance = alliance;

        this.moves = moves;
        this.moveType = moveType;

        this.board = (Board)board;
        this.canJump = canJump;
        this.piece = piece;
        this.value = value;

        this.hasMoved = hasMoved;
    }
    protected ChessPiece(ChessPiece other) {
    	position = other.position;
    	alliance = other.alliance;

        moves = (HashSet<Vector2>)other.moves.clone();
    	moveType = other.moveType;

    	board = other.board;
    	canJump = other.canJump;
    	piece = other.piece();
    	value = other.value;
    	hasMoved = other.hasMoved;
	}

	public Vector2 position() { return position; }
	public Alliance alliance() { return alliance; }
	public Piece piece() { return piece; }
	public int getValue() { return value; }

	public Alliance otherAlliance() {
    	return alliance.equals(Alliance.BLACK) ? Alliance.WHITE : Alliance.BLACK;
	}

	public Board getBoard() {
    	return board.clone();
	}

	public Set<Vector2> getMoves() {
        return (HashSet<Vector2>)(moves).clone();
    }

	/**
	 * Checks if the piece can go to the given destination
	 * super.legalMove checks if the destination is within the board's boundaries, and if the piece at the given destination is hostile
	 * @param destination
	 * @return Whether or not the move can be performed
	 */
	public boolean legalMove(Vector2 destination) {
		IChessPiece endPiece = board.getPiece(destination);
		// Prevents attack on an allied piece
		if(endPiece != null && endPiece.alliance().equals(alliance)) return false;

		if(!board.insideBoard(position()) || !board.insideBoard(destination)) return false;

		if(!board.hasKing(alliance))
			return true;	// Special-case check for boards where a king was never created

		King king = board.getKing(alliance);
		if(king == null) return false;

		// Lastly, check if king is in check, and whether or not the move resolves it (SHOULD OCCUR LAST, FOR OPTIMIZATION)
		return king.resolvesCheck(position(), destination);
	}

    public Set<Vector2> getPossibleDestinations() {
		Console.printNotice("Evaluating possible destinations for " + this);

        MoveEvaluator evaluator = new MoveEvaluator();
        if(moveType == MoveType.LINE)
            evaluator.evaluateContinuous(moves);
        else
            evaluator.evaluate(moves);

        Set<Vector2> result = evaluator.getResult();

        return result;
    }

	/**
	 * @return Whether or not the piece has been moved during the game
	 */
	public boolean hasMoved() {
		return hasMoved;
	}


	/**
	 * 
	 * @param destination
	 */
	public boolean move(Vector2 destination, Board board) {
		this.board = board;

		//resources.Console.println("Attempting to move " + alliance + " " + piece + " from " + position + " to " + move);
		if (!legalMove(destination)) return false; // If the destination is unreachable, the move fails

		position = destination;
		media.playSound("move.mp3");
		hasMoved = true;

		//resources.Console.println("Move performed. New pos: " + position);
		return true;
	}

	public void loadData(List<Boolean> vals) {
		hasMoved = vals.get(0);
	}

	/**
	 * checks one by one position from this position
	 * toward destination
	 * @return false if runs into another piece
	 */
	protected boolean freePath(Vector2 destination) {
		Vector2 position = position();

		Vector2 path = position;
		int between = position.distance(destination) - 1;

		for (int step = 0; step < between; step++) {
			path = path.stepToward(destination);
			if (board.getPiece(path) != null) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @return if piece is placed in the lines:
	 * up, down, left, right
	 *
	 * logic is: if only x or y change, the piece move in a straight path
	 */
	protected boolean inStraights(Vector2 move) {
		Vector2 position = position();

		return (
				( position.getX() == move.getX() && position.getY() != move.getY() )
						||
						( position.getX() != move.getX() && position.getY() == move.getY() )
		);
	}
	protected boolean inDiagonals(Vector2 newPos) {
		Vector2 position = position();
		return Math.abs(position.getX() - newPos.getX()) == Math.abs(position.getY() - newPos.getY());
	}

	@Override
    public String toString() {
	    return alliance + " " + piece;
    }

    @Override
	public boolean equals(Object o) {
		if(!(o instanceof ChessPiece)) return false;
		ChessPiece other = (ChessPiece)o;

		return position == other.position;
	}

    public ChessPiece clone() {
		return clonePiece();
	}
}