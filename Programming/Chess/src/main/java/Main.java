import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import management.Board;
import pieces.IChessPiece;
import resources.Vector2;

import java.util.Map;

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
        final int WIDTH = 630;
        final int HEIGHT = 600;
        BorderPane root = new BorderPane();

        Label labelWelcome = new Label("Welcome, " + username + "!");
        labelWelcome.setPrefWidth(WIDTH);
        labelWelcome.setMinHeight((HEIGHT / 8)*2);
        labelWelcome.setAlignment(Pos.CENTER);
        labelWelcome.setId("title");

//        Label labelPlayAGame = new Label("Play a game");
//        labelPlayAGame.setPrefWidth(WIDTH / 2);
//        labelPlayAGame.setMinHeight(HEIGHT / 8);
//        labelPlayAGame.setAlignment(Pos.CENTER);

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

        buttonPlayEasy.setOnAction(e -> root.setCenter(createChessGame()));
        buttonPlayMedium.setOnAction(e -> root.setCenter(createChessGame()));
        buttonPlayHard.setOnAction(e -> root.setCenter(createChessGame()));
        buttonQuit.setOnAction(e -> onQuit());

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BASELINE_CENTER);
        buttonContainer.getChildren().addAll(buttonPlayEasy, buttonPlayMedium, buttonPlayHard, buttonHighScore, buttonQuit);

        VBox mainContent = new VBox(0);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPrefSize(WIDTH, HEIGHT);
        mainContent.getChildren().addAll(labelWelcome, buttonContainer);

        root.setTop(generateMenuBar());
        root.setCenter(mainContent);

        return root;
    }

    /**
     *
     * @return chessGame
     */
    private GridPane createChessGame() {
        final int SIZE = 8;
        Board board = new Board(SIZE, false);
        GridPane grid = new GridPane();
        Tile[][] tiles = new Tile[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle rect = new Rectangle();
                Vector2 pos = new Vector2(row, col);
                Tile tile;

                if ((row + col) % 2 == 0) {
                    tile = new Tile(pos);
                    rect.setFill(Color.CORNSILK);
                } else {
                    tile = new Tile(pos);
                    rect.setFill(Color.DARKSALMON);
                }

                tile.setOnMouseClicked(e -> {
                    System.out.println("row: " + tile.getPos().getX() +
                            ", col: " + tile.getPos().getY() + ", piece: " + tile.getPiece());
                });

                tiles[row][col] = tile;
                grid.add(rect, col, row);
                grid.add(tile, col, row);
                rect.widthProperty().bind(grid.widthProperty().divide(SIZE));
                rect.heightProperty().bind(grid.heightProperty().divide(SIZE));
                tile.widthProperty().bind(grid.widthProperty().divide(SIZE));
                tile.heightProperty().bind(grid.heightProperty().divide(SIZE));
            }
        }

        for(Map.Entry<Vector2, IChessPiece> entry : board.pieces.entrySet()) {
            int row = entry.getKey().getX();
            int col = entry.getKey().getY();
            IChessPiece piece = entry.getValue();
            tiles[col][row].setPiece(piece);
        }


        return grid;
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
