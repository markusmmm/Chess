package management;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.Iterator;

public class DatabaseController {

    private static final String dbUser = "db";
    private static final String dbPassword = "gitgud";
    private static final String dbURL = "ds137019.mlab.com";
    private static final String dbPort = "37019";
    private static final String dbName = "chess";

    public MongoClient mongoClient;
    public MongoDatabase db;

    public DatabaseController() {
        MongoClientURI uri = new MongoClientURI("mongodb://" + dbUser + ":" + dbPassword +
                "@" + dbURL + ":" + dbPort + "/" + dbName);
        mongoClient = new MongoClient(uri);
        db = mongoClient.getDatabase(uri.getDatabase());
    }

    /* Tests adding a user and a highscore to the users collection */
    public static void main(String[] args) throws UnknownHostException {
        DatabaseController databaseController = new DatabaseController();
        MongoCollection<Document> collection = databaseController.db.getCollection("users");
        Document document = new Document("name", "Magnus")
                .append("score", 10000);
        collection.insertOne(document);
        databaseController.close();
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        MongoCollection<Document> collection = db.getCollection(collectionName);
        return collection;
    }

    public boolean userExists(String username) {
        long count = db.getCollection("users")
                .count(new Document("name", username));
        if (count > 0)
            return true;
        return false;
    }

    public int getScore(String username) {
        if (userExists(username)) {
            FindIterable<Document> it = db.getCollection("users")
                    .find(new Document("name", username));
            return (int) it.first().get("score");
        }
        return 0;
    }

    public void close() {
        mongoClient.close();
    }

}
