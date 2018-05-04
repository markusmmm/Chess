package management;

import resources.Console;
import resources.Move;
import resources.Tools;

import java.util.*;

public class ChessComputerEasy extends ChessComputer {
    public ChessComputerEasy(Board board) {
        super(board);
    }

    public Move getMove() {
        Tools<Move> tools = new Tools<>();

        Set<Move> attacks = board.getAllPossibleAttacks(alliance());
        Set<Move> moves = board.getAllPossibleMoves(alliance());

        // Prioritize attacks
        if(attacks.size() > 0) {
            Console.printNotice(this + " is attacking");
            return tools.randomElem(attacks);
        }
        if(moves.size() > 0) {
            Console.printNotice(this + " is moving");
            return tools.randomElem(moves);
        }

        return null;
    }
}
