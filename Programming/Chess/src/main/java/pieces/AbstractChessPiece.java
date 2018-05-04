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

        /**
         * Evaluates a single move
         * @param move Move to evaluate
         * @return If the move is legal
         */
		protected boolean evaluate(Vector2 move) {
			Vector2 destination = position().add(move);
			//Console.printNotice(clonePiece() + " evaluates destination " + destination);

			if(legalAction(destination)) {
				possibleDestinations.add(destination);
				//Console.printSuccess("Move success");
				return true;
			}
			//Console.printError("Move failure");
			return false;
		}

		/**
		 * Evaluates multiple moves at once
		 * @param moves Moves to evaluate
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

		protected HashSet<Vector2> getResult() {
			return (HashSet<Vector2>)possibleDestinations.clone();
		}
	}

	public final static Tools<Vector2> vectorTools = new Tools<>();

    public enum ActionType { STEP, LINE }

	private MediaHelper media = new MediaHelper();

	private Vector2 position;
	protected final Alliance alliance;

	protected final HashSet<Vector2> moves;
	protected final HashSet<Vector2> attacks;
	protected final ActionType actionType;

	protected Board board;

	private final boolean canJump;
	protected final Piece piece;
	private final int value;

	private boolean hasMoved = false;

    /**
     *
     * @param position Initial position of piece
     * @param alliance Assigned alliance
     * @param actions Relative vectors, describing how the piece can move
     * @param actionType How the actions behave (STEP: Can go to destination of action. LINE: Can go in direction of action)
     * @param board The board for the currently active game
     * @param canJump Whether or not the piece can jump over other pieces
     * @param piece Type of piece
     * @param value Score used by AI
     * @param hasMoved Whether or not this piece has been moved earlier in the game
     */
    protected AbstractChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> actions, ActionType actionType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
    	this.position = position;
        this.alliance = alliance;

        this.moves = actions;
        attacks = actions;
        this.actionType = actionType;

        this.board = (Board)board;
        this.canJump = canJump;
        this.piece = piece;
        this.value = value;

        this.hasMoved = hasMoved;
    }

    /**
     * Overloaded constructor, that splits actions into moves and attacks (See original constructor for full param documentation)
     * @param moves Relative vectors, describing how the piece can move
     * @param attacks Relative vectors, describing how the piece can attack
     */
	protected AbstractChessPiece(Vector2 position, Alliance alliance, HashSet<Vector2> moves, HashSet<Vector2> attacks, ActionType actionType, AbstractBoard board, boolean canJump, Piece piece, int value, boolean hasMoved) {
		this.position = position;
		this.alliance = alliance;

		this.moves = moves;
		this.attacks = attacks;
		this.actionType = actionType;

		this.board = (Board)board;
		this.canJump = canJump;
		this.piece = piece;
		this.value = value;

		this.hasMoved = hasMoved;
	}

    /**
     * Constructs a clone of an existing chess-piece
     * @param position Position of cloned piece
     * @param other Piece to clone
     */
    protected AbstractChessPiece(Vector2 position, AbstractChessPiece other) {
        this.position = position;
        alliance = other.alliance;

        moves = (HashSet<Vector2>)other.moves.clone();
        attacks = (HashSet<Vector2>)other.attacks.clone();
        actionType = other.actionType;

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

    /**
     * @return The opponent's alliance
     */
	public Alliance otherAlliance() {
    	return alliance.equals(Alliance.BLACK) ? Alliance.WHITE : Alliance.BLACK;
	}

    /**
     * @return Clone of this piece's board
     */
	public Board getBoard() {
    	return board.clone();
	}

	/**
	 * Checks if the piece can go to the given destination
	 * super.legalAction checks if the destination is within the board's boundaries, and if the piece at the given destination is hostile
	 * @param destination End position of attempted move
	 * @return Whether or not the move can be performed
	 */
	public boolean legalAction(Vector2 destination) {
		if(!board.insideBoard(position()) || !board.insideBoard(destination)) return false;

		IChessPiece endPiece = board.getPiece(destination);
		// Prevents attack on an allied piece
		if(endPiece != null && endPiece.alliance().equals(alliance)) return false;

        // Special-case check for boards where a king was never created
		if(!board.hasKing(alliance))
			return true;

		King king = board.getKing(alliance);

		// Check if king is in check, and whether or not the move resolves it (SHOULD OCCUR LAST, FOR OPTIMIZATION)
		return king != null && king.resolvesCheck(position(), destination);
	}

    /**
     * @param action Type of action
     * @return All legal actions of the given type this piece can perform
     */
	private HashSet<Vector2> getPossibleActionsOfType(Action action) {
        HashSet<Vector2> actions = new HashSet<>();
        if(action == Action.ACTION || action == Action.MOVE) actions.addAll(moves);
        if(action == Action.ACTION || action == Action.ATTACK) actions.addAll(attacks);

		MoveEvaluator evaluator = new MoveEvaluator();
		if(actionType == ActionType.LINE)
			evaluator.evaluateContinuous(actions);
		else
			evaluator.evaluate(actions);

		HashSet<Vector2> result = evaluator.getResult();

		return result;
	}

    /**
     * @return All legal moves this piece can perform
     */
	@Override
    public HashSet<Vector2> getPossibleMoves() {
	    return getPossibleActionsOfType(Action.MOVE);
    }

    /**
     * @return All legal attacks this piece can perform
     */
	@Override
    public HashSet<Vector2> getPossibleAttacks() {
        HashSet<Vector2> actions = getPossibleActionsOfType(Action.ATTACK);
        HashSet<Vector2> attacks = new HashSet<>();

        // Special case for an action that is both a move and an attack
        for(Vector2 action : actions) {
            AbstractChessPiece other = board.getPiece(action);
            if(other != null && other.alliance() != alliance())
                attacks.add(action);
        }

        return attacks;
    }

    /**
     * @return All legal actions this piece can perform
     */
    @Override
    public Set<Vector2> getPossibleActions() {
	    return getPossibleActionsOfType(Action.ACTION);
    }

	public boolean hasMoved() {
		return hasMoved;
	}
	public boolean canJump() { return canJump; }

	/**
	 * Performs a move, if it is legal
	 * @param destination End position of attempted move
     * @param board The board where the piece resides (Ensures that the piece is always in-sync with the board)
     * @return If the move is legal
	 */
	public boolean move(Vector2 destination, Board board) {
		this.board = board;

		if (!legalAction(destination)) return false; // If the destination is unreachable, the move fails

		position = destination;
		media.playSound("move.mp3");
		hasMoved = true;

		return true;
	}

    /**
     * Loads all piece-specific data (Data that is subject to change during the game)
     * @param vals Data values
     */
	public void loadData(List<Boolean> vals) {
		hasMoved = vals.get(0);
	}

	/**
	 * Checks one by one position from this position toward destination
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

    @Override
    public AbstractChessPiece clonePiece() {
        return clonePiece(position());
    }
}