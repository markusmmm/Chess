import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import management.*;
import pieces.ChessPiece;
import pieces.IChessPiece;
import resources.Alliance;
import resources.Move;
import resources.MoveNode;
import resources.Vector2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class GameBoard {
    private final int SIZE = 8;
    private final Board board;
    private final ChessComputer computer;

    private GridPane grid;
    private BorderPane container;
    private ListView<MoveNode> moveLog;
    private ListView<ChessPiece> capturedPieces;
    private Text informationField;

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
        this.container = new BorderPane();
        this.moveLog = new ListView<>();
        this.capturedPieces = new ListView<>();
        this.informationField = new Text();

        if (difficulty == 1) computer = new ChessComputerEasy(board);
        else if (difficulty == 2) computer = new ChessComputerMedium(board);
        else if (difficulty == 3) computer = new ChessComputerHard(board);
        else computer = null;
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
                    tileClick(e, tile);
                });

                tiles[row][col] = tile;
                squares[row][col] = rect;
                grid.add(rect, col, row);
                grid.add(tile, col, row);
                rect.widthProperty().bind((grid.widthProperty().divide(SIZE)));
                rect.heightProperty().bind(grid.heightProperty().divide(SIZE));
                tile.widthProperty().bind(grid.widthProperty().divide(SIZE));
                tile.heightProperty().bind(grid.heightProperty().divide(SIZE));
            }
        }

        int rightColumnSize = 200;

        VBox right = new VBox();
        right.setSpacing(0);
        right.setId("rightColumn");

        Label labelMoveLog = new Label();
        labelMoveLog.setPrefWidth(rightColumnSize);
        labelMoveLog.setText("Movelog:");
        labelMoveLog.setId("rightColumnTitle");

        moveLog.setPrefWidth(rightColumnSize);
        moveLog.setPrefHeight(200);
        moveLog.setId("moveLog");

        Label labelCapturedPieces = new Label();
        labelCapturedPieces.setPrefWidth(rightColumnSize);
        labelCapturedPieces.setText("Captured pieces:");
        labelCapturedPieces.setId("rightColumnTitle");

        capturedPieces.setPrefWidth(rightColumnSize);
        capturedPieces.setPrefHeight(200);
        capturedPieces.setId("moveLog");

        right.getChildren().addAll(labelMoveLog, moveLog, labelCapturedPieces, capturedPieces);

        VBox informationFieldContainer = new VBox();
        informationFieldContainer.setAlignment(Pos.CENTER);
        informationFieldContainer.getChildren().add(informationField);
        informationFieldContainer.setId("informationFieldContainer");

        container.setCenter(grid);
        container.setRight(right);
        container.setBottom(informationFieldContainer);

        drawBoard();
    }

    private void attemptMove(Tile firstTile, Vector2 pos) {
        IChessPiece temp = board.getPiece(firstTile.getPos());
        System.out.println("Before: " + temp.position());

        if (board.movePiece(firstTile.getPos(), pos)) {
            if (computer != null) {
                Move move = computer.getMove();
                board.movePiece(move);
                int row = move.start.getY();
                int col = move.start.getX();

                tiles[row][col].setFill(Color.TRANSPARENT);
            }

            firstTile.setFill(Color.TRANSPARENT);
            drawBoard();
            updateMoveLog();
            System.out.println("Moving " + board.getPiece(firstTile.getPos()) + " from " + firstTile.getPos() + " to " + pos);
        } else {
            drawBoard();
        }

        System.out.println("After:" + temp.position());
    }

    private boolean tileClick(MouseEvent e, Tile tile) {
        Alliance alliance = board.getActivePlayer();

        Vector2 pos = tile.getPos();

        IChessPiece piece = board.getPiece(pos);

        if (firstClick && firstTile.getPos() != pos) {
            IChessPiece firstPiece = board.getPiece(firstTile.getPos());

            System.out.println(firstClick + " " + firstPiece);

            attemptMove(firstTile, pos);

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
            System.out.println(piece);

            firstClick = true;
            firstTile = tile;

            Set<Vector2> list = board.getPiece(tile.getPos()).getPossibleDestinations();
            for (Vector2 possibleDestination : list) {
                highlightSquare(possibleDestination);
            }

            return true;
        }
        return false;
    }

    private void updateMoveLog() {
        Stack<MoveNode> gameLog = board.getGameLog();
        Collections.reverse(gameLog);

        HashSet<ChessPiece> inactivePieces = board.getInactivePieces();

        ObservableList<MoveNode> data = FXCollections.observableArrayList(gameLog);
        moveLog.setItems(data);
        ObservableList<ChessPiece> data2 = FXCollections.observableArrayList(board.getInactivePieces());
        capturedPieces.setItems(data2);
    }

    private void highlightSquare(Vector2 pos) {
        if (board.getPiece(pos) != null) {
            squares[pos.getY()][pos.getX()].setFill(Color.RED);
        } else {
            squares[pos.getY()][pos.getX()].setFill(Color.YELLOW);
        }
    }

    public void drawBoard() {
        for (Vector2 pos : board.pieces.keySet()) {
            int col = pos.getX();
            int row = pos.getY();
            tiles[row][col].drawPiece();
        }
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    squares[row][col].setFill(Color.LIGHTGRAY);
                } else {
                    squares[row][col].setFill(Color.DARKGRAY);
                }
            }
        }
        informationField.setText("It's " + board.getActivePlayer().toString() + " player's turn.");
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

    public BorderPane getContainer() {
        return container;
    }

    public String getUsername() {
        return username;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
