package resources;

public class MoveScore implements Comparable<MoveScore>{
    private int score;
    private Move move;
    public MoveScore(int score, Move move) {
        this.score = score;
        this.move = move;
    }

    @Override
    public int compareTo(MoveScore other) {
        return this.score - other.score;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public String toString() {
        return score + ":" + move.toString();
    }
}
