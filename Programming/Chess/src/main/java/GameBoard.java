import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import management.Board;
import pieces.IChessPiece;
import resources.Alliance;
import resources.Vector2;

import java.util.Map;

public class GameBoard {
    private final int SIZE = 8;
    private Board board;
    private GridPane grid;
    private Tile[][] tiles;
    private Rectangle[][] squares;
    private String username;
    private int difficulty;

    private boolean firstClick;
    private Tile firstTile;

    public GameBoard(String username, int difficulty) {
        this.board = new Board(SIZE, false);
        this.grid = new GridPane();
        this.tiles = new Tile[SIZE][SIZE];
        this.squares = new Rectangle[SIZE][SIZE];
        this.username = username;
        this.difficulty = difficulty;
        this.firstClick = false;
        this.firstTile = null;
    }

    /**
     * Setups the necessary tiles and structures to be able to create a board
     */
    public void createBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle rect = new Rectangle();
                Vector2 pos = new Vector2(col, row);
                Tile tile;

                if ((row + col) % 2 == 0) {
                    tile = new Tile(pos, board);
                    rect.setFill(Color.LIGHTGRAY);
                } else {
                    tile = new Tile(pos, board);
                    rect.setFill(Color.DARKGRAY);
                }

                tile.setOnMouseClicked(e -> {
                    //tile.tileClicked(e, Alliance.WHITE);
                    tileClick(e, tile, Alliance.WHITE);
                });

                tiles[row][col] = tile;
                squares[row][col] = rect;
                grid.add(rect, col, row);
                grid.add(tile, col, row);
                rect.widthProperty().bind(grid.widthProperty().divide(SIZE));
                rect.heightProperty().bind(grid.heightProperty().divide(SIZE));
                tile.widthProperty().bind(grid.widthProperty().divide(SIZE));
                tile.heightProperty().bind(grid.heightProperty().divide(SIZE));
            }
        }

        drawBoard();
    }

    private boolean tileClick(MouseEvent e, Tile tile, Alliance alliance) {
        Vector2 pos = tile.getPos();
        IChessPiece piece = board.getPiece(pos);

        if (firstClick && firstTile.getPos() != pos) {
            System.out.println(firstClick + " " + piece);
            if(board.movePiece(firstTile.getPos(), pos)) {
                drawBoard();
                firstTile.setFill(Color.TRANSPARENT);
                System.out.println("Moving " + piece + "from " + firstTile.getPos() + "to " + pos);
            }
            firstClick = false;
            firstTile = null;
        } else {
            System.out.print(pos + ": ");
            if (piece == null) {
                System.out.println("No piece");
                return false;
            } else if (piece.alliance() != alliance) {
                System.out.println("Not your alliance");
                return false;
            }
            System.out.print(piece);

            firstClick = true;
            firstTile = tile;
            return true;
        }
        return false;
    }

    public void drawBoard() {
        for (Vector2 pos : board.pieces.keySet()) {
            int col = pos.getX();
            int row = pos.getY();
            tiles[row][col].drawPiece();
        }
    }

    public void printTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(tiles[row][col].getPiece() + " ");
            }
            System.out.print("\n");
        }
    }

    public void gameOver() {
        //TODO GameBoard.gameOver
        throw new UnsupportedOperationException();
    }

    public GridPane getGrid() {
        return grid;
    }

    public String getUsername() {
        return username;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
