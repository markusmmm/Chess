package main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import management.DatabaseController;
import resources.MediaHelper;
import resources.BoardMode;

import java.io.File;


public class Main extends Application {

    static final int WIDTH = 720;
    static final int HEIGHT = 500;
    MediaHelper media = new MediaHelper();
    private Stage stage;
    private BorderPane root;
    private DatabaseController database = new DatabaseController();

    public static final File savesDir = new File(System.getProperty("user.home"), "GitGud/");
    public static final File logsDir = new File(savesDir, ".logs/");

    public static final String DATA_SEPARATOR = "====";

    public void start(Stage primaryStage) throws Exception {
        directorySetup();

        stage = primaryStage;
        Scene scene = new Scene(loginWindow());
        scene.getStylesheets().add("stylesheet.css");
        stage.setScene(scene);
        stage.setTitle("Chess");
        stage.setResizable(false);
        stage.show();
    }

    private void directorySetup() {
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }
        if(!logsDir.exists())
            logsDir.mkdirs();
    }

    /**
     * Generates the login window
     *
     * @return login window
     */
    private Parent loginWindow() {
        //Label labelTitle = new Label("CHESS");
        //labelTitle.setUnderline(true);
        //labelTitle.setId("title");

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

    private void handleLogin(String username, Text errorField) {
        if (username == null || username.trim().isEmpty())
            errorField.setText("Please enter a non-empty username.");
        else {
            if (database.userExists(username)) {
                // database.updateScore(username, 9999);
            } else {
                database.addUser(username);
            }
            mainMenu(username, stage);
        }
    }

    /**
     * Generates the main menu, and set it to the scene
     *
     * @param username
     * @return mainMenu
     */
    public void mainMenu(String username, Stage stage) {
        root = new BorderPane();

        Label labelWelcome = new Label("Welcome, " + username +
                "!\nYour score: " + database.getScore(username));
        labelWelcome.setPrefWidth(WIDTH);
        labelWelcome.setMinHeight((HEIGHT / 8) * 2);
        labelWelcome.setAlignment(Pos.CENTER);
        labelWelcome.setId("title");
        labelWelcome.setTextAlignment(TextAlignment.CENTER);

        Button buttonPlayVersus = new Button();
        buttonPlayVersus.setText("PLAY: VERSUS");

        Button buttonPlayEasy = new Button();
        buttonPlayEasy.setText("PLAY: EASY");

        Button buttonPlayMedium = new Button();
        buttonPlayMedium.setText("PLAY: MEDIUM");

        Button randomBoardPlay = new Button();
        randomBoardPlay.setText("PLAY: RANDOM BOARD");

        Button buttonPlayHard = new Button();
        buttonPlayHard.setText("PLAY: HARD");
        buttonPlayHard.setVisible(false);

        Button buttonHighScore = new Button();
        buttonHighScore.setText("HIGHSCORE");

        Button buttonQuit = new Button();
        buttonQuit.setText("QUIT");

        buttonPlayVersus.setOnAction(e -> createChessGame(username, 0, BoardMode.DEFAULT, root));
        buttonPlayEasy.setOnAction(e -> createChessGame(username, 1, BoardMode.DEFAULT, root));
        randomBoardPlay.setOnAction(e -> createChessGame(username, 1, BoardMode.RANDOM, root));
        buttonPlayMedium.setOnAction(e -> createChessGame(username, 2, BoardMode.DEFAULT, root));
        buttonPlayHard.setOnAction(e -> createChessGame(username, 3, BoardMode.DEFAULT, root));
       // media.playSound("welcome.mp3");
        media.playSound("welcome.mp3");
        buttonQuit.setOnAction(e -> onQuit());

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BASELINE_CENTER);
        buttonContainer.getChildren().addAll(buttonPlayVersus, buttonPlayEasy, buttonPlayMedium, randomBoardPlay, buttonHighScore, buttonQuit);

        VBox mainContent = new VBox(0);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPrefSize(WIDTH, HEIGHT);
        mainContent.getChildren().addAll(labelWelcome, buttonContainer);

        root.setTop(generateMenuBar());
        root.setCenter(mainContent);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("stylesheet.css");
        stage.setScene(scene);
    }

    /**
     * creates a board with the choosen AI-difficulty
     *
     * @return chessGame
     */
    private void createChessGame(String username, int difficulty, BoardMode boardMode, BorderPane root) {
        GameBoard gameBoard = new GameBoard(username, difficulty, boardMode, this, stage, root);
        gameBoard.createBoard();
        root.setCenter(gameBoard.getContainer());
        root.setTop(gameBoard.generateGameMenuBar());


        media.playSound("startup.mp3");
        //return gameBoard.getContainer();
    }

    /**
     * generates and return menubar
     *
     * @return menubar
     */
    public MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuHelp = new Menu("Help");

        MenuItem menuItemQuit = new MenuItem("Quit");
        menuItemQuit.setOnAction(e -> onQuit());
        menuFile.getItems().add(menuItemQuit);

        MenuItem menuItemAbout = new MenuItem("About");
        menuHelp.getItems().add(menuItemAbout);

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

    public static void main(String[] args) {
        launch(args);
    }

}
