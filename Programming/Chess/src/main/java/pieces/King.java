package pieces;

import management.AbstractBoard;
import management.Board;
import management.ChessRanking;
import resources.Alliance;
import resources.Piece;
import resources.Vector2;

import java.util.*;

public class King extends ChessPiece {
    private Board clonedBoard;
    private final int value = 2;
    private Set<Vector2> moves = new HashSet<>(Arrays.asList(
        new Vector2(-1, -1), new Vector2( 0, -1), new Vector2( 1, -1),
        new Vector2(-1,  0), new Vector2( 1,  0), new Vector2(-1,  1),
            new Vector2( 0,  1), new Vector2( 1,  1),
            new Vector2( -2,  0), new Vector2( 2,  0)
    ));

    /**
     * @param position
     */
    public King(Vector2 position, Alliance alliance, AbstractBoard board, Boolean hasMoved) {

        super(position, alliance, board, false, Piece.KING, 2, hasMoved);


    }

    public King clonePiece() {
        return new King(position, alliance, board, hasMoved());
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public boolean legalMove(Vector2 destination) {
        //IMPORTANT! King can NOT call super.legalMove, as the king demands a custom alliance check (when performing castling),
        //and does not need to perform the inCheck-call that occurs from within super

        if(!(board.insideBoard(position) && board.insideBoard(destination))) return false;

        IChessPiece endPiece = board.getPiece(destination);
        int kingSideRookX = destination.getX()+1;
        int queenSideRookX = destination.getX()-2;

        if (castling(new Vector2(kingSideRookX,destination.getY())) || castling(new Vector2(queenSideRookX, destination.getY()))){
            return true;
        }

        if(endPiece != null && endPiece.alliance().equals(alliance)) return false; // Temporary fix, until castling has been integrated

        if(inCheck()) return false;

        // Ensures that the king can't be moved into check
        // Ignored if king is not on live board
        if(movesIntoCheck(destination)) return false;

        return (
            (inDiagonals(destination) || inStraights(destination)) &&
            position.distance(destination) == 1 &&
            freePath(destination)
        );
    }

    public Set<Vector2> getPossibleDestinations(String caller) {
        logActionPossibleDestinations(caller);

        Set<Vector2> possibleMoves = new HashSet<>();

        for(Vector2 move : moves) {
            Vector2 endPos = position.add(move);

            if(legalMove(endPos))
                possibleMoves.add(endPos);
        }

        return possibleMoves;
    }

    public boolean inCheck(Vector2 destination) {
        clonedBoard = board.clone();
        // The inactive player's king can NOT be in check (Impossible state)
        if(!board.getActivePlayer().equals(alliance)) return false;

        // NEVER USE 'board.getUsablePieces' WITHIN THIS METHOD (WILL CREATE A CIRCULAR METHOD CALL)
        HashMap<Vector2, IChessPiece> hostilePieces = clonedBoard.getPieces(otherAlliance());

        for(Vector2 key : hostilePieces.keySet()) {
            IChessPiece piece = hostilePieces.get(key);

            // Custom handling for king, as the default implementation causes an infinite circular call
            if(piece instanceof King) {
                King hostileKing = (King) piece;
                if(destination.distance(hostileKing.position()) == 1)
                    return true;
            }
            else if(piece instanceof Pawn) {
                Pawn hostilePawn = (Pawn) piece;
                Set<Vector2> attacks = hostilePawn.getPossibleAttacks();
                if(attacks.contains(destination))
                    return true;
            }
            else {
                Set<Vector2> destinations = piece.getPossibleDestinations(toString());
                if (destinations.contains(destination))
                    return true;
            }
        }

        return false;
    }
    public boolean inCheck() {
        return inCheck(position);
    }

    private boolean movesIntoCheck(Vector2 end) {
        clonedBoard.addPiece(end, Piece.KING, alliance);


        board.suspendPieces(position);

        boolean setsCheck = inCheck(end);

        board.releasePieces(position);

        return setsCheck;
    }

    public boolean resolvesCheck(Vector2 start, Vector2 end) {
        // TODO Add alliance check (A hostile piece cannot resolve check)

        if(start.equals(position))
            return !movesIntoCheck(end);

        if(!inCheck()) return true;



        Set<Vector2> endangered = new HashSet<>();
        Set<Vector2> destinations = new HashSet<>();

        for (Vector2 move : moves)
            destinations.add(position.add(move));

        board.suspendPieces(position);

        for (Vector2 destination : destinations) {
            if (inCheck(destination)) {
                endangered.add(destination);
            }
        }

        endangered.remove(end);

        board.releasePieces(position);

        return endangered.size() == 0;

    }

    public boolean checkmate() {
        return inCheck() && board.getUsablePieces(alliance).size() == 0;
    }
    public boolean stalemate() {
        return !inCheck() && board.getUsablePieces(alliance).size() == 0;
    }

    public boolean castling(Vector2 pos){
        if(board.getKing(this.alliance).hasMoved()) return false;


        if(inCheck()) return false;

        IChessPiece rook = board.getPiece(pos);

        if(!(rook instanceof Rook)) return false;

        if(rook.hasMoved()) return false;

        int x = this.position.getX();
        int y = this.position.getY();

        int rookX = pos.getX();
        int rookY = pos.getY();

        int diff = x - rookX;

        if(!((Rook) rook).freePath(this.position)) return false;

            //queen side
        if(diff > 0){

            Vector2 pos1 = new Vector2(x-1,y);
            Vector2 pos2 = new Vector2(x-2,y);
            if(!inCheck(pos1) && !inCheck(pos2)){
                return true;

            }
            //kingside
        } else {
            Vector2 pos1 = new Vector2(x+1,y);
            Vector2 pos2 = new Vector2(x+2,y);
            if(!inCheck(pos1) && !inCheck(pos2)){
                return true;
        }

        }
        return false;

        }


}