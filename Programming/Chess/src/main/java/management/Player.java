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
        this.username = username;
        this.alliance = alliance;
        this.score = new DatabaseController().getScore(username);
    }

	public String getUsername() {
		return username;
	}

	public Alliance getAlliance() {
        return alliance;
    }

	public int getScore() {
        return score;
    }

}