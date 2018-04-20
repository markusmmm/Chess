package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.StageStyle;
import management.DatabaseController;
import management.HighscoreController;
import org.bson.Document;
import resources.BoardMode;
import resources.Console;
import resources.Highscore;
import resources.MediaHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;



public class Main extends Application {

    static final int WIDTH = 720;
    static final int HEIGHT = 500;
    MediaHelper media = new MediaHelper();
    private Stage stage;
    private BorderPane root = new BorderPane();
    private MenuBar menuBar = generateMenuBar();
    private DatabaseController database = new DatabaseController();

    public static final File SAVES_DIR = new File(System.getProperty("user.home"), "GitGud/");
    public static final File LOGS_DIR = new File(SAVES_DIR, ".logs/");
    public static final File CORE_DIR = new File("core/");
    public static final File TESTS_DIR = new File("tests/");

    public static final String DATA_SEPARATOR = "====";
    public static final String SAVE_EXTENSION = ".txt";
    public static final String TEST_EXTENSION = ".txt";

    public void start(Stage primaryStage) throws Exception {
        directorySetup();
        root.setCenter(loginWindow());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("stylesheet.css");
        stage = primaryStage;
        stage.setScene(scene);
        stage.setTitle("Chess");
        stage.setResizable(false);
        stage.show();
    }

    private void directorySetup() {
        if (!SAVES_DIR.exists()) {
            SAVES_DIR.mkdirs();
        }
        if(!LOGS_DIR.exists())
            LOGS_DIR.mkdirs();

        if(CORE_DIR.exists()) {
            File[] coreFiles = CORE_DIR.listFiles();
            if(coreFiles == null) return;

            for(File coreFile : coreFiles) {
                String fileName = coreFile.getName();

                File destFile = new File(SAVES_DIR, fileName);
                if(!destFile.exists()) {
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

                    while(reader.hasNextLine()) {
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
        // root = new BorderPane();
        // menuBar = generateMenuBar();

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
        // buttonPlayHard.setVisible(false);

        Button buttonHighScore = new Button();
        buttonHighScore.setText("HIGHSCORE");

        Button buttonQuit = new Button();
        buttonQuit.setText("QUIT");

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
        buttonPlayEasy.setOnAction(e -> createChessGame(username, "AI: Easy", 1, BoardMode.DEFAULT, root));
        buttonPlayMedium.setOnAction(e -> createChessGame(username, "AI: Medium", 2, BoardMode.DEFAULT, root));
        buttonPlayHard.setOnAction(e -> createChessGame(username, "AI: Hard", 3, BoardMode.DEFAULT, root));
        randomBoardPlay.setOnAction(e -> createChessGame(username, "AI: Easy", 1, BoardMode.RANDOM, root));
        buttonHighScore.setOnAction(e -> highscore(username, stage));
        
        media.playSound("welcome.mp3");
        buttonQuit.setOnAction(e -> onQuit());

        VBox buttonContainer = new VBox(5);
        buttonContainer.setAlignment(Pos.BASELINE_CENTER);
        buttonContainer.getChildren().addAll(buttonPlayVersus, buttonPlayEasy, buttonPlayMedium, buttonPlayHard, randomBoardPlay, buttonHighScore, buttonQuit);

        VBox mainContent = new VBox(0);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPrefSize(WIDTH, HEIGHT);
        mainContent.getChildren().addAll(labelWelcome, buttonContainer);

        root.setTop(menuBar);
        root.setCenter(mainContent);
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
        GameBoard gameBoard = new GameBoard(player1, player2, difficulty, boardMode, this, stage, root);
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
    private MenuBar generateMenuBar() {
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
