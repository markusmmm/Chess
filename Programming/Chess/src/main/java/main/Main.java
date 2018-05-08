package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import management.DatabaseController;
import management.HighscoreController;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import resources.BoardMode;
import resources.Console;
import resources.Highscore;
import resources.MediaHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main extends Application {

    public static final File SAVES_DIR = new File(System.getProperty("user.home"), "GitGud/");
    public static final File LOGS_DIR = new File(SAVES_DIR, ".logs/");
    public static final File ONLINE_GAME_DIR = new File(SAVES_DIR, ".online/");
    public static final File CORE_DIR = new File("core/");
    public static final File TESTS_DIR = new File("tests/");
    public static final String DATA_SEPARATOR = "====";
    public static final String SAVE_EXTENSION = ".txt";
    public static final String TEST_EXTENSION = ".txt";
    public static final String USER_MANUAL_URL = "https://gitlab.uib.no/inf112-v2018/gruppe-3/blob/c9df10dba1e74977d1eb417fa3cf17cc54f19f0d/Documentation/User%20manual/User%20Manual.pdf";
    static final int WIDTH = 720;
    static final int HEIGHT = 500;
    MediaHelper media = new MediaHelper();
    MediaPlayer mp = media.playSound("chess_theme.mp3");
    private int mCounter = 0;
    private Stage stage;
    private BorderPane root = new BorderPane();
    private DatabaseController database = new DatabaseController();
    private MenuBar menuBar = generateMenuBar();
    private ListView<String> listView = new ListView<>();
    private List<Document> activeGameData;
    private Timer thread;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        directorySetup();
        root.setCenter(loginWindow());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("stylesheet.css");
        stage = primaryStage;
        stage.setScene(scene);
        stage.setTitle("Chess");
        stage.setResizable(false);
        stage.setOnHidden(e -> onQuit());
        stage.show();

        MediaHelper media = new MediaHelper();
        media.playSound("chess_theme.mp3");
    }

    /**
     * Setups the necessary directories for the Chess application
     */
    private void directorySetup() {
        if (!SAVES_DIR.exists()) {
            SAVES_DIR.mkdirs();
        }
        if (!LOGS_DIR.exists())
            LOGS_DIR.mkdirs();
        if (!ONLINE_GAME_DIR.exists())
            ONLINE_GAME_DIR.mkdirs();

        if (CORE_DIR.exists()) {
            File[] coreFiles = CORE_DIR.listFiles();
            if (coreFiles == null) return;

            for (File coreFile : coreFiles) {
                String fileName = coreFile.getName();

                File destFile = new File(SAVES_DIR, fileName);
                if (!destFile.exists()) {
                    try {
                        destFile.createNewFile();
                    } catch (IOException e) {
                        Console.printWarning("Couldn't copy core file " + fileName + " to saves directory");
                        //e.printStackTrace();
                        continue;
                    }
                }

                try {
                    FileWriter writer = new FileWriter(destFile);
                    writer.write(""); // Clear file
                    Scanner reader = new Scanner(coreFile);

                    while (reader.hasNextLine()) {
                        writer.append(reader.nextLine() + "\n");
                    }
                    writer.close();
                } catch (IOException e) {
                    Console.printWarning("Couldn't copy core file " + fileName + " to saves directory");
                    //e.printStackTrace();
                    continue;
                }
            }
            Console.printSuccess("All core files copied to save dir");
        } else {
            Console.printNotice("No core found");
        }

        Console.printSuccess("All directories setup successfully");
    }

    /**
     * Generates the login window
     *
     * @return login window
     */
    private Parent loginWindow() {
        Image bootImage = new Image("images/bootDecal.png", 500, 250, true, true);
        Rectangle bootDecal = new Rectangle(bootImage.getRequestedWidth(), bootImage.getRequestedHeight());
        bootDecal.setFill(new ImagePattern(bootImage));

        Label labelUsername = new Label("Username:");
        labelUsername.setPrefWidth(120);
        labelUsername.setAlignment(Pos.CENTER);

        TextField textUsername = new TextField();
        textUsername.setPrefWidth(240);
        textUsername.setAlignment(Pos.CENTER);

        Button loginButton = new Button();
        loginButton.setText("LOGIN");
        loginButton.setPrefWidth(120);

        Text errorField = new Text();
        errorField.setFill(Color.RED);

        textUsername.setOnAction(e -> handleLogin(textUsername.getText(), errorField));
        loginButton.setOnAction(e -> handleLogin(textUsername.getText(), errorField));

        VBox loginContainer = new VBox(10);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setPrefWidth(240);
        loginContainer.setMaxWidth(240);
        loginContainer.getChildren().addAll(labelUsername, textUsername);


        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(bootDecal, loginContainer, loginButton, errorField);
        container.setPrefSize(WIDTH, HEIGHT);
        return container;

    }

    /**
     * Handles the login and redirects to the main menu
     * @param username
     * @param errorField
     */
    private void handleLogin(String username, Text errorField) {
        if (username == null || username.trim().isEmpty())
            errorField.setText("Please enter a non-empty username.");
        else {
            if (database.userExists(username)) {

            } else {
                database.addUser(username);
            }
            mainMenu(username.toLowerCase(), stage);
        }
    }

    /**
     * Generates the main menu, and set it to the scene
     *
     * @param username
     * @return mainMenu
     */
    public void mainMenu(String username, Stage stage) {
        Label labelWelcome = new Label("Welcome, " + username +
                "!\nYour score: " + database.getScore(username));
        labelWelcome.setPrefWidth(WIDTH);
        labelWelcome.setMinHeight((HEIGHT / 8) * 2);
        labelWelcome.setAlignment(Pos.CENTER);
        labelWelcome.setId("title");
        labelWelcome.setTextAlignment(TextAlignment.CENTER);

        // Buttons for right container
        Button buttonCreateOnlineGame = new Button();
        buttonCreateOnlineGame.setText("CREATE ONLINE GAME");

        Button buttonPlayVersus = new Button();
        buttonPlayVersus.setText("PLAY: VERSUS");

        Button buttonPlayAi = new Button();
        buttonPlayAi.setText("PLAY: AI");

        Button buttonRandomBoardPlay = new Button();
        buttonRandomBoardPlay.setText("PLAY: RANDOM BOARD");

        Button buttonChessTutor = new Button();
        buttonChessTutor.setText("CHESS TUTORIAL");

        Button buttonHighScore = new Button();
        buttonHighScore.setText("HIGHSCORE");

        Button buttonQuit = new Button();
        buttonQuit.setText("QUIT");

        buttonCreateOnlineGame.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle("Enter the second players username");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Enter the second players username:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(player2 -> {
                if (!username.toLowerCase().equals(player2.toLowerCase())) {
                    if (!database.userExists(player2))
                        System.out.println("User does not exist.");
                    else
                        database.createGameInvite(username, player2);
                } else System.out.println("You can't play against yourself!");
            });
        });

        buttonPlayVersus.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle("Enter the second players username");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Enter the second players username:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(player2 -> {
                if (!username.toLowerCase().equals(player2.toLowerCase())) {
                    if (!database.userExists(player2))
                        database.addUser(player2);
                    createChessGame(username, player2, 0, BoardMode.DEFAULT, root);
                } else System.out.println("You can't play against yourself!");
            });
        });

        buttonPlayAi.setOnAction(e -> {
            List<String> choices = new ArrayList<>();
            choices.add("Easy");
            choices.add("Medium");
            choices.add("Hard");

            ChoiceDialog<String> dialog = new ChoiceDialog<>("Easy", choices);
            dialog.setTitle("Play against AI");
            dialog.setContentText("Choose difficulty:");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(choice -> {
                if (choice.equals("Easy"))
                    createChessGame(username, "AI: Easy", 1, BoardMode.DEFAULT, root);
                else if (choice.equals("Medium"))
                    createChessGame(username, "AI: Medium", 2, BoardMode.DEFAULT, root);
                else if (choice.equals("Hard"))
                    createChessGame(username, "AI: Hard", 3, BoardMode.DEFAULT, root);
            });

        });

        buttonRandomBoardPlay.setOnAction(e -> createChessGame(username, "AI: Easy", 1, BoardMode.RANDOM, root));
        buttonChessTutor.setOnAction(e -> createChessGame(username, "AI: Medium", 2, BoardMode.CHESSPUZZLES, root));
        buttonHighScore.setOnAction(e -> highscore(username, stage));
        buttonQuit.setOnAction(e -> onQuit());

        //mp.play();
        //mp.setCycleCount(-1);

        // Setups the right container of the buttons
        VBox rightContainer = new VBox(5);
        //rightContainer.setAlignment(Pos.BASELINE_CENTER);
        rightContainer.getChildren().addAll(buttonCreateOnlineGame, buttonPlayVersus, buttonPlayAi,
                buttonRandomBoardPlay, buttonChessTutor, buttonHighScore, buttonQuit);
        rightContainer.setPrefWidth(275);
        rightContainer.setPadding(new Insets(25, 15, 0, 15));


        // START of left container setup
        Label labelActiveGames = new Label("Active Games");
        labelActiveGames.setTextAlignment(TextAlignment.CENTER);
        labelActiveGames.setId("bold");

        // Retrieve games for the selected user, and updates the ListView with it
        activeGameData = database.getOnlineGames(username);
        updateGameList(username);

        Button buttonPlay = new Button("Play");
        buttonPlay.setOnAction(event -> {
            ObservableList<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();
            for (Object o : selectedIndices) {
                int i = (Integer) o;
                ObjectId id = (ObjectId) activeGameData.get(i).get("_id");
                String gameData = (String) activeGameData.get(i).get("gameData");
                String player1 = (String) activeGameData.get(i).get("player1");
                String player2 = (String) activeGameData.get(i).get("player2");
                try {
                    File gameFile = new File(ONLINE_GAME_DIR, username + "/" + id + ".txt");
                    FileUtils.writeStringToFile(gameFile, gameData, StandardCharsets.UTF_8);
                    GameBoard gameBoard = new GameBoard(player1, player2, 0, BoardMode.DEFAULT,
                            this, stage, root, username, id, getHostServices());
                    gameBoard.createBoard();
                    gameBoard.performLoad(gameFile);
                    root.setCenter(gameBoard.getContainer());
                    root.setTop(gameBoard.generateGameMenuBar());
                    thread.cancel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Button buttonForfeit = new Button("Forfeit");
        buttonForfeit.setOnAction(event -> {
            ObservableList<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();
            for (Object o : selectedIndices) {
                int i = (Integer) o;
                ObjectId id = (ObjectId) activeGameData.get(i).get("_id");
                database.forfeitGame(id);
            }
            updateGameList(username);
        });

        Button buttonRefresh = new Button("Refresh");
        buttonRefresh.setOnAction(event -> {
            updateGameList(username);
        });

        HBox leftButtonContainer = new HBox(buttonPlay, buttonForfeit, buttonRefresh);
        leftButtonContainer.setSpacing(15);

        VBox leftContainer = new VBox(labelActiveGames, listView, leftButtonContainer);
        leftContainer.setSpacing(15);
        leftContainer.setPadding(new Insets(15, 15, 15, 15));

        VBox container = new VBox(0);
        HBox contentContainer = new HBox(0);
        contentContainer.getChildren().addAll(leftContainer, rightContainer);
        container.setPrefSize(WIDTH, HEIGHT);
        container.getChildren().addAll(labelWelcome, contentContainer);


        root.setTop(menuBar);
        root.setCenter(container);

        thread = new Timer();
        thread.scheduleAtFixedRate(new InviteChecker(username, this), 0, 5 * 1000);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        updateGameList(username);
                    }
                });
            }
        }, 0, 5 * 1000);
    }

    public void updateGameList(String username) {
        activeGameData = database.getOnlineGames(username);
        ArrayList<String> activeGameList = new ArrayList<>();
        for (int i = 0; i < activeGameData.size(); i++) {
            String player1 = (String) activeGameData.get(i).get("player1");
            String player2 = (String) activeGameData.get(i).get("player2");
            activeGameList.add("Game " + (i + 1) + ": " + player1 + " vs " + player2);
        }
        ObservableList<String> observableList = FXCollections.observableArrayList(activeGameList);
        listView.setItems(observableList);
    }

    public void highscore(String username, Stage stage) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        Label labelWelcome = new Label("HIGHSCORES");
        labelWelcome.setAlignment(Pos.CENTER);
        labelWelcome.setTextAlignment(TextAlignment.CENTER);
        labelWelcome.setId("title");

        List<Document> documents = database.db.getCollection("users")
                .find().sort(new Document("score", -1)).into(new ArrayList<Document>());

        ArrayList<String> scoreList = new ArrayList<>();
        for (int i = 1; i <= documents.size(); i++) {
            scoreList.add(i + ". " + documents.get(i - 1).get("name")
                    + ": " + documents.get(i - 1).get("score"));
        }

        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList(scoreList);
        list.setItems(items);

        Button buttonBack = new Button();
        buttonBack.setText("← GO BACK");
        buttonBack.setOnAction(e -> mainMenu(username, stage));

        container.getChildren().addAll(labelWelcome, list, buttonBack);
        root.setCenter(container);
    }

    /**
     * TODO: implement a popup with a scoreboard containing scores from a HighscoreController
     */
    private void highScorePopup() {
        HighscoreController highscoreController = new HighscoreController();
        for (Highscore score : highscoreController.getHighscores()) {
            System.out.println(score);
        }
    }

    /**
     * creates a board with the choosen AI-difficulty
     *
     * @return chessGame
     */
    private void createChessGame(String player1, String player2, int difficulty, BoardMode boardMode, BorderPane root) {
        mp.stop();
        System.out.println(boardMode);
        GameBoard gameBoard = new GameBoard(player1, player2, difficulty, boardMode, this, stage, root, player1, getHostServices());

        gameBoard.createBoard();
        root.setCenter(gameBoard.getContainer());
        root.setTop(gameBoard.generateGameMenuBar());
        MediaPlayer nmp = media.playSound("startup.mp3");
        nmp.play();
        media.playSound("startup.mp3");
        thread.cancel();
        //return gameBoard.getContainer();
    }

    /**
     * generates and return menubar
     *
     * @return menubar
     */
    private MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuHelp = new Menu("Help");

        // File menu setup
        MenuItem menuItemQuit = new MenuItem("Quit");
        menuItemQuit.setOnAction(e -> onQuit());
        menuFile.getItems().add(menuItemQuit);

        // Help menu setup
        MenuItem menuItemManual = new MenuItem("User Manual");
        menuItemManual.setOnAction(e -> getHostServices().showDocument(USER_MANUAL_URL));

        MenuItem menuMute = new MenuItem("Mute");
        menuMute.setOnAction(e -> {
                    if (mCounter == 0) {
                        mp.setMute(true);
                        mCounter++;
                        System.out.println(mCounter);
                    } else if (mCounter == 1) {
                        mp.setMute(false);
                        mCounter--;
                    }
                }
        );
        menuHelp.getItems().addAll(menuItemManual, menuMute);

        menuBar.getMenus().addAll(menuFile, menuHelp);
        return menuBar;
    }

    /**
     * Exits the program
     */
    public void onQuit() {
        database.close();
        System.exit(0);
    }

}