package management;

import resources.*;

import java.util.Random;
import java.util.Set;

public class RandomBoard extends Board {

    public RandomBoard(int size, int difficulty, boolean useClock) {
        super(size, difficulty, useClock, BoardMode.DEFAULT);
        generateRandomBoard(8,14);
    }

    /**
     * Generates a random board, by performing n random moves (n is a random even integer within a given boundary)
     * @param minN Minimum amount of steps
     * @param maxN Maximum amount of steps
     *
     */
    private void generateRandomBoard(int minN, int maxN) {
        int bound = maxN - minN;
        if(bound < 0) throw new IllegalArgumentException("maxN must be greater than or equal to minN");

        Random rand = new Random();
        int n = bound == 0 ? minN : rand.nextInt(bound) + minN;
        if(n % 2 != 0) n++;     // Ensure that n is always even (otherwise, the starting move would go to the AI)

        Tools<Move> tools = new Tools<>();

        for(int i = 0; i < n; i++) {
            Set<Move> possibleMoves = getAllLegalActions(getActivePlayer());

            boolean state = movePiece(tools.randomElem(possibleMoves));
            if(!state) throw new IllegalStateException("Illegal move attempted while generating random board");
        }
    }
}
