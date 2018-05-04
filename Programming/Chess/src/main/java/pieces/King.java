package pieces;

import management.AbstractBoard;
import management.Board;
import resources.*;

import java.util.*;

public class King extends ChessPiece {
    /**
     *
     */
    public King(Vector2 position, Alliance alliance, AbstractBoard board, boolean hasMoved) {
        super(position, alliance,
                vectorTools.addAll(Vector2.UNIT, new Vector2(2,0), new Vector2(-2,0)),
                ActionType.STEP, board, false, Piece.KING, 2, hasMoved);
    }
    private King(Vector2 position, King other) { super(position, other); }

    @Override
    public AbstractChessPiece clonePiece(Vector2 position) {
        return new King(position,this);
    }

    @Override
    public boolean legalAction(Vector2 destination) {
        if(!super.legalAction(destination)) return false;

        if(Math.abs(destination.sub(position()).getX()) > 1) return castling(destination.add(Vector2.E), destination.add(Vector2.W.mult(2)));
        return true;
    }


    public boolean inCheck(Vector2 destination) {
        if(!board.getActivePlayer().equals(alliance))
            return false;

        Vector2 position = position();

        boolean checked = false;
        board.suspendPieces(position);

        HashMap<Vector2, AbstractChessPiece> hostilePieces = board.getPieces(otherAlliance());
        for(IChessPiece hostile : hostilePieces.values()) {
            if(hostile.getPossibleAttacks().contains(destination)) {
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
     * Determines if the attempted move puts the king in check
     * @param start Start position of the attempted move
     * @param end End position of the attempted move
     * @return Whether or not the move successfully protects the king
     */
    public boolean resolvesCheck(Vector2 start, Vector2 end) {
        if(start.equals(position()))
            return !movesIntoCheck(end);

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
    public boolean castling(Vector2... positions) {
        for(Vector2 pos : positions)
            if(castling(pos)) return true;
        return false;
    }

}