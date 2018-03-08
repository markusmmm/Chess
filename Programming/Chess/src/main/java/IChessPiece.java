public interface IChessPiece {
    Piece Piece();
    boolean legalMove(Vector2 move);
    boolean canJump();
    boolean move(Vector2 move);
    void remove();
}
