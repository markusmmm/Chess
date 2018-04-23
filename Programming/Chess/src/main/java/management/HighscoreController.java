package management;

import resources.Highscore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class HighscoreController {

    // Path to Highscore text-file in the resource folder
    private final String HIGHSCORE_TXT = "highscore.txt";
    private ArrayList<Highscore> highscores;

    public HighscoreController() {
        highscores = new ArrayList<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(HIGHSCORE_TXT);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                String username = split[0];
                int score = Integer.parseInt(split[1]);
                highscores.add(new Highscore(username, score));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(String username) {
        Highscore score = new Highscore(username, 0);
        if (highscores.contains(score))
            return true;
        return false;
    }

    public boolean addUser(String username) {
        if (userExists(username))
            return false;
        highscores.add(new Highscore(username, 0));
        syncWithTextFile();
        return true;
    }

    public Highscore getScore(String username) {
        if (userExists(username)) {
            int index = highscores.indexOf(new Highscore(username, 0));
            return highscores.get(index);
        }
        System.out.println("Can't grab score for " + username + ", user does not exist.");
        return null;
    }

    public void updateScore(String username, int updatedValue) {
        Highscore score = getScore(username);
        score.setScore(updatedValue);
        syncWithTextFile();
    }

    public void updateUsername(String oldUsername, String newUsername) {
        Highscore score = getScore(oldUsername);
        score.setUsername(newUsername);
        syncWithTextFile();
    }

    public void syncWithTextFile() {
        StringBuilder sb = new StringBuilder();
        for (Highscore score : highscores) {
            sb.append(score.getUsername() + ",");
            sb.append(score.getScore());
            sb.append("\n");
        }
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(HIGHSCORE_TXT);
            PrintWriter writer = new PrintWriter(classloader.getResource(HIGHSCORE_TXT).getPath());
            writer.write(sb.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Highscore> getHighscores() {
        return highscores;
    }
}
