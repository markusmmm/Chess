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
        DatabaseController database = new DatabaseController();
        this.score = database.getScore(username);
        database.close();
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