package management;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;

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
        MongoClientURI uri = new MongoClientURI("mongodb://" + dbUser + ":" + dbPassword +
                "@" + dbURL + ":" + dbPort + "/" + dbName);
        mongoClient = new MongoClient(uri);
        db = mongoClient.getDatabase(uri.getDatabase());
    }

    public static void main(String[] args) throws UnknownHostException {
        DatabaseController databaseController = new DatabaseController();
        MongoCollection<Document> collection = databaseController.db.getCollection("users");
        Document document = new Document("name", "Magnus")
                .append("score", 10000);
        collection.insertOne(document);
        databaseController.close();
    }

    /**
     * Check if user already exists in the database
     * @param username
     * @return true/false
     */
    public boolean userExists(String username) {
        long count = db.getCollection("users")
                .count(new Document("name",
                        new Document("$regex", username)
                        .append("$options", "i")
                ));
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
            FindIterable<Document> it = db.getCollection("users")
                    .find(new Document("name", new Document("$regex", username)
                            .append("$options", "i")));
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
            db.getCollection("users").insertOne(new Document("name", username).append("score", 0));
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

            Document searchQuery = new Document().append("name",
                    new Document("$regex", username).append("$options", "i"));
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