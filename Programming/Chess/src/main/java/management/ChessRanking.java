package management;

public class ChessRanking {

    public final static int SUPPORTED_PLAYERS = 2;


    private static String player1;
    private static String player2;


    private static int scorePlayer1;
    private static int scorePlayer2;

    // player 1 won
    private final static int player1Win = 1;
    // draw
    private final static int DRAW = 0;
    // player 2 won
    private final static int player2Win = -1;

    public ChessRanking(String player1, String player2, int score1, int score2){
        this.player1 = player1;
        this.player2 = player2;
        this.scorePlayer1= score1;
        this.scorePlayer2 = score2;
    }


    public int getScore(String player){
        if(player.equals(player1)){
            return scorePlayer1;
        }
        return scorePlayer2;
    }

    public int getNewScore(String player, int winConstant) {
        if(player.equals(player1) && winConstant == player1Win) return scorePlayer1+3;
        if(player.equals(player1) && winConstant == player2Win) return scorePlayer1;
        if(player.equals(player2) && winConstant == player2Win) return scorePlayer2 + 3;
        if(player.equals(player2) && winConstant == player2Win) return scorePlayer2;

        else if(player.equals(player1)) return scorePlayer1+1;

        else return scorePlayer2+1; 


    }

}
