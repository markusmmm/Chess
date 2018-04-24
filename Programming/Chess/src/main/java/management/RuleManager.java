package management;

import pieces.ChessPiece;
import resources.Move;
import resources.Piece;
import resources.Requirement;
import resources.Vector2;

import java.util.HashSet;
import java.util.Set;

public class RuleManager {
    private Board board;

    private class SubRule {
        Piece piece;
        Vector2 relativePosition;
        Requirement pieceRequirement;
        Requirement allianceRequirement;
        boolean canAttackAllies;

        private SubRule(Piece piece, Vector2 relativePosition, Requirement pieceRequirement, Requirement allianceRequirement, boolean canAttackAllies) {
            this.piece = piece;
            this.relativePosition = relativePosition;
            this.pieceRequirement = pieceRequirement;
            this.allianceRequirement = allianceRequirement;
            this.canAttackAllies = canAttackAllies;
        }

        @Override
        public SubRule clone() {
            return new SubRule(piece, relativePosition, pieceRequirement, allianceRequirement, canAttackAllies);
        }

        private boolean evaluate(Vector2 position) {
            if(!board.insideBoard(new Move(position, position.add(relativePosition)))) return false;

            ChessPiece p0 = board.getPiece(position);
            ChessPiece p1 = board.getPiece(position.add(relativePosition));

            if(p0 == null || !p0.piece().equals(piece)) return false;
            if(p1 == null) return pieceRequirement.equals(Requirement.NONE);
            if(!canAttackAllies && p1.alliance().equals(p0.alliance())) return false; // Prevents attack on allies

            boolean validPiece = pieceRequirement.equals(Requirement.ANY) ||
                    (pieceRequirement.equals(Requirement.OTHER) && !p1.piece().equals(p0.piece())) ||
                    (pieceRequirement.equals(Requirement.SAME) && p1.piece().equals(p0.piece()));

            boolean validAlliance = allianceRequirement.equals(Requirement.ANY) ||
                    (allianceRequirement.equals(Requirement.OTHER) && !p1.alliance().equals(p0.alliance())) ||
                    (allianceRequirement.equals(Requirement.SAME) && p1.alliance().equals(p0.alliance()));

            return validPiece && validAlliance;
        }

        // move validity is checked in RuleManager.execute
        private void execute(Vector2 position) {
            board.removePiece(position);
        }

        @Override
        public boolean equals(Object other) {
            if(!(other instanceof SubRule)) return false;
            SubRule o = (SubRule)other;

            return piece.equals(o.piece) && relativePosition.equals(o.relativePosition);
        }
    }

    private class Rule {
        SubRule
    }

    Set<SubRule> subRules = new HashSet<>();

    public RuleManager() {

    }

    public void setBoard(AbstractBoard board) {
        this.board = board == null ? null : (Board)board;
    }

    public SubRule getRule(Piece piece, Vector2 relativePosition) {
        for(SubRule subRule : subRules)
            if(subRule.piece.equals(piece) && subRule.relativePosition.equals(relativePosition))
                return subRule;

        return null;
    }

    /**
     * Adds a new rule. Overwrites any existing rule that matches the new rule's criteria
     * @param piece The piece that follows the rule (can be set to Piece.ANY)
     * @param relativePosition The destination of the move, relative to the piece's current position
     * @param pieceRequirement Requirement for piece type between the rule's piece and the destination's piece
     * @param allianceRequirement Requirement for alliance between the rule's alliance and the destination's alliance
     * @param canAttackAllies Whether or not
     */
    public void setRule(Piece piece, Vector2 relativePosition, Requirement pieceRequirement, Requirement allianceRequirement, boolean canAttackAllies) {
        SubRule newSubRule = new SubRule(piece, relativePosition, pieceRequirement, allianceRequirement, canAttackAllies);
        subRules.add(newSubRule);
    }

    /**
     * Removes a rule, if it exists
     * @param piece The rule's piece
     * @param relativePosition The rule's relative position
     * @return Whether or not the piece existed before removal
     */
    public boolean removeRule(Piece piece, Vector2 relativePosition) {
        SubRule subRule = getRule(piece, relativePosition);
        if(subRule == null) return false;

        subRules.remove(subRule);
        return true;
    }

    public boolean evaluate(Move move) {
        if(!board.insideBoard(move)) return false;

        ChessPiece p = board.getPiece(move.start);
        if(p == null) return false;

        SubRule subRule = getRule(p.piece(), move.end.subtract(move.start));
        if(subRule == null) return false;

        return subRule.evaluate(move.start);
    }

    public boolean execute(Move move) {
        if(!evaluate(move)) return false;

        getRule(board.getPiece(move.start).piece(), move.end.subtract(move.start)).execute(move.start);
        return true;
    }
}
