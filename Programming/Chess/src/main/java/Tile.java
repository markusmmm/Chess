import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import pieces.*;
import resources.*;

public class Tile extends Rectangle {
    private Vector2 pos;
    private IChessPiece piece;

    public Tile(Vector2 pos) {
        super();
        this.pos = pos;
        this.piece = null;
        this.setFill(Color.TRANSPARENT);
    }


    public void setPiece(IChessPiece piece2) {
        this.piece = piece2;

        String s = "";
        if(piece.alliance() == Alliance.WHITE)
            s += "w_";
        else
            s += "b_";
        s += piece.piece().toString().toLowerCase();

        Image image = new Image("images/pieces/" + s + ".png");
        this.setFill(new ImagePattern(image));
    }

    public IChessPiece getPiece() {
        return piece;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Alliance getPieceColor() {
        if(getPiece() != null)
            return getPiece().alliance();
        else
            return null;
    }

}
