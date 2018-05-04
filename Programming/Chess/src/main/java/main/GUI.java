package main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import resources.Console;

public abstract class GUI {
    public static Button createButton(String text, EventHandler<ActionEvent> value) {
        Button button = new Button();
        button.setText(text);
        button.setOnAction(value);
        return button;
    }
    public static MenuItem createMenuItem(String text, EventHandler<ActionEvent> value) {
        MenuItem item = new MenuItem();
        item.setText(text);
        item.setOnAction(value);
        return item;
    }

    public static void syncRegion(Control control, double prefWidth, double prefHeight, String id) {
        control.setPrefWidth(prefWidth);
        control.setPrefHeight(prefHeight);
        control.setId(id);
    }


    public static <T extends Node> T createNode(T elem, String input, String id) {
        String[] commands = input.split(";");

        if(id.length() > 0)
            elem.setId(id);

        for(String cmd : commands) {
            String[] temp = cmd.split(":");
            String key = temp[0];
            String value = temp[1];

            setProperty(elem, key, value);
        }

        return elem;
    }
    public static <T extends Node> T createNode(T elem, String input) {
        return createNode(elem, input, "");
    }

    private static void setProperty(Node elem, String key, String value) {
        if(key.equals("spacing")) ((VBox) elem).setSpacing(Double.parseDouble(value));
        else if(key.equals("prefWidth")) ((Region)elem).setPrefWidth(Double.parseDouble(value));
        else if(key.equals("prefHeight")) ((Region)elem).setPrefHeight(Double.parseDouble(value));
        else if(key.equals("maxWidth")) ((Region)elem).setMaxWidth(Double.parseDouble(value));
        else if(key.equals("maxHeight")) ((Region)elem).setMaxHeight(Double.parseDouble(value));
        else if(key.equals("minWidth")) ((Region)elem).setMinWidth(Double.parseDouble(value));
        else if(key.equals("minHeight")) ((Region)elem).setMinHeight(Double.parseDouble(value));
        else if(key.equals("alignment")) {
            Pos val = Pos.valueOf(value);
            if(elem instanceof Labeled) ((Labeled)elem).setAlignment(val);
            else if(elem instanceof TextField) ((TextField)elem).setAlignment(val);
            else if(elem instanceof VBox) ((VBox)elem).setAlignment(val);
            else throw new IllegalArgumentException("Can't set alignment of " + elem.getClass().getName());
        }
        else if(key.equals("textAlignment")) ((Labeled)elem).setTextAlignment(TextAlignment.valueOf(value));
        else if(key.equals("text")) ((Labeled)elem).setText(value);
        else if(key.equals("fill")) ((Shape)elem).setFill(Color.valueOf(value));
        else throw new IllegalArgumentException("Unknown GUI property: " + key);
    }
}
