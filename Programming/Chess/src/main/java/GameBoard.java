import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import management.Board;
import pieces.IChessPiece;
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

    public GameBoard(String username, int difficulty) {
        this.board = new Board(SIZE, false);
        this.grid = new GridPane();
        this.tiles = new Tile[SIZE][SIZE];
        this.squares = new Rectangle[SIZE][SIZE];
        this.username = username;
        this.difficulty = difficulty;
    }

    /**
     * Setups the necessary tiles and structures to be able to create a board
     */
    public void createBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle rect = new Rectangle();
                Vector2 pos = new Vector2(row, col);
                Tile tile;

                if ((row + col) % 2 == 0) {
                    tile = new Tile(pos, board);
                    rect.setFill(Color.LIGHTGRAY);
                } else {
                    tile = new Tile(pos, board);
                    rect.setFill(Color.DARKGRAY);
                }

                tile.setOnMouseClicked(e -> {
                    System.out.println("row: " + tile.getPos().getX() +
                            ", col: " + tile.getPos().getY() + ", piece: " + tile.getPiece());
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

    public void drawBoard() {
        for (Vector2 pos : board.pieces.keySet()) {
            int row = pos.getX();
            int col = pos.getY();
            tiles[col][row].drawPiece();
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
