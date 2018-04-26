package management;

import resources.Move;

import java.util.HashSet;

public class RuleSet {
    private HashSet<Rule> rules = new HashSet<>();

    public RuleSet() {

    }
    public RuleSet(RuleSet other) {
        rules = (HashSet<Rule>)other.rules.clone();
    }

    public void add(Rule newRule) {
        rules.add(newRule);
    }
    public void remove(Rule rule) {
        rules.remove(rule);
    }

    public void setBoard(AbstractBoard board) {
        for(Rule rule : rules)
            rule.setBoard(board);
    }

    /**
     * Checks if any of the rules validates the given move
     * @param move Move to check
     * @return Move validity
     */
    public boolean evaluate(Move move) {
        for(Rule rule : rules)
            if(rule.evaluate(move)) return true;
        return false;
    }

    @Override
    public RuleSet clone() {
        return new RuleSet(this);
    }

    @Override
    public String toString() {
        String str = "RULESET (" + rules.size() + " rules):\n";
        for(Rule rule : rules)
            str += rule;

        return str;
    }
}
