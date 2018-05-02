package management;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.QueryBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

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


        /* Deletes all users with score set to 0 */
        //collection.deleteMany(new Document("score", 0));
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

    /**
     * Adds the user to the database
     * @param username
     */
    public void addUser(String username) {
        if (!userExists(username)) {
            db.getCollection("users").insertOne(new Document("name", username.toLowerCase()).append("score", 0));
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

    public List<Document> checkForGameInvites(String username) {
        if (userExists(username)) {
            List<Document> gameInvites = new ArrayList<>();
            db.getCollection("gameInvite").find(new Document("player2", username.toLowerCase()).append("inviteAccepted", null)).into(gameInvites);
            return gameInvites;
        }
        return null;
    }

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
        Document query = new Document("completed", false).append("player1", player1)
                .append("player2", player2).append("gameData", defaultChess);
        db.getCollection("games").insertOne(query);
    }

    public void handleGameInvite(ObjectId id, boolean accepted, String player1, String player2) {
        Document newDoc = new Document();
        newDoc.append("$set", new Document().append("inviteAccepted", accepted));
        Document searchQuery = new Document().append("_id", id);
        db.getCollection("gameInvite").updateOne(searchQuery, newDoc);
        createOnlineGame(player1, player2);
    }

    public Document getGame(String id) {
        Document query = new Document("_id", new ObjectId(id));
        return db.getCollection("games").find(query).first();

    }

    /**
     * Closes the database connection
     */
    public void close() {
        mongoClient.close();
    }

}
