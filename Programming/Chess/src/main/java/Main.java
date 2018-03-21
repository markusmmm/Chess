import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import management.HighscoreController;
import resources.Highscore;

import java.util.List;

public class Main extends Application {

    Stage stage;
    static final int WIDTH = 550;
    static final int HEIGHT = 540;

    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Scene scene = new Scene(loginWindow());
        scene.getStylesheets().add("stylesheet.css");
        stage.setScene(scene);
        stage.setTitle("Chess");
        stage.show();
    }

    /**
     * Generates the login window
     *
     * @return login window
     */
    private Parent loginWindow() {
        Label labelTitle = new Label("CHESS");
        labelTitle.setUnderline(true);
        labelTitle.setId("title");

        Label labelUsername = new Label("Username:");
        labelUsername.setPrefWidth(120);
        labelUsername.setAlignment(Pos.CENTER);

        TextField textUsername = new TextField();
        textUsername.setPrefWidth(240);

        Button loginButton = new Button();
        loginButton.setText("LOGIN");
        loginButton.setPrefWidth(120);

        GridPane gridPane = new GridPane();
        gridPane.add(labelUsername, 1, 0);
        gridPane.add(textUsername, 2, 0);

        Text errorField = new Text();
        errorField.setFill(Color.RED);

        loginButton.setOnAction(e -> {
            String username = textUsername.getText();
            if (username == null || username.trim().isEmpty())
                errorField.setText("Please enter a non-empty username.");
            else {
                HighscoreController highscoreController = new HighscoreController();
                if (highscoreController.addUser(username)) {
                   System.out.println("added");
                } else System.out.println("not added");
                // System.out.println(highscoreController.getScore(username));
                // System.out.println("Eksisterer: " + highscoreController.userExists(username));
                // System.out.println(highscoreController.getScore("Magnus"));
                // new HighscoreController().addUser(username);
                Scene scene = new Scene(mainMenu(username));
                scene.getStylesheets().add("stylesheet.css");
                stage.setScene(scene);
            }
        });

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(labelTitle, gridPane, loginButton, errorField);
        root.setPrefSize(400, 250);
        return root;
    }

    /**
     * Generates the main menu
     *
     * @param username
     * @return mainMenu
     */
    private Parent mainMenu(String username) {

        BorderPane root = new BorderPane();

        Label labelWelcome = new Label("Welcome, " + username + "!");
        labelWelcome.setPrefWidth(WIDTH);
        labelWelcome.setMinHeight((HEIGHT / 8) * 2);
        labelWelcome.setAlignment(Pos.CENTER);
        labelWelcome.setId("title");

        Button buttonPlayVersus = new Button();
        buttonPlayVersus.setText("PLAY: VERSUS");

        Button buttonPlayEasy = new Button();
        buttonPlayEasy.setText("PLAY: EASY");

        Button buttonPlayMedium = new Button();
        buttonPlayMedium.setText("PLAY: MEDIUM");

        Button buttonPlayHard = new Button();
        buttonPlayHard.setText("PLAY: HARD");
        buttonPlayHard.setVisible(false);

        Button buttonHighScore = new Button();
        buttonHighScore.setText("HIGHSCORE");

        Button buttonQuit = new Button();
        buttonQuit.setText("QUIT");

        buttonPlayVersus.setOnAction(e -> root.setCenter(createChessGame(username, 0)));
        buttonPlayEasy.setOnAction(e -> root.setCenter(createChessGame(username, 1)));
        buttonPlayMedium.setOnAction(e -> root.setCenter(createChessGame(username, 2)));
        buttonPlayHard.setOnAction(e -> root.setCenter(createChessGame(username, 3)));
        buttonHighScore.setOnAction(e -> highScorePopup());
        buttonQuit.setOnAction(e -> onQuit());

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BASELINE_CENTER);
        buttonContainer.getChildren().addAll(buttonPlayVersus/*, buttonPlayEasy, buttonPlayMedium, buttonPlayHard*/, buttonHighScore, buttonQuit);

        VBox mainContent = new VBox(0);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPrefSize(WIDTH, HEIGHT);
        mainContent.getChildren().addAll(labelWelcome, buttonContainer);

        root.setTop(generateMenuBar());
        root.setCenter(mainContent);

        return root;
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
    private GridPane createChessGame(String username, int difficulty) {
        GameBoard gameBoard = new GameBoard(username, difficulty);
        gameBoard.createBoard();
        return gameBoard.getGrid();
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
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
