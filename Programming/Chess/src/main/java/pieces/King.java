package pieces;

import resources.*;
import management.*;

import java.util.List;

public class King extends ChessPiece<King> {

    /**
     *
     * @param position
     */
    public King(Vector2 position, Alliance alliance, Board board) {
        super(position, alliance, board, false, Piece.KING);
    }
    public King clone() {
        return new King(position, alliance, board);
    }

	public boolean legalMove(Vector2 move) {
		return false;
	}

	public List<Vector2> getPossibleMoves() {
		//TODO King.getPossibleMoves
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param move
	 * @param board
	 */
	public boolean legalMove(Vector2 move, Board board) {
		// TODO - implement King.legalMove
		throw new UnsupportedOperationException();
	}

	public boolean inCheck() {
		// TODO - implement King.inCheck
		throw new UnsupportedOperationException();
	}

	public boolean checkMate() {
		// TODO - implement King.checkMate
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param Rook
	 */
	public void castling(boolean Rook) {
		// TODO - implement King.castling
		throw new UnsupportedOperationException();
	}
}