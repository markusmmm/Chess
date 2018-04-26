package management;

import pieces.ChessPiece;
import resources.Move;
import resources.Piece;
import resources.Requirement;
import resources.Vector2;

import java.util.HashSet;

public class Rule {

    private class Criteria {
        Vector2 relativeDestination;
        Requirement pieceRequirement;
        Requirement allianceRequirement;

        /**
         * Creates a new subRule
         * @param relativeDestination The move's destination, relative to the move start
         * @param targetPiece Requirement for the target's piece
         * @param targetAlliance Requirement for the target's alliance
         */
        private Criteria(Vector2 relativeDestination, Requirement targetPiece, Requirement targetAlliance) {
            this.relativeDestination = relativeDestination;
            this.pieceRequirement = targetPiece;
            this.allianceRequirement = targetAlliance;
        }

        @Override
        public Criteria clone() {
            return new Criteria(relativeDestination, pieceRequirement, allianceRequirement);
        }

        /**
         * Evaluates the rule for the given position at the given board
         * @param position Position of the piece to check
         * @return
         */
        private boolean evaluate(Vector2 position) {
            if(!board.insideBoard(new Move(position, position.add(relativeDestination)))) return false;

            ChessPiece p0 = board.getPiece(position);
            ChessPiece p1 = board.getPiece(position.add(relativeDestination));

            if(p0 == null || !p0.piece().equals(piece)) return false;
            if(p1 == null) return pieceRequirement == Requirement.NONE || pieceRequirement == Requirement.NULL;

            // Checks if attack is legal
            boolean sameAlliance = p0.alliance().equals(p1.alliance());
            if(pieceRequirement == Requirement.ANY && (
                    (allianceRequirement == Requirement.OTHER && sameAlliance) ||
                    (allianceRequirement == Requirement.SAME && !sameAlliance)   )) {
                return false;
            }

            boolean validPiece = pieceRequirement.equals(Requirement.ANY) ||
                    (pieceRequirement.equals(Requirement.OTHER) && !p1.piece().equals(p0.piece())) ||
                    (pieceRequirement.equals(Requirement.SAME) && p1.piece().equals(p0.piece()));

            boolean validAlliance = allianceRequirement.equals(Requirement.ANY) ||
                    (allianceRequirement.equals(Requirement.OTHER) && !p1.alliance().equals(p0.alliance())) ||
                    (allianceRequirement.equals(Requirement.SAME) && p1.alliance().equals(p0.alliance()));

            return validPiece && validAlliance;
        }

        // move validity is checked in Rule.execute
        private void execute(Vector2 position) {
            if(pieceRequirement == Requirement.ANY)
                board.removePiece(position.add(relativeDestination));
        }

        @Override
        public boolean equals(Object other) {
            if(!(other instanceof Criteria)) return false;
            Criteria o = (Criteria)other;

            return relativeDestination.equals(o.relativeDestination);
        }

        @Override
        public String toString() {
            if(pieceRequirement == Requirement.NULL && allianceRequirement == Requirement.NULL)
                return "N/A";

            String str = "Square at " + relativeDestination + " ";
            if(pieceRequirement == Requirement.NONE) {
                str += "is empty";
            } else if (allianceRequirement != Requirement.NULL){
                str += "contains any piece with " + allianceRequirement + " alliance";
            }

            return str;
        }
    }

    private final Piece piece;
    private final Vector2 relativeDestination;
    private Board board;
    private final HashSet<Criteria> criteria;

    /**
     * Creates a new rule
     * @param relativeDestination Destination of the performed move, relative to the move's start position
     */
    public Rule(Piece piece, Vector2 relativeDestination) {
        this.piece = piece;
        this.relativeDestination = relativeDestination;
        criteria = new HashSet<>();
    }

    public Rule(Rule other) {
        piece = other.piece;
        relativeDestination = other.relativeDestination;
        board = other.board;
        criteria = (HashSet<Criteria>)other.criteria.clone();
    }
    @Override
    public Rule clone() {
        return new Rule(this);
    }

    public void setBoard(AbstractBoard board) {
        this.board = board == null ? null : (Board)board;
    }

    /**
     * Updates rule criteria. Criteria is added if it doesn't already exist
     * @param relativeDestination The destination of the move, relative to the piece's current position
     * @param pieceRequirement Requirement for piece type between the rule's piece and the destination's piece
     * @param allianceRequirement Requirement for alliance between the rule's alliance and the destination's alliance
     */
    public void setCriteria(Vector2 relativeDestination, Requirement pieceRequirement, Requirement allianceRequirement) {
        Criteria newCriteria = new Criteria(relativeDestination, pieceRequirement, allianceRequirement);
        criteria.add(newCriteria);
    }

    /**
     * Checks if the move meets all rule criteria
     * @param move Move to check
     * @return Move validity
     */
    public boolean evaluate(Move move) {
        if(board == null) return false;
        if(!board.insideBoard(move)) return false;

        for(Criteria criteria : this.criteria)
            if(!criteria.evaluate(move.start))
                return false;
        return true;
    }

    public boolean execute(Move move) {
        if(!evaluate(move)) return false;

        for(Criteria criteria : this.criteria)
            criteria.execute(move.start);
        return true;
    }

    @Override
    public String toString() {
        String str = piece == Piece.ANY ? "- Any piece" : piece.toString();
        str += " can move to " + relativeDestination;
        String critStr = "";
        for(Criteria crit : criteria) {
            String temp = "\t* " + crit + "\n";
            if(temp.equals("N/A")) continue;
            critStr += temp;
        }
        if(!critStr.equals(""))
            str += " if:\n" + critStr;

        return str;
    }
}
