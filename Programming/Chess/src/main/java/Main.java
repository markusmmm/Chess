import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    Stage stage;

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
     * @return login window
     */
    private Parent loginWindow() {
        Label labelTitle = new Label("CHESS");
        labelTitle.setUnderline(true);
        labelTitle.setId("title");

        Label labelUsername = new Label("Username:");
        labelUsername.setPrefWidth(120);
        labelUsername.setAlignment(Pos.CENTER);
        //labelUsername.setFont(Font.font("Arial", 16));

        TextField textUsername = new TextField();
        textUsername.setPrefWidth(240);

        Button loginButton = new Button();
        loginButton.setText("LOGIN");
        //loginButton.setFont(Font.font("Arial", 16));
        loginButton.setPrefWidth(120);

        GridPane gridPane = new GridPane();
        gridPane.add(labelUsername, 1, 0);
        gridPane.add(textUsername, 2, 0);

        Text errorField = new Text();
        errorField.setFill(Color.RED);

        loginButton.setOnAction(e -> {
            if (textUsername.getText() == null || textUsername.getText().trim().isEmpty())
                errorField.setText("Please enter a non-empty username.");
            else {
                Scene scene = new Scene(mainMenu(textUsername.getText()));
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
     * @param username
     * @return mainMenu
     */
    private Parent mainMenu(String username) {
        final int WIDTH = 650;
        final int HEIGHT = 400;

        Label labelWelcome = new Label("Welcome, " + username + "!");
        labelWelcome.setPrefWidth(WIDTH);
        labelWelcome.setMinHeight((HEIGHT / 8)*2);
        labelWelcome.setAlignment(Pos.CENTER);
        labelWelcome.setId("title");

        Label labelPlayAGame = new Label("Play a game");
        labelPlayAGame.setPrefWidth(WIDTH / 2);
        labelPlayAGame.setMinHeight(HEIGHT / 8);
        labelPlayAGame.setAlignment(Pos.CENTER);

        /*Label labelHighscore = new Label("Highscore");
        labelHighscore.setFont(Font.font("Arial", 22));
        labelHighscore.setPrefWidth(WIDTH / 2);
        labelHighscore.setMinHeight(HEIGHT / 8);
        labelHighscore.setAlignment(Pos.CENTER);*/

        Button buttonPlayEasy = new Button();
        buttonPlayEasy.setText("PLAY: EASY");
        Button buttonPlayMedium = new Button();
        buttonPlayMedium.setText("PLAY: MEDIUM");
        Button buttonPlayHard = new Button();
        buttonPlayHard.setText("PLAY: HARD");
        Button buttonHighScore = new Button();
        buttonHighScore.setText("HIGHSCORE");
        Button buttonQuit = new Button();
        buttonQuit.setText("QUIT");
        buttonQuit.setOnAction(e -> onQuit());

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BASELINE_CENTER);
        buttonContainer.getChildren().addAll(buttonPlayEasy, buttonPlayMedium, buttonPlayHard, buttonHighScore, buttonQuit);

        ListView<String> list = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList(
                "1. Magnus: 2304", "2. Player2: 1826", "3. Player3: 1337");
        list.setItems(items);

        /*GridPane gridPane = new GridPane();
        gridPane.add(labelPlayAGame, 1, 0);
        gridPane.add(labelHighscore, 2, 0);
        gridPane.add(buttonContainer, 1, 1);
        gridPane.add(list, 2, 1);
        gridPane.setPrefHeight((HEIGHT/8)*6);*/

        VBox mainContent = new VBox(0);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPrefSize(WIDTH, HEIGHT);
        mainContent.getChildren().addAll(labelWelcome, buttonContainer);

        BorderPane root = new BorderPane();
        MenuBar menuBar = generateMenuBar();
        root.setTop(menuBar);
        root.setCenter(mainContent);

        return root;
    }

    /**
     * generates and return menubar
     * @return menubar
     */
    private MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuHelp = new Menu("Help");

        MenuItem menuItemQuit = new MenuItem("Quit");
        menuItemQuit.setOnAction(e -> onQuit());
        //menuItemQuit.setAccelerator( new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN) );
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


/*
    GridPane root = new GridPane();
    final int size = 8;

    public void start(Stage primaryStage) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Rectangle square = new Rectangle();
                Color color;
                if ((row + col) % 2 == 0) color = Color.BISQUE;
                else color = Color.DARKSALMON;
                square.setFill(color);
                root.add(square, col, row);
                square.widthProperty().bind(root.widthProperty().divide(size));
                square.heightProperty().bind(root.heightProperty().divide(size));
            }
        }
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }*/

}
