import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {
    @FXML private Text errorField;
    @FXML private TextField username;
    @FXML private Button loginButton;

    public void loginToChess(ActionEvent actionEvent) {
        if(username.getText() == null || username.getText().trim().isEmpty())
            errorField.setText("Please enter a non-empty username.");
        else {
            errorField.setText(username.getText());


        }
    }
}
