package main;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import pieces.IChessPiece;
import pieces.King;
import resources.MediaHelper;
import resources.*;
import resources.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static main.GUI.*;

public class GameController {
    private HostServices hostServices;

    MediaHelper media = new MediaHelper();
    private final int SIZE = 8;
    private Board board;
    private ChessComputer computer;
    private ChessComputer whiteHelper, blackHelper;

    private Main main;
    private Stage stage;
    private BorderPane root;
    private GridPane grid;
    private BorderPane container;
    private ListView<MoveNode> moveLog;
    private ListView<PieceNode> capturedPieces;
    private Text gameStatus;
    private DatabaseController database;

    private Tile[][] tiles;
    private Rectangle[][] squares;
    private String user1, user2;
    private BoardMode boardMode;

    private boolean firstClick;
    private Tile firstTile;
    private Player player1, player2;

    public GameController(String user1, String user2, int difficulty, BoardMode boardMode, Main main, Stage stage, BorderPane root, HostServices hostServices) {
        board = new Board(SIZE, difficulty,false, boardMode);

        this.user1 = user1;
        this.user2 = user2;
        this.main = main;
        this.stage = stage;
        this.boardMode = boardMode;
        this.root = root;
        this.hostServices = hostServices;

        grid = new GridPane();
        tiles = new Tile[SIZE][SIZE];
        squares = new Rectangle[SIZE][SIZE];
        firstClick = false;
        firstTile = null;
        container = new BorderPane();
        moveLog = new ListView<>();
        capturedPieces = new ListView<>();
        gameStatus = new Text();
        database = new DatabaseController();

        setComputer();

        player1 = new Player(user1, Alliance.WHITE);
        player2 = new Player(user2, Alliance.BLACK);

        Console.printSuccess("Game setup (Difficulty " + difficulty + ")");
    }

    /**
     * Creates a new chess computer, based on the given difficulty
     */
    private void setComputer() {
        int difficulty = board.difficulty();
        if (difficulty == 1) computer = new ChessComputerEasy(board);
        else if (difficulty == 2) computer = new ChessComputerMedium(board);
        else if (difficulty == 3) computer = new ChessComputerHard(board);
        else computer = null;

        if(computer != null)
            Console.printSuccess("Computer set for difficulty " + difficulty);

        whiteHelper = new ChessComputerHard(board, Alliance.WHITE);
        blackHelper = new ChessComputerHard(board, Alliance.BLACK);
    }

    /**
     * Setups the necessary tiles and structures needed to create a board
     */
    public void createBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle rect = new Rectangle();
                Vector2 pos = new Vector2(col, row);
                Tile tile;

                tile = new Tile(pos, board);
                rect.setFill((row + col) % 2 == 0 ? Color.LIGHTGRAY : Color.DARKGRAY);

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

        VBox right = styleNode(new VBox(), "spacing:0", "rightColumn");

        Label labelMoveLog = styleNode(new Label("Movelog: "), "prefWidth:" + rightColumnSize + ";", "rightColumnTitle");
        Label labelCapturedPieces = styleNode(new Label("Captured pieces: "), "prefWidth:" + rightColumnSize + ";", "rightColumnTitle");

        moveLog = styleNode(moveLog, "prefWidth:" + rightColumnSize + ";prefHeight:200;", "moveLog");
        capturedPieces = styleNode(capturedPieces, "prefWidth: " + rightColumnSize + ";prefHeight:200;", "moveLog");

        Button buttonHint = createButton("Hint", e -> {
            Move move = getHint(board.getActivePlayer());

            squares[move.start.getY()][move.start.getX()].setFill(Color.CYAN);
            squares[move.end.getY()][move.end.getX()].setFill(Color.LIMEGREEN);
        });

        right.getChildren().addAll(labelMoveLog, moveLog, labelCapturedPieces, capturedPieces, buttonHint);

        VBox statusFieldContainer = styleNode(new VBox(), "alignment:CENTER;", "informationFieldContainer");
        statusFieldContainer.getChildren().add(gameStatus);

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

        MenuItem menuItemExit = createMenuItem("Main Menu", e -> {
            main.mainMenu(player1.getUsername(), stage);
        });
        MenuItem menuItemReset = createMenuItem("Reset Game", e -> {
            GameController newGameController = new GameController(player1.getUsername(), player2.getUsername(), board.difficulty(), boardMode, main, stage, root, hostServices);
            newGameController.createBoard();
            root.setCenter(newGameController.getContainer());
        });
        MenuItem menuItemLoad = createMenuItem("Load Game", e -> performLoad());
        MenuItem menuItemSave = createMenuItem("Save Game", e -> performSave());
        MenuItem menuItemUndo = createMenuItem("Undo", e -> performUndo());
        MenuItem menuItemQuit = createMenuItem("Quit", e -> main.onQuit());

        menuFile.getItems().addAll(menuItemExit, menuItemReset, menuItemLoad, menuItemSave, menuItemUndo, menuItemQuit);
        MenuItem menuItemAbout = new MenuItem("User Manual");
        menuItemAbout.setOnAction(e -> hostServices.showDocument(Main.USER_MANUAL_URL));
        menuHelp.getItems().add(menuItemAbout);

        menuBar.getMenus().addAll(menuFile, menuHelp);
        return menuBar;
    }

    public void performLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Chess Game File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Chess Game File", "*" + Main.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(Main.SAVES_DIR);
        File selectedFile = fileChooser.showOpenDialog(stage);

        if(selectedFile != null) {
            try {
                board = new Board(selectedFile);
                createBoard();
                setComputer();
                updateLogs();
            } catch (FileNotFoundException e1) {
                Console.printError("Save file " + selectedFile.getName() + " does not exist");
                e1.printStackTrace();
            }
        }
    }
    private void performSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Chess Game File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Chess Game File", "*" + Main.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(Main.SAVES_DIR);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null)
            board.saveBoard(file);
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


        //resources.Console.println("Before: " + temp.position());

        boolean moveResult = board.movePiece(firstTile.getPos(), pos);
       // resources.Console.println("Outer move result: " + moveResult);
        if (moveResult) {
            Console.println("Has computer: " + (computer != null) + ", difficulty: " + board.difficulty());
            if (computer != null) {
                drawBoard();

                Move move = computer.getMove();
                Console.println("Computer attempting move " + move);
                board.movePiece(move);
                int row = move.start.getY();
                int col = move.start.getX();

                //tiles[row][col].setFill(Color.TRANSPARENT);
            }

            //firstTile.setFill(Color.TRANSPARENT);
            /*resources.Console.println("Moving " + board.getPiece(firstTile.getPos()) +
                    " from " + firstTile.getPos() + " to " + pos);*/
        } else {
            media.playSound("denied.mp3");
        }

        drawBoard();
        updateLogs();

        //resources.Console.println("After:" + temp.position());
    }

    public Move getHint(Alliance alliance) {
        if(alliance == Alliance.BLACK)
            return blackHelper.getMove();
        else if(alliance == Alliance.WHITE)
            return whiteHelper.getMove();

        return null;
    }

    private void performUndo() {
        int backStep = computer == null ? 1 : 2;
        int i = board.moveI() - backStep;

        if (i >= 0) {
            File logFile = new File(Main.LOGS_DIR, "log" + i + Main.SAVE_EXTENSION);
            try {
                board = new Board(logFile);
                createBoard();
                setComputer();
                updateLogs();
            } catch (FileNotFoundException e1) {
                Console.printError("No log exists for turn " + i);
                e1.printStackTrace();
            }
        }
    }

    public GameController(){

    }
    public String pawnPromotion() {


        ArrayList<String> choices = new ArrayList<>();
        choices.add("QUEEN");
        choices.add("BISHOP");
        choices.add("KNIGHT");
        choices.add("ROOK");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("QUEEN", choices);


        dialog.setHeaderText("Promote your pawn");
        dialog.setContentText("Choose your piece:");

        Optional<String> result = dialog.showAndWait();
        return result.map(String::toLowerCase).orElse("queen");
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
        ObservableList<PieceNode> observableInactivePieces =
                FXCollections.observableArrayList(board.getCapturedPieces());
        moveLog.setItems(observableGameLog);
        capturedPieces.setItems(observableInactivePieces);
    }

    private void highlightSquares(Vector2 pos) {
        IChessPiece piece = board.getPiece(pos);
        if(piece == null) return;

        Console.printNotice("Highlighting " + pos);
        Set<Vector2> list = piece.getPossibleActions();
        for (Vector2 possibleDestination : list) {
            Color squareColor = board.getPiece(possibleDestination) == null ? Color.YELLOW : Color.RED;
            squares[possibleDestination.getY()][possibleDestination.getX()].setFill(squareColor);
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
                    squares[row][col].setFill(Color.web("#E0E0E0"));
                } else {
                    squares[row][col].setFill(Color.web("#424242"));
                }
            }
        }
        String player;
        if (board.getActivePlayer() == Alliance.WHITE) player = player1.getUsername();
        else player = player2.getUsername();
        gameStatus.setText("It's " + player + " turn.");
    }

    public void printTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(tiles[col][row].getPiece() + " ");
            }
            System.out.print("\n");
        }
    }
    
    public boolean gameOver() {
        King whiteKing = board.getKing(Alliance.WHITE),
                blackKing = board.getKing(Alliance.BLACK);

        if(whiteKing == null || blackKing == null) {
            Console.printWarning("Non-conventional game setup (one or more kings missing). Game-over check not supported");
            return false;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if(whiteKing.checkmate()) {
            Console.println("Game over\nBlack player won!");
            alert.setContentText(player2.getUsername() + " won!");
        } else if(blackKing.checkmate()) {
            Console.println("Game over\nWhite player won!");
            alert.setContentText(player1.getUsername() + " won!");
        } else if(whiteKing.stalemate() || blackKing.stalemate()) {
            Console.println("Game Over\nRemiss");
            alert.setContentText("Remiss!");
        } else {
            return false;
        }

        if (board.difficulty() == 0) {
            if (blackKing.checkmate()) {
                database.updateScore(player1.getUsername(), (player1.getScore() + 3));
            } else if (whiteKing.checkmate()) {
                database.updateScore(player2.getUsername(), (player2.getScore() + 3));
            } else if(whiteKing.stalemate() || blackKing.stalemate()) {
                database.updateScore(player1.getUsername(), (player1.getScore() + 1));
                database.updateScore(player2.getUsername(), (player2.getScore() + 1));
            }
        } else {
            if (board.difficulty() == 1) {
                if (blackKing.checkmate()) {
                    database.updateScore(player1.getUsername(), (player1.getScore() + 3));
                }
            } else if (board.difficulty() == 2) {
                if (blackKing.checkmate()) {
                    database.updateScore(player1.getUsername(), (player1.getScore() + 6));
                }
            } else if (board.difficulty() == 3) {
                if (blackKing.checkmate()) {
                    database.updateScore(player1.getUsername(), (player1.getScore() + 9));
                }
            }
        }

        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        MediaHelper media = new MediaHelper();
        media.playSound("game_over.mp3");
        alert.showAndWait();
        main.mainMenu(player1.getUsername(), stage);
        return true;
    }

    public BorderPane getContainer() {
        return container;
    }
}
