package management;

import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.Vector2;

import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;

/**
 * gets a map over enemy's possible moves which is avoided.
 * always kill a piece if possible, prefer kills where you can't be caught back
 * last resort is random move
 */
public class ChessComputerMedium extends ChessComputer {
    private Alliance enemy;
    private ArrayList<Vector2> enemyMoves = new ArrayList<>();
    private ArrayList<IChessPiece> ownPieces = new ArrayList<>();
    private ArrayList<IChessPiece> inDanger = new ArrayList<>();
    private ArrayList<IChessPiece> enemyPieces = new ArrayList<>();

    public ChessComputerMedium(Board board) {
        super(board);
        //get enemy alliance
        if(alliance() == Alliance.WHITE) {
            enemy = Alliance.BLACK;
        } else {
            enemy = Alliance.WHITE;
        }
    }


    @Override
    public Move getMove() {
        getOwnPieces();
        getEnemyPieces();
        calcEnemyPossibleMoves();
        getPiecesInDanger();
        if(piecesInDanger()) {
            return getBestMove(inDanger, enemyPieces);
        }
        return getBestMove(ownPieces, enemyPieces);
    }

    private Move getBestMove(ArrayList<IChessPiece> inDanger, ArrayList<IChessPiece> enemyPieces) {
        Move bestKill = getBestKill(inDanger, enemyPieces);
        if(bestKill == null) {
            return randomMove(ownPieces);
        }
        return bestKill;
    }

    private Move randomMove(ArrayList<IChessPiece> pieces) {
        IChessPiece orgPos = getRandomPiece(pieces);
        Vector2 newPos = orgPos.getPossibleDestinations().iterator().next();
        return new Move(orgPos.position(),newPos);
    }

    private Move bestFlee(ArrayList<IChessPiece> inDanger) {
        ArrayList<Move> flees  = findFlees(inDanger);
        if(flees == null) {
            return null;
        }
    }

    private ArrayList<Move> findFlees(ArrayList<IChessPiece> inDanger) {
        ArrayList<Move> flees = new ArrayList<>();
        for(IChessPiece targeted: inDanger) {
            for(Vector2 dest: targeted.getPossibleDestinations()) {
                if(enemyMoves.contains(dest)){

                }
            }
        }
        getOpenSpace();
        inDanger.get(0).getPossibleDestinations();
    }

    private ArrayList<Vector2> getOpenSpace() {
        ArrayList<Vector2> openSpace = makeSpace();
        for (Vector2 point: openSpace) {
            if(enemyMoves.contains(point) || ownPieces.(point)) {
                openSpace.remove(point);
            }
        }
        return openSpace;
    }

    private ArrayList<Vector2> makeSpace() {
        ArrayList<Vector2> space = new ArrayList<>();
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                space.add(new Vector2(x,y));
            }
        }
        return space;
    }

    private void getEnemyPieces() {
        enemyPieces.addAll(board.getUsablePieces(enemy).values());
    }

    private Move getBestKill(ArrayList<IChessPiece> killers, ArrayList<IChessPiece> victims) {
        ArrayList<Move> killMoves = findKills(killers, victims);
        if(0 < killMoves.size()) {
            return null;
        }
        return killMoves.get(0);
    }

    private ArrayList<Move> findKills(ArrayList<IChessPiece> killers, ArrayList<IChessPiece> victims) {
        ArrayList<Move> killMoves = new ArrayList<>();
        for(IChessPiece killer: killers) {
            for(IChessPiece victim: victims)
            if(killer.getPossibleDestinations().contains(victim)) {
                killMoves.add(new Move(killer.position(), victim.position()));
            }
        }
        return killMoves;
    }

    private boolean piecesInDanger() {
        return 0 < inDanger.size();
    }

    private void getPiecesInDanger() {
        inDanger.clear();
        for (IChessPiece piece: ownPieces) {
            if (enemyMoves.contains(piece.position())) {
                inDanger.add(piece);
            }
        }
    }

    private void getOwnPieces() {
        ownPieces.clear();
        ownPieces = (ArrayList<IChessPiece>) board.getUsablePieces(alliance()).values();
    }

    private IChessPiece getRandomPiece(ArrayList<IChessPiece> pieces) {
        return pieces.get(fromZeroTo(pieces.size() - 1));
    }

    private void calcEnemyPossibleMoves() {
        enemyMoves.clear();
        for (IChessPiece p: board.getUsablePieces(enemy).values()) {
            enemyMoves.addAll(p.getPossibleDestinations("ChessComputerMedium"));
        }
    }
    private int fromZeroTo(int num) {
        return (int) Math.random() * num;
    }
}
