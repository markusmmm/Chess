package main;

import javafx.application.HostServices;
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
import org.bson.types.ObjectId;
import pieces.IChessPiece;
import pieces.King;
import resources.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.apache.commons.io.FileUtils.readFileToString;

public class GameBoard {
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
    private ChessPuzzles chessPuzzles;
    private int numberOfPuzzlesCompleted;
    private int numberOfPuzzles;
    private String username;
    private boolean online;
    private ObjectId gameId;
    private Timer thread;

    public GameBoard(String user1, String user2, int difficulty, BoardMode boardMode, Main main,
                     Stage stage, BorderPane root, String username, ObjectId gameId, HostServices hostServices) {
        //this(user1, user2, difficulty, boardMode, main, stage, root, username);
        this(user1, user2, difficulty, boardMode, main, stage, root, username, hostServices);
        this.online = true;
        this.gameId = gameId;
        this.thread = new Timer();
        thread.scheduleAtFixedRate(new GameUpdater(this, gameId, username),
                0, 4 * 1000);
    }

    public GameBoard(String user1, String user2, int difficulty, BoardMode boardMode, Main main, Stage stage, BorderPane root, String username, HostServices hostServices) {
        this.hostServices = hostServices;

        Board boardVal = null;

        this.player1 = new Player(user1, Alliance.WHITE);
        this.player2 = new Player(user2, Alliance.BLACK);
        this.database = new DatabaseController();


        this.numberOfPuzzlesCompleted = database.getPuzzlesCompleted(user1);
        player1.setPuzzlesCompleted(numberOfPuzzlesCompleted);

        if(boardMode == BoardMode.CHESSPUZZLES){
            chessPuzzles = new ChessPuzzles();
            this.numberOfPuzzles = chessPuzzles.getSizeOfDirectory();

            String path;

            if(numberOfPuzzlesCompleted < numberOfPuzzles) {
                 path = chessPuzzles.getFile(numberOfPuzzlesCompleted);
            } else {
                completedAllPuzzles();
                path = chessPuzzles.getRandomFile();
            }


            Console.print("Attempting to open path " + path);

            try {
                boardVal = new Board(new File(path + Main.SAVE_EXTENSION), difficulty);
            } catch (FileNotFoundException e) {
                //e.printCaller();
                //System.err.println("Game setup failed! exiting...");
                //System.exit(1);

                Console.printWarning("Save file 'default' not found. Attempting legacy generation...");
                boardVal = new Board(SIZE, difficulty,false, boardMode);
            }
        }
        else if(boardMode == BoardMode.DEFAULT) {
            //this.board = new Board(SIZE, false, boardMode); TEMP TEST
            try {
                boardVal = new Board(new File("default" + Main.SAVE_EXTENSION), difficulty);
            } catch (FileNotFoundException e) {
                //e.printCaller();
                //System.err.println("Game setup failed! exiting...");
                //System.exit(1);

                Console.printWarning("Save file 'default' not found. Attempting legacy generation...");
                boardVal = new Board(SIZE, difficulty,false, boardMode);
            }
        } else if(boardMode == BoardMode.RANDOM) {
            boardVal = new Board(SIZE, difficulty, false, boardMode);
        }
        board = boardVal;

        this.main = main;
        this.stage = stage;
        this.boardMode = boardMode;
        this.root = root;
        this.grid = new GridPane();
        this.tiles = new Tile[SIZE][SIZE];
        this.squares = new Rectangle[SIZE][SIZE];
        this.user1 = user1;
        this.user2 = user2;
        this.firstClick = false;
        this.firstTile = null;
        this.container = new BorderPane();
        this.moveLog = new ListView<>();
        this.capturedPieces = new ListView<>();
        this.gameStatus = new Text();
        this.database = new DatabaseController();
        this.online = false;
        this.gameId = null;
        this.username = username;


        setComputer();



        Console.printSuccess("Game setup (Difficulty " + difficulty + ")");
    }

    /**
     * Creates a new chess computer, based on the given difficulty
     */
    private void setComputer() {
        int difficulty = board.difficulty();
        Console.printNotice("Setting computer for difficulty " + difficulty);
        if (difficulty == 1) computer = new ChessComputerEasy(board);
        else if (difficulty == 2) computer = new ChessComputerMedium(board);
        else if (difficulty == 3) computer = new ChessComputerHard(board);
        else computer = null;

        whiteHelper = new ChessComputerHard(board, Alliance.WHITE);
        blackHelper = new ChessComputerHard(board, Alliance.BLACK);
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

        Label labelClock = new Label();
        ChessClock clock = board.getClock();

        if (clock != null) {
            labelClock.setPrefWidth(rightColumnSize);
            labelClock.setText(clock.toString());
        }

        capturedPieces.setPrefWidth(rightColumnSize);
        capturedPieces.setPrefHeight(200);
        capturedPieces.setId("moveLog");

        if(boardMode != BoardMode.CHESSPUZZLES || !online) {

            Button buttonHint = new Button();
            buttonHint.setText("Hint");
            buttonHint.setOnAction(e -> {
                Move move = getHint(board.getActivePlayer());


                squares[move.start.getY()][move.start.getX()].setFill(Color.CYAN);
                squares[move.end.getY()][move.end.getX()].setFill(Color.LIMEGREEN);


            });

            right.getChildren().addAll(labelMoveLog, moveLog, labelCapturedPieces, capturedPieces, buttonHint);

        }

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
        MenuItem menuItemUndo = new MenuItem("Undo");
        MenuItem menuItemQuit = new MenuItem("Quit");

        menuItemExit.setOnAction(e -> goToMenu(username, stage));
        menuItemReset.setOnAction(e -> {
            GameBoard newGameBoard = new GameBoard(player1.getUsername(), player2.getUsername(), board.difficulty(), boardMode, main, stage, root, username, hostServices);
            newGameBoard.createBoard();
            root.setCenter(newGameBoard.getContainer());
        });
        menuItemLoad.setOnAction(e -> performLoad());
        menuItemUndo.setOnAction(e -> performUndo());
        menuItemSave.setOnAction(e -> performSave());
        menuItemQuit.setOnAction(e -> main.onQuit());

        if (online)
            menuFile.getItems().addAll(menuItemExit, menuItemQuit);
        else
            menuFile.getItems().addAll(menuItemExit, menuItemReset, menuItemLoad, menuItemSave, menuItemUndo, menuItemQuit);

        MenuItem menuItemManual = new MenuItem("User Manual");
        menuItemManual.setOnAction(e -> hostServices.showDocument(Main.USER_MANUAL_URL));
        menuHelp.getItems().add(menuItemManual);

        menuBar.getMenus().addAll(menuFile, menuHelp);
        return menuBar;
    }



    private void performLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Chess Game File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Chess Game File", "*" + Main.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(Main.SAVES_DIR);
        File selectedFile = fileChooser.showOpenDialog(stage);
        performLoad(selectedFile);
    }

    public void performLoad(File file) {
        if(file != null) {
            try {
                board = new Board(file);
                createBoard();
                setComputer();
                updateLogs();
            } catch (FileNotFoundException e1) {
                Console.printError("Save file " + file.getName() + " does not exist");
                e1.printStackTrace();
            }
        }
    }

    private void performSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Chess Game File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Chess Game File", "*" + Main.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(Main.SAVES_DIR);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null)
            board.saveBoard(file);
    }

    public void completedAllPuzzles(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("You have already completed all of the puzzles");
        alert.setContentText("Starting a random puzzle");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK){
            return;

        }
        return;

    }
    public void chessPuzzlePopup(){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("You have made the wrong move!");
        alert.setContentText("Do you wish to restart?");

        ButtonType buttonTypeOne = new ButtonType("Restart the game");
        ButtonType buttonTypeTwo = new ButtonType("Main menu");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            GameBoard newGameBoard = new GameBoard(player1.getUsername(), player2.getUsername(), board.difficulty(), boardMode, main, stage, root, player1.getUsername(), hostServices);

            newGameBoard.createBoard();
            root.setCenter(newGameBoard.getContainer());

        } else if (result.get() == buttonTypeTwo) {
                main.mainMenu(player1.getUsername(), stage);

        } else {
                main.mainMenu(player1.getUsername(), stage);
        }
    }

    public void puzzleCompleted(){

        database.updatePuzzlesCompleted(player1.getUsername(),(player1.getPuzzlesCompleted()+1));
        player1.setPuzzlesCompleted(player1.getPuzzlesCompleted()+1);

        int numberOfPuzzlesCompleted = player1.getPuzzlesCompleted();

        System.out.println(numberOfPuzzlesCompleted + "number of puzzles completed");


        if(numberOfPuzzles > numberOfPuzzlesCompleted) {
            String path = chessPuzzles.getFile(numberOfPuzzlesCompleted);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setHeaderText("You have completed the puzzle! " + (numberOfPuzzlesCompleted) + " / " + numberOfPuzzles+  " completed");
            alert.setContentText("Do you want to continue?");

            ButtonType buttonTypeOne = new ButtonType("Next puzzle");
            ButtonType buttonTypeTwo = new ButtonType("Restart this puzzle");
            ButtonType buttonTypeThree = new ButtonType("Main Menu");

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {

                try {
                    board = new Board(new File(path + Main.SAVE_EXTENSION),board.difficulty());

                    createBoard();
                    setComputer();
                    updateLogs();

                } catch (FileNotFoundException e1) {

                    main.mainMenu(player1.getUsername(), stage);

                }

            } else if (result.get() == buttonTypeTwo) {
                GameBoard newGameBoard = new GameBoard(player1.getUsername(), player2.getUsername(), board.difficulty(), boardMode, main, stage, root, player1.getUsername(), hostServices);

                newGameBoard.createBoard();
                root.setCenter(newGameBoard.getContainer());

            } else if (result.get() == buttonTypeThree){
                main.mainMenu(player1.getUsername(), stage);
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("CONGRATULATIONS!");
            alert.setContentText("You have completed all of the puzzles");

            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK){
                main.mainMenu(player1.getUsername(), stage);

            }

            main.mainMenu(player1.getUsername(), stage);
        }

    }

    /**
     * Attempts to move the piece at the given tile to the given position
     * @param firstTile Tile where the piece to move is placed
     * @param destination End position of attempted move
     */
    private void attemptMove(Tile firstTile, Vector2 destination) {
        IChessPiece temp = board.getPiece(firstTile.getPos());
        if(!board.ready()) {
            Console.println("Board not ready. Move failed");
            return;
        } else if(temp == null) {
            Console.println("No piece at " + destination + ". Move failed");
            return;
        }

        if(boardMode == BoardMode.CHESSPUZZLES){

            Move move = getHint(board.getActivePlayer());

            Vector2 correctMoveEnd = move.getEnd();
            Vector2 correctMoveStart = move.getStart();

            if(!(correctMoveEnd.equals(destination) && correctMoveStart.equals(firstTile.getPos()))){
                chessPuzzlePopup();
            }

        }

        boolean moveResult = board.movePiece(firstTile.getPos(), destination);
        if (moveResult) {
            Console.println("Has computer: " + (computer != null));
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
            media.playSound("denied.mp3").play();

        }

        drawBoard();
        updateLogs();

        //resources.Console.println("After:" + temp.position());
    }

    private void goToMenu(String username, Stage stage) {
        if (online)
            thread.cancel();
        main.mainMenu(username, stage);
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

    public GameBoard(){

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
        if (result.isPresent()) {
            String s = result.get().toLowerCase();

            return s;

        } else {
            return "queen";
        }
    }

    private boolean tileClick(MouseEvent e, Tile tile) {
        if (online)
            if (!isYourTurn())
                return false;

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

            if (online) {
                File gameFile = new File(System.getProperty("user.home"), "GitGud/.online/" + username + "/" + gameId + ".txt");
                board.saveBoard(gameFile);
                try {
                    String gameData = readFileToString(gameFile, StandardCharsets.UTF_8);
                    database.updateGame(gameId, gameData);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

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
        Set<Vector2> list = piece.getPossibleDestinations();
        for (Vector2 possibleDestination : list) {
            Color squareColor = board.getPiece(possibleDestination) == null ? Color.YELLOW : Color.RED;
            squares[possibleDestination.getY()][possibleDestination.getX()].setFill(squareColor);
        }
    }

    /**
     * draws the chessboard
     */
    public void drawBoard() {
        drawPieces();
        drawSquares();
        drawPlayerTurn();
    }

    /**
     * updates the GUI feed which tells who's turn it is
     */
    private void drawPlayerTurn() {
        String player;
        if (board.getActivePlayer() == Alliance.WHITE) player = player1.getUsername();
        else player = player2.getUsername();
        gameStatus.setText("It's " + player + " turn.");
    }

    /**
     * draws the pieces on the board
     */
    public void drawPieces() {
        for (Vector2 pos : board.clearDrawPieces()) {
            tiles[pos.getY()][pos.getX()].drawPiece();
        }
    }

    /**
     * draws the black and white squares on the chessboard
     */
    public void drawSquares() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    squares[row][col].setFill(Color.LIGHTGRAY);
                } else {
                    squares[row][col].setFill(Color.DARKGRAY);
                }
            }
        }

        String player;
        if (board.getActivePlayer() == Alliance.WHITE) player = player1.getUsername();
        else player = player2.getUsername();

        if(!(boardMode == BoardMode.CHESSPUZZLES)) {
            gameStatus.setText("It's " + player + " turn.");
        } else {
            gameStatus.setText("Playing as white. Get checkmate in 3 moves.");
        }

    }
    public void printTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(tiles[col][row].getPiece() + " ");
            }
            System.out.print("\n");
        }
    }

    public boolean isYourTurn() {
        Player player;
        if (board.getActivePlayer() == Alliance.WHITE)
            player = player1;
        else player = player2;
        if (player.getUsername().equals(username))
            return true;
        return false;
    }
    
    public boolean gameOver() {
        King whiteKing = board.getKing(Alliance.WHITE),
                blackKing = board.getKing(Alliance.BLACK);
        if(boardMode == BoardMode.CHESSPUZZLES){
            if(blackKing.checkmate()){
                puzzleCompleted();
                return true;
            }
        }

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
                if (online) database.gameOver(gameId, player1.getUsername());
            } else if (whiteKing.checkmate()) {
                database.updateScore(player2.getUsername(), (player2.getScore() + 3));
                if (online) database.gameOver(gameId, player2.getUsername());
            } else if(whiteKing.stalemate() || blackKing.stalemate()) {
                database.updateScore(player1.getUsername(), (player1.getScore() + 1));
                database.updateScore(player2.getUsername(), (player2.getScore() + 1));
                if (online) database.gameOver(gameId, "none");
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
        media.playSound("game_over.mp3").play();
        alert.showAndWait();
        goToMenu(username, stage);
        return true;
    }

    public BorderPane getContainer() {
        return container;
    }
}
