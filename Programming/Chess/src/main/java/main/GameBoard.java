package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import management.*;
import pieces.*;
import resources.MediaHelper;
import resources.*;
import resources.Console;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class GameBoard {
    MediaHelper media = new MediaHelper();
    private final int SIZE = 8;
    private Board board;
    private final ChessComputer computer;

    private Main main;
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

    public GameBoard(String username, int difficulty, BoardMode boardMode, Main main, Stage stage, BorderPane root) {
        Board boardVal = null;

        if(boardMode == BoardMode.DEFAULT) {
            //this.board = new Board(SIZE, false, boardMode); TEMP TEST
            try {
                boardVal = new Board(new File("default.txt"));
            } catch (FileNotFoundException e) {
                //e.printCaller();
                //System.err.println("Game setup failed! exiting...");
                //System.exit(1);

                Console.printWarning("Save file 'default' not found. Attempting legacy generation...");
                boardVal = new Board(SIZE, false, boardMode);
            }
        } else if(boardMode == BoardMode.RANDOM) {
            boardVal = new Board(SIZE, false, boardMode);
        }
        board = boardVal;

        this.main = main;
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

        Console.printSuccess("Game setup (Difficulty " + difficulty + ")");
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

        MenuItem menuItemExit = new MenuItem("main.Main Menu");
        MenuItem menuItemReset = new MenuItem("Reset Game");
        MenuItem menuItemLoad = new MenuItem("Load Game");
        MenuItem menuItemSave = new MenuItem("Save Game");
        MenuItem menuItemUndo = new MenuItem("Undo");
        MenuItem menuItemQuit = new MenuItem("Quit");

        menuItemExit.setOnAction(e -> {
            main.mainMenu(username, stage);
        });
        menuItemReset.setOnAction(e -> {
            GameBoard newGameBoard = new GameBoard(username, difficulty, boardMode, main, stage, root);
            newGameBoard.createBoard();
            root.setCenter(newGameBoard.getContainer());
        });
        menuItemLoad.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Chess Game File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Chess Game File", "*.txt"));
            fileChooser.setInitialDirectory(Main.savesDir);
            File selectedFile = fileChooser.showOpenDialog(stage);

            if(selectedFile != null) {
                try {
                    board = new Board(selectedFile);
                    createBoard();
                    updateLogs();
                } catch (FileNotFoundException e1) {
                    Console.printError("Save file " + selectedFile.getName() + " does not exist");
                    e1.printStackTrace();
                }
            }
        });
        menuItemUndo.setOnAction(e -> {
            int backStep = computer == null ? 1 : 2;
            int i = board.moveI() - backStep;

            if(i >= 0) {
                File logFile = new File(Main.logsDir, "log" + i + ".txt");
                try {
                    board = new Board(logFile);
                    createBoard();
                    updateLogs();
                } catch (FileNotFoundException e1) {
                    Console.printError("No log exists for turn " + i);
                    e1.printStackTrace();
                }
            }
        });
        menuItemSave.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Chess Game File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Chess Game File", "*.txt"));
            fileChooser.setInitialDirectory(Main.savesDir);
            File file = fileChooser.showSaveDialog(stage);
            if (file != null)
                board.saveFile(file);
        });
        menuItemQuit.setOnAction(e -> System.exit(0));

        menuFile.getItems().addAll(menuItemExit, menuItemReset, menuItemLoad, menuItemSave, menuItemUndo, menuItemQuit);
        MenuItem menuItemAbout = new MenuItem("About");
        menuHelp.getItems().add(menuItemAbout);

        menuBar.getMenus().addAll(menuFile, menuHelp);
        return menuBar;
    }

    private void attemptMove(Tile firstTile, Vector2 pos) {
        IChessPiece temp = board.getPiece(firstTile.getPos());
        if(!board.ready()) {
            Console.println("Board not ready. Move failed");
            return;
        } else if(temp == null) {
            Console.println("No piece at " + pos + ". Move failed");
            return;
        }


        if(firstTile.getPiece() instanceof Pawn){
            if((board.pawnPromotion((Pawn)firstTile.getPiece(), pos)))
           {
               Alliance alliance = firstTile.getPiece().alliance();
               pawnPromotion(firstTile.getPos(), pos, alliance);

           }
       }
        //resources.Console.println("Before: " + temp.position());

            Boolean moveResult = board.movePiece(firstTile.getPos(), pos);


            // resources.Console.println("Outer move result: " + moveResult);
            if (moveResult) {
                Console.println("Has computer: " + (computer != null));
                if (computer != null) {
                    Move move = computer.getMove();
                    Console.println("Computer attempting move " + move);
                    board.movePiece(move);
                    int row = move.start.getY();
                    int col = move.start.getX();

                    //tiles[row][col].setFill(Color.TRANSPARENT);
                }

                //firstTile.setFill(Color.TRANSPARENT);
                drawBoard();
                updateLogs();
            /*resources.Console.println("Moving " + board.getPiece(firstTile.getPos()) +
                    " from " + firstTile.getPos() + " to " + pos);*/
            } else {
                media.playSound("denied.mp3");
                drawBoard();
            }

            //resources.Console.println("After:" + temp.position());

    }


    public void pawnPromotion(Vector2 piecePos, Vector2 end, Alliance alliance) {


        ArrayList<String> choices = new ArrayList<>();
        choices.add("QUEEN");
        choices.add("BISHOP");
        choices.add("KNIGHT");
        choices.add("ROOK");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("QUEEN", choices);


        dialog.setHeaderText("Promote your pawn");
        dialog.setContentText("Choose your piece:");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            board.removePiece(piecePos);
            board.advanceMove(true);


            String s = result.get().toLowerCase();

            switch (s.charAt(0)) {
                case 'q':
                    board.addPiece(end, Piece.QUEEN, alliance);
                    break;
                case 'b':
                    board.addPiece(end, Piece.BISHOP, alliance);
                    break;
                case 'k':
                    board.addPiece(end, Piece.KNIGHT, alliance);
                    break;
                case 'r':
                    board.addPiece(end, Piece.ROOK, alliance);
                    break;

            }
        }else {
            board.removePiece(piecePos);
            board.advanceMove(true);
            board.addPiece(end, Piece.QUEEN, alliance);
        }
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
            Console.println(firstClick + " " + firstPiece);
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
                Console.println("No piece");
                return false;
            } else if (piece.alliance() != alliance) {
                Console.println("Not your alliance");
                return false;
            }
            //resources.Console.println(piece);

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
            Console.println("Game over\nBlack player won!");
        else if(blackKing.checkmate())
            Console.println("Game over\nWhite player won!");
        else if(whiteKing.stalemate() || blackKing.stalemate())
            Console.println("Game Over\nRemiss");
        else {
            return false;
        }

        MediaHelper media = new MediaHelper();
        media.playSound("game_over.mp3");
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
