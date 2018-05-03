package resources;


public class Highscore {
    private String username;
    private int score;

    public Highscore(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username + ": " + score;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        Highscore score = (Highscore) obj;
        if (this.username.equals(score.username))
            isEqual = true;
        return isEqual;
    }
}
