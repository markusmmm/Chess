package main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public abstract class GUIController {
    protected GUIController() {

    }

    protected Label createLabel(double prefWidth, String text, String id) {
        Label label = new Label();
        label.setPrefWidth(prefWidth);
        label.setText(text);
        label.setId(id);
        return label;
    }
    protected Button createButton(String text, EventHandler<ActionEvent> value) {
        Button button = new Button();
        button.setText(text);
        button.setOnAction(value);
        return button;
    }
    protected MenuItem createMenuItem(String text, EventHandler<ActionEvent> value) {
        MenuItem item = new MenuItem();
        item.setText(text);
        item.setOnAction(value);
        return item;
    }
    protected VBox createVBox(Pos alignment, Text content, String id) {
        VBox box = new VBox();

        box.setAlignment(alignment);
        box.getChildren().add(content);
        box.setId(id);

        return box;
    }

    protected void syncControl(Control control, double prefWidth, double prefHeight, String id) {
        control.setPrefWidth(prefWidth);
        control.setPrefHeight(prefHeight);
        control.setId(id);
    }
}
