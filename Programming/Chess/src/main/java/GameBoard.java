import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import management.*;
import pieces.ChessPiece;
import pieces.IChessPiece;
import pieces.King;
import resources.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Set;
import java.util.Stack;

public class GameBoard {
    private final int SIZE = 8;
    private Board board;
    private final ChessComputer computer;

    private Stage stage;
    private BorderPane root;
    private GridPane grid;
    private BorderPane container;
    private ListView<MoveNode> moveLog;
    private ListView<ChessPiece> capturedPieces;
    private Text gameStatus;

    private Tile[][] tiles;
    private Rectangle[][] squares;
    private String username;
    private int difficulty;
    private BoardMode boardMode;

    private boolean firstClick;
    private Tile firstTile;

    private File savesDir = new File(System.getProperty("user.home"), "GitGud/");

    public GameBoard(String username, int difficulty, BoardMode boardMode, Stage stage, BorderPane root) {
        Board boardVal = null;

        if(boardMode == BoardMode.DEFAULT) {
            //this.board = new Board(SIZE, false, boardMode); TEMP TEST
            try {
                boardVal = new Board("default");
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                //System.err.println("Game setup failed! exiting...");
                //System.exit(1);

                Console.printWarning("Save file 'default' not found. Attempting legacy generation...");
                boardVal = new Board(SIZE, false, boardMode);
            }
        } else {
            boardVal = new Board(SIZE, false, boardMode);
        }
        board = boardVal;

        this.stage = stage;
        this.boardMode = boardMode;
        this.root = root;
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
        this.gameStatus = new Text();

        if (difficulty == 1) computer = new ChessComputerEasy(board);
        else if (difficulty == 2) computer = new ChessComputerMedium(board);
        else if (difficulty == 3) computer = new ChessComputerHard(board);
        else computer = null;

        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }

        Console.printSuccess("Game setup");
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

                tile.setOnMouseClicked(e -> tileClick(e, tile));

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

        VBox statusFieldContainer = new VBox();
        statusFieldContainer.setAlignment(Pos.CENTER);
        statusFieldContainer.getChildren().add(gameStatus);
        statusFieldContainer.setId("informationFieldContainer");

        container.setCenter(grid);
        container.setRight(right);
        container.setBottom(statusFieldContainer);

        root.setTop(generateGameMenuBar());
        root.setCenter(container);

        drawBoard();
    }

    public MenuBar generateGameMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuHelp = new Menu("Help");

        MenuItem menuItemExit = new MenuItem("Main Menu");
        MenuItem menuItemReset = new MenuItem("Reset Game");
        MenuItem menuItemLoad = new MenuItem("Load Game");
        MenuItem menuItemSave = new MenuItem("Save Game");
        MenuItem menuItemQuit = new MenuItem("Quit");

        menuItemExit.setOnAction(e -> {
            new Main().mainMenu(username, stage);
        });
        menuItemReset.setOnAction(e -> {
            GameBoard newGameBoard = new GameBoard(username, difficulty, boardMode, stage, root);
            newGameBoard.createBoard();
            root.setCenter(newGameBoard.getContainer());
        });
        menuItemLoad.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Chess Game File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Chess Game File", "*.txt"));
            fileChooser.setInitialDirectory(savesDir);
            File selectedFile = fileChooser.showOpenDialog(stage);
        });
        menuItemSave.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Chess Game File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Chess Game File", "*.txt"));
            fileChooser.setInitialDirectory(savesDir);
            File file = fileChooser.showSaveDialog(stage);
            if (file != null)
                board.saveFile(fileChooser.showSaveDialog(stage));
        });
        menuItemQuit.setOnAction(e -> System.exit(0));

        menuFile.getItems().addAll(menuItemExit, menuItemReset, menuItemLoad, menuItemSave, menuItemQuit);
        MenuItem menuItemAbout = new MenuItem("About");
        menuHelp.getItems().add(menuItemAbout);

        menuBar.getMenus().addAll(menuFile, menuHelp);
        return menuBar;
    }

    private void attemptMove(Tile firstTile, Vector2 pos) {
        IChessPiece temp = board.getPiece(firstTile.getPos());
        if(!board.ready()) {
            System.out.println("Board not ready. Move failed");
            return;
        } else if(temp == null) {
            System.out.println("No piece at " + pos + ". Move failed");
            return;
        }

        //System.out.println("Before: " + temp.position());

        boolean moveResult = board.movePiece(firstTile.getPos(), pos);
       // System.out.println("Outer move result: " + moveResult);
        if (moveResult) {
            if(board.getKing(Alliance.WHITE).checkmate())

               // System.out.println("Has computer: " + computer != null);
            if (computer != null) {
                Move move = computer.getMove();
               // System.out.println("Computer attempting move " + move);
                board.movePiece(move);
                int row = move.start.getY();
                int col = move.start.getX();

                //tiles[row][col].setFill(Color.TRANSPARENT);
            }

            //firstTile.setFill(Color.TRANSPARENT);
            drawBoard();
            updateLogs();
            /*System.out.println("Moving " + board.getPiece(firstTile.getPos()) +
                    " from " + firstTile.getPos() + " to " + pos);*/
        } else {
            drawBoard();
        }

        //System.out.println("After:" + temp.position());
    }

    private boolean tileClick(MouseEvent e, Tile tile) {
        Vector2 pos = tile.getPos();
        //if(!board.ready()) return false;

        Alliance alliance = board.getActivePlayer();
        IChessPiece piece = board.getPiece(pos);

        /*
         * checks if another tile has already been selected
         */
        if (firstClick && firstTile.getPos() != pos) {
            /*
             * checks if the newly clicked tile is another friendly piece,
             * if it is, change the highlighted squares to the new piece
             */
            if (piece != null) {
                if (piece.alliance() == alliance) {
                    firstTile = tile;
                    drawBoard();
                    highlightSquares(pos);
                    return true;
                }
            }
            /*
             * if not, attempt to move the pre-selected piece
             * to the new location
             */
            IChessPiece firstPiece = board.getPiece(firstTile.getPos());
            System.out.println(firstClick + " " + firstPiece);
            attemptMove(firstTile, pos);

            firstClick = false;
            firstTile = null;

            if(gameOver())
                return false;

        /*
         * checks if the tile clicked has a piece, and that the
         * piece has same alliance as the active player.
         * if true for both, store the tile for the next call to
         * tileClick.
         */
        } else {
            System.out.print(pos + ": ");
            if (piece == null) {
                System.out.println("No piece");
                return false;
            } else if (piece.alliance() != alliance) {
                System.out.println("Not your alliance");
                return false;
            }
            //System.out.println(piece);

            firstClick = true;
            firstTile = tile;
            highlightSquares(pos);

            return true;
        }
        return false;
    }

    private void updateLogs() {
        Stack<MoveNode> gameLog = board.getGameLog();
        Collections.reverse(gameLog);
        ObservableList<MoveNode> observableGameLog =
                FXCollections.observableArrayList(gameLog);
        ObservableList<ChessPiece> observableInactivePieces =
                FXCollections.observableArrayList(board.getCapturedPieces());
        moveLog.setItems(observableGameLog);
        capturedPieces.setItems(observableInactivePieces);
    }

    private void highlightSquares(Vector2 pos) {
        IChessPiece piece = board.getPiece(pos);
        if(piece == null) return;

        Set<Vector2> list = piece.getPossibleDestinations();
        for (Vector2 possibleDestination : list) {
            if (board.getPiece(possibleDestination) != null) {
                squares[possibleDestination.getY()][possibleDestination.getX()].setFill(Color.RED);
            } else {
                squares[possibleDestination.getY()][possibleDestination.getX()].setFill(Color.YELLOW);
            }
        }
    }

    public void drawBoard() {
        for (Vector2 pos : board.clearDrawPieces()) {
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
        gameStatus.setText("It's " + board.getActivePlayer().toString() + " player's turn.");
    }

    public void printTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(tiles[col][row].getPiece() + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * TODO: tell the user which player won the game, and update highscore
     */
    public boolean gameOver() {
        King whiteKing = board.getKing(Alliance.WHITE),
                blackKing = board.getKing(Alliance.BLACK);

        if(whiteKing.checkmate())
            System.out.println("Game over\nBlack player won!");
        else if(blackKing.checkmate())
            System.out.println("Game over\nWhite player won!");
        else if(whiteKing.stalemate() || blackKing.stalemate())
            System.out.println("Game Over\nRemiss");
        else {
            return false;
        }

        // new Main().mainMenu(username, stage);

        return true;
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
