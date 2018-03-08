public interface IChessPiece {
    Piece piece();
    boolean legalMove(Vector2 move);
    boolean canJump();
    boolean move(Vector2 move);
    void remove();
}
