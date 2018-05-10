package main;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import management.Board;
import management.ChessComputer;
import pieces.IChessPiece;
import resources.Alliance;
import resources.Console;
import resources.Move;
import resources.Vector2;

public class Tile extends Rectangle {
    private final Vector2 pos;
    private final Board board;

    public Tile(Vector2 pos, Board board) {
        super();
        this.pos = pos;
        this.board = board;
        this.setFill(Color.TRANSPARENT);
    }


    public void drawPiece() {
        if(getPiece() == null) {
            setFill(Color.TRANSPARENT);
            return;
        }

        String pieceName = getPiece().piece().toString();
        String s = "";

        if (getPieceColor() == Alliance.WHITE)
            s += "w_";
        else
            s += "b_";
        s += pieceName.toLowerCase();

        Image image = new Image("images/pieces/" + s + ".png", 64, 64, true, true);
        this.setFill(new ImagePattern(image));
    }

    public IChessPiece getPiece() {
        return board.getPiece(pos);
    }

    public Vector2 getPos() {
        return pos;
    }


    public Alliance getPieceColor() {
        if (getPiece() != null)
            return getPiece().alliance();
        else
            return null;
    }
}
