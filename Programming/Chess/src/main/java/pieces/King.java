package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.*;

public class King extends AbstractChessPiece {
    /**
     *
     */
    public King(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
        super(position, alliance,
                vectorTools.addAll(Vector2.UNIT, new Vector2(2,0), new Vector2(-2,0)),
                MoveType.STEP, board, false, Piece.KING, 2, hasMoved);
    }
    public King(King other) {
        super(other);
    }

    @Override
    public AbstractChessPiece clonePiece() {
        return new King(this);
    }

    @Override
    public boolean legalMove(Vector2 destination) {
        Vector2 position = position();

        //IMPORTANT! King can NOT call super.legalMove, as the king demands a custom alliance check (when performing castling),
        //and CAN'T perform the inCheck-call that occurs from within super (will create an infinite recursion call)

        if(!(board.insideBoard(position) && board.insideBoard(destination))) return false;

        IChessPiece endPiece = board.getPiece(destination);
        int kingSideRookX = destination.getX()+1;
        int queenSideRookX = destination.getX()-2;

        if (castling(new Vector2(kingSideRookX,destination.getY())) || castling(new Vector2(queenSideRookX, destination.getY()))){

            return true;
        }

        if(endPiece != null && endPiece.alliance().equals(alliance)) return false; // Temporary fix, until castling has been integrated

        // Ensures that the king can't be moved into check
        // Ignored if king is not on live board
        if(movesIntoCheck(destination)) return false;

        return (
                (inDiagonals(destination) || inStraights(destination)) &&
                        position.distance(destination) == 1 &&
                        freePath(destination)
        );
    }


    public boolean inCheck(Vector2 destination) {
        if(!board.getActivePlayer().equals(alliance))
            return false;

        Vector2 position = position();

        boolean checked = false;
        board.suspendPieces(position);

        HashMap<Vector2, AbstractChessPiece> hostilePieces = board.getPieces(otherAlliance());
        for(IChessPiece hostile : hostilePieces.values()) {
            if(hostile instanceof Pawn) {
                if(((Pawn) hostile).getPossibleAttacks().contains(destination)) {
                    checked = true;
                    break;
                }
                continue;
            }

            if(hostile.getPossibleDestinations().contains(destination)) {
                checked = true;
                break;
            }
        }

        board.releasePieces(position);
        return checked;
    }
    public boolean inCheck() {
        return inCheck(position());
    }

    private boolean movesIntoCheck(Vector2 end) {
        Vector2 position = position();

        board.suspendPieces(position);
        board.suspendPieces(end);

        boolean setsCheck = inCheck(end);

        board.releasePieces(position);
        board.releasePieces(end);

        return setsCheck;
    }

    /**
     * Determines if the attempted relativeMovement puts the king in check
     * @param start Start position of the attempted relativeMovement
     * @param end End position of the attempted relativeMovement
     * @return Whether or not the relativeMovement successfully protects the king
     */
    public boolean resolvesCheck(Vector2 start, Vector2 end) {
        //resources.Console.printNotice("\nSimulating relativeMovement " + new Move(start, end));
        //resources.Console.printCaller();

        if(start.equals(position()))
            return movesIntoCheck(end);

        Board tempBoard = board.clone();
        AbstractChessPiece piece = board.getPiece(start);

        board.forceMovePiece(start, end);
        boolean checked = inCheck();

        board.sync(tempBoard);

        return !checked;
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

        Vector2 position = position();

        int x = position.getX();
        int y = position.getY();

        int rookX = pos.getX();
        int rookY = pos.getY();

        int diff = x - rookX;

        if(!((Rook) rook).freePath(position)) return false;

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