package management;

import resources.*;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Set;

public class RandomBoard extends Board {

    public RandomBoard(int size, int difficulty, boolean useClock) {
        super(size, difficulty, useClock, BoardMode.DEFAULT);
        generateRandomBoard(6,10);
    }

    /**
     * Generates a random board, by performing n random moves (n is a random value, within a boundary)
     * @param minN Minimum amount of steps
     * @param maxN Maximum amount of steps
     *
     */
    private void generateRandomBoard(int minN, int maxN) {
        if(minN % 2 != 0) minN++;
        if(maxN % 2 != 0) maxN++;
        int bound = maxN - minN;
        if(bound < 0) throw new IllegalArgumentException("maxN must be greater than or equal to minN");

        Random rand = new Random();
        int n = bound == 0 ? minN : rand.nextInt(bound) + minN;

        Tools<Move> tools = new Tools<>();

        for(int i = 0; i < n; i++) {
            Set<Move> possibleMoves = getAllPossibleActions(getActivePlayer());

            boolean state = movePiece(tools.randomElem(possibleMoves));
            if(!state) throw new IllegalStateException("Illegal move attempted while generating random board");
        }
    }
}
