package management;

import resources.Vector2;

import java.util.Scanner;

public class GameManager {

	private static Board board = null;

	//public static void main(String[] args) {
	//    board = new Board(2,8, false);
    //    int playerI = 0;

    //    while(true) {
    //        if(step(playerI))
    //            playerI++;
    //    }
	//}

	/**
	 * Takes in input from the active player, and attempts to perform the given move
     * NOTE: The code below is a placeholder functionality, until UI has been implemented
	 */
	public static boolean step(int playerI) {
		Scanner in = new Scanner(System.in);
		System.out.println("Player " + (playerI + 1) + "'s turn");
		System.out.println("Enter start pos: ");
		int x0 = in.nextInt(), y0 = in.nextInt();
        System.out.println("Enter end pos: ");
        int x1 = in.nextInt(), y1 = in.nextInt();
        in.close();

        Vector2 start = new Vector2(x0, y0),
                end = new Vector2(x1, y1);

        if(board.movePiece(start, end))
            return true;

        System.out.println("Invalid move!");
        return false;
	}

}