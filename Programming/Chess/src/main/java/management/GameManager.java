package management;

import resources.*;

public class GameManager {
    public static RuleSet getGameRules(GameMode mode) {
        switch (mode) {
            case SHADAM:
                RuleSet shadam = new RuleSet();
                for(Vector2 v : Tools.unitVectors) {
                    Vector2 v2 = v.add(v);

                    Rule dam = new Rule(Piece.ANY, v2);
                    dam.setCriteria(v, Requirement.ANY, Requirement.OTHER);
                    dam.setCriteria(v2, Requirement.NONE, Requirement.NULL);

                    shadam.add(dam);
                }

                return shadam;
        }

        return new RuleSet();
    }
}
