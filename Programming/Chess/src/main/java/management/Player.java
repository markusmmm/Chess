package management;

import resources.Alliance;

public class Player {

	private String username;
	private Alliance alliance;
	private int score;

    /**
     *
	 * @param username
     * @param alliance
     */
    public Player(String username, Alliance alliance) {
        // TODO - implement Player.Player
        throw new UnsupportedOperationException();
    }

	public String getUsername() {
		return this.username;
	}

	public Alliance getAlliance() { return alliance; }

	public int getScore() { return score; }
}