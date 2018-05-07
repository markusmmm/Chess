package main;

import javafx.application.Platform;
import management.DatabaseController;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

import static org.apache.commons.io.FileUtils.readFileToString;

public class GameUpdater extends TimerTask {
    private DatabaseController database = new DatabaseController();
    private GameBoard gameBoard;
    private ObjectId id;
    private String username;

    public GameUpdater(GameBoard gameBoard, ObjectId id, String username) {
        this.gameBoard = gameBoard;
        this.id = id;
        this.username = username;
    }

    @Override
    public void run() {
        Document gameDocument = database.getGame(id);
        String databaseGameData = (String) gameDocument.get("gameData");
        try {
            String gameDataOnPc = readFileToString(new File(System.getProperty("user.home"), "GitGud/.online/" + username + "/" + id + ".txt"),
                    StandardCharsets.UTF_8);
            if (!databaseGameData.equals(gameDataOnPc)) {
                File gameFile = new File(System.getProperty("user.home"), "GitGud/.online/" + username + "/" + id + ".txt");
                FileUtils.writeStringToFile(gameFile, databaseGameData, StandardCharsets.UTF_8);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gameBoard.performLoad(gameFile);
                    }
                });
                /*System.out.println("is equal");
                File gameFile = new File(System.getProperty("user.home"), "GitGud/.online/" + id + ".txt");
                FileUtils.writeStringToFile(gameFile, gameData, StandardCharsets.UTF_8);
                gameBoard.performLoad(gameFile);*/
            }
        } catch (IOException e1){
        e1.printStackTrace();
        }
    }
}
