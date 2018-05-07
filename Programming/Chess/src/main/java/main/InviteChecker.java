package main;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import management.DatabaseController;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;
import java.util.TimerTask;

class InviteChecker extends TimerTask {
    private DatabaseController database = new DatabaseController();
    private boolean hasPopup = false;
    private String username;

    public InviteChecker(String username) {
        this.username = username;
    }

    public void run() {
        List<Document> invites = database.checkForGameInvites(username);
        if (invites.size() > 0) {
            for (int i = 0; i < invites.size(); i++) {
                ObjectId id = (ObjectId) invites.get(i).get("_id");
                String player1 = (String) invites.get(i).get("player1");
                String player2 = (String) invites.get(i).get("player2");
                if (!hasPopup) {
                    hasPopup = true;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Game Invite");
                            alert.setHeaderText(player1 + " has invited you to a game of chess!");
                            alert.setContentText("Do you want to accept?");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                database.handleGameInvite(id, true, player1, player2);
                                hasPopup = false;
                            } else {
                                database.handleGameInvite(id, false, player1, player2);
                                hasPopup = false;
                            }
                        }
                    });
                }
            }
        }
    }
}
