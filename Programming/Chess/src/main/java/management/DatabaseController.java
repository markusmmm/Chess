package management;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseController {

    private static final String dbUser = "db";
    private static final String dbPassword = "gitgud";
    private static final String dbURL = "ds137019.mlab.com";
    private static final String dbPort = "37019";
    private static final String dbName = "chess";

    public MongoClient mongoClient;
    public MongoDatabase db;

    /**
     * Establish a connection to the database
     */
    public DatabaseController() {
        /* Removes the console clutter */
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        MongoClientURI uri = new MongoClientURI("mongodb://" + dbUser + ":" + dbPassword +
                "@" + dbURL + ":" + dbPort + "/" + dbName);
        mongoClient = new MongoClient(uri);
        db = mongoClient.getDatabase(uri.getDatabase());
    }

    public static void main(String[] args) throws UnknownHostException {
        DatabaseController database = new DatabaseController();
        MongoCollection<Document> collection = database.db.getCollection("users");


        /*Document document = new Document("name", "Magnus")
                .append("score", 10000);
        collection.insertOne(document);
        databaseController.close();*/

        /*List<Document> invites = database.checkForGameInvites("magnus");
        System.out.println(invites.size());
        String player1 = (String) invites.get(0).get("player1");
        String player2 = (String) invites.get(0).get("player2");
        System.out.println(player1 + " has challenged " + player2 + " to a game of chess.");*/

        // database.createOnlineGame("magnus", "tom");

        /*Document gameData = database.getGame("5ae9f3e9e33da16d580fe644");
        String chessData = (String) gameData.get("chessData");
        System.out.println(chessData);*/

        //database.updateGame(new ObjectId("5af063f2c3ba570a2871dc0d"), "test");


        /* Deletes all users with score set to 0 */
        // collection.deleteMany(new Document("score", 0));
    }

    /**
     * Check if user already exists in the database
     * @param username
     * @return true (user exists)/false (user does not exist)
     */
    public boolean userExists(String username) {
        /*long count = db.getCollection("users")
                .count(new Document("name",
                        new Document("$regex", username)
                        .append("$options", "i")
                ));*/
        long count = db.getCollection("users")
                .count(new Document("name", username.toLowerCase()));
        if (count > 0)
            return true;
        return false;
    }

    /**
     * Retrieves the score for the given user from the database
     * @param username
     * @return score
     */
    public int getScore(String username) {
        if (userExists(username)) {
            /*FindIterable<Document> it = db.getCollection("users")
                    .find(new Document("name", new Document("$regex", username)
                            .append("$options", "i")));

            return (int) it.first().get("score");*/
            FindIterable<Document> it = db.getCollection("users")
                    .find(new Document("name", username.toLowerCase()));
            return (int) it.first().get("score");
        }
        return 0;
    }

    public int getPuzzlesCompleted(String username) {
        if (userExists(username)) {
            /*FindIterable<Document> it = db.getCollection("users")
                    .find(new Document("name", new Document("$regex", username)
                            .append("$options", "i")));

            return (int) it.first().get("score");*/
            FindIterable<Document> it = db.getCollection("users")
                    .find(new Document("name", username.toLowerCase()));
            if(it.first().get("completedPuzzles") == null){
                return 0;
            } else
                return (int) it.first().get("completedPuzzles");
        }
        return 0;
    }



    /**
     * Adds the user to the database
     * @param username
     */
    public void addUser(String username) {
        if (!userExists(username)) {
            db.getCollection("users").insertOne(new Document("name", username.toLowerCase()).append("score", 0).append("completedPuzzles",0));
        }
    }



    /**
     * Updates the score of the user
     * @param username
     * @param newScore
     */
    public void updateScore(String username, int newScore) {
        if (userExists(username)) {
            Document newDoc = new Document();
            newDoc.append("$set", new Document().append("score", newScore));
            Document searchQuery = new Document().append("name", username.toLowerCase());
            db.getCollection("users").updateOne(searchQuery, newDoc);
        }
    }

    public void updateGame(ObjectId id, String gameData) {
        Document newDoc = new Document();
        newDoc.append("$set", new Document().append("gameData", gameData));
        Document searchQuery = new Document().append("_id", id);
        db.getCollection("games").updateOne(searchQuery, newDoc);
    }

    /**
     * Grabs all the game invites which have not been seen
     * @param username
     * @return a list of invites
     */
    public List<Document> checkForGameInvites(String username) {
        if (userExists(username)) {
            List<Document> gameInvites = new ArrayList<>();
            db.getCollection("gameInvite").find(new Document("player2",
                    username.toLowerCase()).append("inviteAccepted", null)).into(gameInvites);
            return gameInvites;
        }
        return null;
    }

    /**
     * Grabs all the game invites which have not been seen
     * @param username
     * @return a list of invites
     */
    public List<Document> getOnlineGames(String username) {
        if (userExists(username)) {
            List<Document> games = new ArrayList<>();
            db.getCollection("games").find(new Document("player1",
                    username.toLowerCase()).append("completed", false)).into(games);
            db.getCollection("games").find(new Document("player2",
                    username.toLowerCase()).append("completed", false)).into(games);
            return games;
        }
        return null;
    }

    /**
     * Creates a new game of chess in the database
     * @param player1
     * @param player2
     */
    public void createOnlineGame(String player1, String player2) {
        String defaultChess = "8 -1 0 0 0\n" +
                "-1 -1\n" +
                "rnbqkbnr\n" +
                "pppppppp\n" +
                "eeeeeeee\n" +
                "eeeeeeee\n" +
                "eeeeeeee\n" +
                "eeeeeeee\n" +
                "PPPPPPPP\n" +
                "RNBQKBNR";
        Document query = new Document("completed", false).append("player1", player1.toLowerCase())
                .append("player2", player2.toLowerCase()).append("gameData", defaultChess);
        db.getCollection("games").insertOne(query);
    }

    public void createGameInvite(String player1, String player2) {
        Document query = new Document("player1", player1.toLowerCase()).append("player2", player2.toLowerCase())
                .append("inviteAccepted", null);
        db.getCollection("gameInvite").insertOne(query);
    }

    public void markInviteAsViewed(ObjectId id) {
        Document newDoc = new Document();
        newDoc.append("$set", new Document().append("viewed", true));
        Document searchQuery = new Document().append("_id", id);
        db.getCollection("games").updateOne(searchQuery, newDoc);
    }

    public void markInviteAsViewed(String id) {
        markInviteAsViewed(new ObjectId(id));
    }

    /**
     * Updates the gameInvites in the database and creates a new
     * online game if the invited is accepted
     * @param id
     * @param accepted
     * @param player1
     * @param player2
     */
    public void handleGameInvite(ObjectId id, boolean accepted, String player1, String player2) {
        Document newDoc = new Document();
        newDoc.append("$set", new Document().append("inviteAccepted", accepted));
        Document searchQuery = new Document().append("_id", id);
        db.getCollection("gameInvite").updateOne(searchQuery, newDoc);
        if (accepted)
            createOnlineGame(player1.toLowerCase(), player2.toLowerCase());
    }

    /**
     * Get game document by id
     * @param id
     * @return game document
     */
    public Document getGame(ObjectId id) {
        Document query = new Document("_id", id);
        return db.getCollection("games").find(query).first();

    }

    /**
     * Forfeits game
     * @param id
     */
    public void forfeitGame(ObjectId id) {
        Document newDoc = new Document();
        newDoc.append("$set", new Document().append("completed", true));
        Document searchQuery = new Document().append("_id", id);
        db.getCollection("games").updateOne(searchQuery, newDoc);
    }

    /**
     * Marks the game as completed and sets the winner
     * @param id
     */
    public void gameOver(ObjectId id, String winner) {
        Document newDoc = new Document();
        newDoc.append("$set", new Document().append("completed", true));
        Document newDoc2 = new Document();
        newDoc2.append("$set", new Document().append("winner", winner));
        Document searchQuery = new Document().append("_id", id);
        db.getCollection("games").updateOne(searchQuery, newDoc);
        db.getCollection("games").updateOne(searchQuery, newDoc2);
    }

    public void forfeitGame(String id) {
        forfeitGame(new ObjectId(id));
    }


    /**
     * Updates the number of puzzles completed of the user
     * @param username
     * @param newScore
     */

    public void updatePuzzlesCompleted(String username, int newScore) {
        if (userExists(username)) {
            Document newDoc = new Document();
            newDoc.append("$set", new Document().append("completedPuzzles", newScore));
            Document searchQuery = new Document().append("name", username.toLowerCase());
            db.getCollection("users").updateOne(searchQuery, newDoc);
        }
    }

    /**
     * Closes the database connection
     */
    public void close() {
        mongoClient.close();
    }

}
