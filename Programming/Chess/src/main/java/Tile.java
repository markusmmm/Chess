import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import pieces.IChessPiece;
import resources.Alliance;
import resources.Vector2;

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
        if (piece.alliance() == Alliance.WHITE)
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
        if (getPiece() != null)
            return getPiece().alliance();
        else
            return null;
    }

    public void tileClicked(MouseEvent e, Alliance alliance) {
        if (this.piece == null) {
            System.out.println("No piece");
            return;
        }
        if (this.piece.alliance() != alliance) {
            System.out.println("Not your alliance");
            return;
        }

        System.out.println("legal piece");

    }
}
