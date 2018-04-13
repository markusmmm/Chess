package management;

import pieces.ChessPiece;
import pieces.IChessPiece;
import pieces.King;
import pieces.Pawn;
import resources.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Board extends AbstractBoard {
	private static final Piece[] defaultBoard = new Piece[] {
			Piece.ROOK,   Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN,  Piece.KING,   Piece.BISHOP, Piece.KNIGHT, Piece.ROOK,
			Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,
			Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,
			Piece.PAWN,   Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,  Piece.EMPTY,
			Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,   Piece.PAWN,
			Piece.ROOK,   Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN,  Piece.KING,   Piece.BISHOP, Piece.KNIGHT, Piece.ROOK
	};

	private Board(Board template) {
		super(template);
	}

    public Board(int size) {
        super(size, false);
    }
    public Board(int size, boolean useClock, BoardMode mode) {
    	super(size, useClock);

		if(mode == BoardMode.DEFAULT) {
			int p = 0;

			for (Piece type : defaultBoard) {
				int x = p % size;
				int y = p / size;

				Vector2 pos = new Vector2(x, y);
				Vector2 invPos = new Vector2(x, size - y - 1);

				if (type.equals(Piece.EMPTY)) continue;

				addPiece(pos, type, Alliance.BLACK);
				System.out.println(pos + ": " + getPiece(pos));

				addPiece(invPos, type, Alliance.WHITE);
				System.out.println(invPos + ": " + getPiece(invPos));

				p++;
			}
		} else if (mode == BoardMode.RANDOM) {
			generateRandomBoard();
		}
	}
    public Board(String fileName) throws FileNotFoundException {
    	super(fileName);
	}

	/*
	public boolean undoMove() {
    	if(gameLog.size() == 0) return false;

    	MoveNode lastMove = gameLog.pop();

    	pieces.remove(lastMove.end);
    	pieces.put(lastMove.start, lastMove.piece);

		ChessPiece victim = lastMove.victimPiece;
		if(victim != null)
			pieces.put(victim.position(), victim);

    	return true;
	}
	*/

	private void generateRandomBoard() {
		int bRooks = 0, bPawns = 0, bQueens = 0, bKings = 0, bBishops = 0, bKnights = 0;
		int wRooks = 0, wPawns = 0, wQueens = 0, wKings = 0, wBishops = 0, wKnights = 0;
		int w = 0;
		int b = 0;

		int bishopX = 0;
		int bishopY = 0;

		int bishopWX = 0;
		int bishopWY = 0;

		Random random = new Random();
		while (b < 16) {
			Piece aPiece = randomPiece();
			int x = random.nextInt(7 - 0 + 1) + 0;
			int y = random.nextInt(7 - 0 + 1) + 0;

			Vector2 pos = new Vector2(x, y);
			if (!vacant(pos)) {
				continue;
			}
			if (b == 15 && bKings == 0) {
				addPiece(pos, Piece.KING, Alliance.BLACK);
				if (getKing(Alliance.BLACK).inCheck()) {
					removePiece(pos);
					continue;
				}
				w++;
				wKings++;
				continue;
			}

			if (aPiece.equals(Piece.ROOK) && bRooks < 2) {

				addPiece(pos, aPiece, Alliance.BLACK);


				b++;
				bRooks++;
				continue;
			}
			if (aPiece.equals(Piece.PAWN) && bPawns < 8) {
				if (y == 0) {
					continue;
				}
				addPiece(pos, aPiece, Alliance.BLACK);
				b++;
				bPawns++;
				continue;
			}
			if (aPiece.equals(Piece.QUEEN) && bQueens < 1) {
				addPiece(pos, aPiece, Alliance.BLACK);
				b++;
				bQueens++;
				continue;
			}
			if (aPiece.equals(Piece.KING) && bKings < 1) {

				addPiece(pos, aPiece, Alliance.BLACK);
				if (getKing(Alliance.BLACK).inCheck()) {
					removePiece(pos);
					continue;
				}
				b++;
				bKings++;
				continue;
			}
			if (aPiece.equals(Piece.BISHOP) && bBishops < 2) {
				if (bBishops == 0) {
					bishopX = x;
					bishopY = y;
				}
				if (bBishops == 1) {
					boolean b1 = bishopX % 2 == bishopY % 2;
					boolean b2 = x % 2 == y % 2;
					if (b1 == b2) {
						continue;
					}
				}
				addPiece(pos, aPiece, Alliance.BLACK);
				b++;
				bBishops++;
				continue;
			}
			if (aPiece.equals(Piece.KNIGHT) && bKnights < 2) {
				addPiece(pos, aPiece, Alliance.BLACK);
				b++;
				bKnights++;
				continue;
			}
			if (aPiece.equals(Piece.EMPTY)) {
				b++;
				continue;
			}
		}
		while (w < 16) {
			Piece aPiece = randomPiece();

			int x = random.nextInt(7 - 0 + 1) + 0;
			int y = random.nextInt(7 - 0 + 1) + 0;

			Vector2 invPos = new Vector2(x, y);
			if (!vacant(invPos)) {
				continue;
			}
			if (w == 15 && wKings == 0) {
				addPiece(invPos, Piece.KING, Alliance.WHITE);
				if (getKing(Alliance.WHITE).inCheck()) {
					removePiece(invPos);
					continue;
				}
				w++;
				wKings++;
				continue;
			}
			if (aPiece.equals(Piece.ROOK) && wRooks < 2) {
				addPiece(invPos, aPiece, Alliance.WHITE);
				w++;
				wRooks++;
				continue;
			}
			if (aPiece.equals(Piece.PAWN) && wPawns < 8) {
				if (y == 7) {
					continue;
				}
				addPiece(invPos, aPiece, Alliance.WHITE);
				w++;
				wPawns++;
				continue;
			}
			if (aPiece.equals(Piece.QUEEN) && wQueens < 1) {
				addPiece(invPos, aPiece, Alliance.WHITE);
				w++;
				wQueens++;
				continue;
			}
			if (aPiece.equals(Piece.KING) && wKings < 1) {

				addPiece(invPos, aPiece, Alliance.WHITE);
				if (getKing(Alliance.WHITE).inCheck()) {
					removePiece(invPos);
					continue;
				}

				w++;
				wKings++;
				continue;
			}
			if (aPiece.equals(Piece.BISHOP) && wBishops < 2) {
				if (wBishops == 0) {
					bishopWX = x;
					bishopWY = y;
				}
				if (wBishops == 1) {
					boolean b1 = bishopWX % 2 == bishopWY % 2;
					boolean b2 = x % 2 == y % 2;
					if (b1 == b2) {
						continue;
					}
				}
				addPiece(invPos, aPiece, Alliance.WHITE);
				w++;
				wBishops++;
				continue;
			}
			if (aPiece.equals(Piece.KNIGHT) && wKnights < 2) {
				addPiece(invPos, aPiece, Alliance.WHITE);
				w++;
				wKnights++;
				continue;
			}
			if (aPiece.equals(Piece.EMPTY)) {
				b++;
				continue;
			}
		}
	}

    public HashMap<Vector2, IChessPiece> getPieces(Alliance alliance) {
        HashMap<Vector2, IChessPiece> temp = new HashMap<>();

        for (Vector2 pos : getPositions()) {
            IChessPiece piece = getPiece(pos);
            if (piece == null) continue;

            if (insideBoard(pos) && piece.alliance().equals(alliance)) {
                temp.put(pos, piece);
            }
        }

        return temp;
    }

    public HashMap<Vector2, IChessPiece> getUsablePieces(Alliance alliance) {
        HashMap<Vector2, IChessPiece> usablePieces = new HashMap<>();

        Set<Vector2> positions = getPositions();
        for (Vector2 pos : positions) {
            IChessPiece piece = getPiece(pos);
            if (piece == null) continue; // Ignore empty squares

            if (insideBoard(pos) && piece.alliance().equals(alliance)) {
                Set<Vector2> possibleDestinations = piece.getPossibleDestinations("Board");
                if (possibleDestinations.size() == 0) continue; // If the piece has no valid moves, ignore it

                usablePieces.put(pos, piece);
            }
        }

        return usablePieces;
    }

    public King getKing(Alliance alliance) {
        HashMap<Vector2, IChessPiece> temp = getPieces(alliance);
        for (Vector2 pos : temp.keySet())
            if (temp.get(pos) instanceof King)
                return (King) temp.get(pos);

        return null;
    }

    /**
     * @param start
     * @param end
     */
    public boolean movePiece(Vector2 start, Vector2 end) {
        if (!insideBoard(start)) return false;

        ChessPiece piece = (ChessPiece) getPiece(start);

        System.out.println("Currently " + activePlayer + "'s turn");

        if (piece == null) {
            return false; // Check if a piece exists at the given position
        }
        if (!piece.alliance().equals(activePlayer)) {
            return false; // Checks if the active player owns the piece that is being moved
        }

        //castling
        if(piece instanceof  King){
            int kingSideRookX = end.getX()+1;
            int queenSideRookX = end.getX()-2;

            // castling kingside
            if (((King) piece).castling(new Vector2(kingSideRookX,end.getY()))){
                Vector2 rookPos = new Vector2(kingSideRookX, end.getY());
                IChessPiece rook = getPiece(rookPos);
                Alliance alliance = rook.alliance();
                Piece pieceType = rook.piece();

                IChessPiece king = getKing(alliance);
                Vector2 kingPos = king.position();

                removePiece(rookPos);
                removePiece(kingPos);


                addPiece(new Vector2(kingSideRookX-2, end.getY()), pieceType, alliance);
                addPiece(new Vector2(kingSideRookX-1, end.getY()), Piece.KING, alliance);

                logMove(new MoveNode(piece, start, end, (ChessPiece) getPiece(end)));
                activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;

                return true;

            }

            // castling queenside
            if (((King) piece).castling(new Vector2(queenSideRookX,end.getY()))){
                Vector2 rookPos = new Vector2(queenSideRookX, end.getY());
                IChessPiece rook = getPiece(rookPos);
                Alliance alliance = rook.alliance();
                Piece pieceType = rook.piece();

                IChessPiece king = getKing(alliance);
                Vector2 kingPos = king.position();

                removePiece(rookPos);
                removePiece(kingPos);


                addPiece(new Vector2(queenSideRookX+3, end.getY()), pieceType, alliance);
                addPiece(new Vector2(queenSideRookX+2, end.getY()), Piece.KING, alliance);

                logMove(new MoveNode(piece, start, end, (ChessPiece) getPiece(end)));
                activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;

                return true;


            }

        }

        if(piece instanceof Pawn)
		{
			if(((Pawn) piece).promotion(end))
			{
				IChessPiece pawnPromoted = piece;
				Vector2 pawnPos = pawnPromoted.position();
				Alliance pawnAlliance = pawnPromoted.alliance();

				removePiece(pawnPos);
				addPiece(new Vector2(end.getX(), end.getY()), Piece.QUEEN, pawnAlliance);
				return true;
			}
		}
        boolean moveSuccessful = piece.move(end);

        if (!moveSuccessful) { // Attempt to move the piece
            System.out.println("piece.move failed. Mutex released");
            return false;
        }

        setLastPiece(piece);
        ChessPiece endPiece = (ChessPiece) getPiece(end);

        ChessPiece victim = null;
        if (endPiece != null) {
            //Remove hostile attacked piece
            if (!endPiece.alliance().equals(piece.alliance())) {
                capturePiece(endPiece);
                removePiece(end);
                victim = endPiece;
            }
        }

        //assert(piece.position().equals(end));

        logMove(new MoveNode(piece, start, end, victim));

        removePiece(start);
        putPiece(end, piece);
        addDrawPos(start);
        addDrawPos(end);

        //After a successful move, advance to the next player
        activePlayer = activePlayer.equals(Alliance.WHITE) ? Alliance.BLACK : Alliance.WHITE;

        //System.out.println("Local after: " + piece.position() + ", has moved: " + piece.hasMoved());
        //System.out.println("Move successful!");

        return true;
    }

    public boolean movePiece(Move move) {
        return movePiece(move.start, move.end);
    }

	public void performAttack(Vector2 start, Vector2 end, Vector2 victim) {
		MoveNode node = new MoveNode(getPiece(start), start, end, getPiece(victim));
		System.out.println("Performing attack: " + node);

		removePiece(victim);
		logMove(node);
	}

    @Override
    public Board clone() {
        return new Board(this);
    }
}