import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import management.Board;
import pieces.IChessPiece;
import resources.Alliance;
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
        //System.out.print("Printing " + pos + ": ");

        if(getPiece() == null) {
            setFill(Color.TRANSPARENT);
            //System.out.println(" EMPTY");
            return;
        }

        String pieceName = getPiece().piece().toString();
        //System.out.println(pieceName);

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

    public boolean tileClicked(MouseEvent e, Alliance alliance) {
        IChessPiece piece = getPiece();

       // System.out.println(pos + ": ");

        if (piece == null) {
            System.out.println("No piece");
            return false;
        }
        if (piece.alliance() != alliance) {
            System.out.println("Not your alliance");
            return false;
        }

        System.out.println("legal piece");
        return true;
    }
}
