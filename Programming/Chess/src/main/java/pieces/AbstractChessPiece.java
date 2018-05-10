package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractChessPiece implements IChessPiece {
	public class MoveEvaluator {
		private HashSet<Vector2> possibleDestinations;

		protected MoveEvaluator() {
			possibleDestinations = new HashSet<>();
		}

		protected boolean evaluate(Vector2 move) {
			Vector2 destination = position().add(move);
			//Console.printNotice(clonePiece() + " evaluates destination " + destination);

			if(isLegalMove(destination)) {
				possibleDestinations.add(destination);
				//Console.printSuccess("Move success");
				return true;
			}
			//Console.printError("Move failure");
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
					Vector2 destination = position.add(move);

					if(isLegalMove(destination))
                        possibleDestinations.add(destination);
					if (!board.insideBoard(destination) || board.getPiece(destination) != null)
                        // Piece/end of board reached. No need to further evaluate direction
						terminatedDirs.add(d);
				}
				dirs.removeAll(terminatedDirs);
			}
		}

		protected Set<Vector2> getResult() {
			return (HashSet<Vector2>)possibleDestinations.clone();
		}
	}

	public final static Tools<Vector2> vectorTools = new Tools<>();

    public enum MoveType { STEP, LINE }

	private MediaHelper media = new MediaHelper();

	private Vector2 position;
	protected final Alliance alliance;

	protected final HashSet<Vector2> moves;
	protected final MoveType moveType;

	protected Board board;

	protected final boolean canJump;
	protected final Piece piece;

	private boolean hasMoved = false;

    /**
     *
     */
    protected AbstractChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, MoveType moveType, AbstractBoard board, boolean canJump, Piece piece, boolean hasMoved) {
    	this.position = position;
        this.alliance = alliance;

        this.moves = moves;
        this.moveType = moveType;

        this.board = (Board)board;
        this.canJump = canJump;
        this.piece = piece;

        this.hasMoved = hasMoved;
    }
    protected AbstractChessPiece(AbstractChessPiece other) {
    	position = other.position;
    	alliance = other.alliance;

        moves = (HashSet<Vector2>)other.moves.clone();
    	moveType = other.moveType;

    	board = other.board;
    	canJump = other.canJump;
    	piece = other.piece();
    	hasMoved = other.hasMoved;
	}

	public Vector2 position() { return position; }
	public Alliance alliance() { return alliance; }
	public Piece piece() { return piece; }

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
	 * super.isLegalMove checks if the destination is within the board's boundaries, and if the piece at the given destination is hostile
	 * @param destination
	 * @return Whether or not the move can be performed
	 */
	public boolean isLegalMove(Vector2 destination) {
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

	@Override
	public Set<Vector2> getLegalActions() {
		MoveEvaluator evaluator = new MoveEvaluator();
		if(moveType == MoveType.LINE)
			evaluator.evaluateContinuous(moves);
		else
			evaluator.evaluate(moves);

		Set<Vector2> result = evaluator.getResult();

		return result;
	}

	/**
	 * Standard behaviour is that for all places a piece can move, it can also attack
	 * @return All positions this piece can attack
	 */
	@Override
	public Set<Vector2> getLegalAttacks() {
		return getLegalActions();
	}

	/**
	 * @return Whether or not the piece has been moved during the game
	 */
	public boolean hasMoved() {
		return hasMoved;
	}


	/**
	 * Attempts to move the piece to the given destination
	 * @param destination End position of attempted move
	 */
	public boolean move(Vector2 destination) {
		//resources.Console.println("Attempting to move " + alliance + " " + piece + " from " + position + " to " + move);
		if (!isLegalMove(destination)) return false; // If the destination is unreachable, the move fails

		performMove(destination);

		//resources.Console.println("Move performed. New pos: " + position);
		return true;
	}

	/**
	 * Performs a move to the given destination
	 * @param destination End position of move to perform
	 */
	protected void performMove(Vector2 destination) {
		position = destination;
		media.getMedia("move.mp3");
		hasMoved = true;
	}

	/**
	 * Loads all dynamic piece data
	 * @param values Data values to load
	 */
	public void loadData(List<Boolean> values) {
		hasMoved = values.get(0);
	}

	/**
	 * checks one by one position from this position
	 * toward destination
	 * @return false if runs into another piece
	 */
	public boolean freePath(Vector2 destination) {
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

	@Override
    public String toString() {
	    return alliance + " " + piece;
    }

    @Override
	public boolean equals(Object o) {
		if(!(o instanceof AbstractChessPiece)) return false;
		AbstractChessPiece other = (AbstractChessPiece)o;

		return position == other.position;
	}

    public AbstractChessPiece clone() {
		return clonePiece();
	}
}