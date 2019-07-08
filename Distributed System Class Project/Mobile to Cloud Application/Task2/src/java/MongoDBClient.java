
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

/**
 * It serves a class that set up database connection.
 *
 * @author Zirui Zheng
 * @AndrewID ziruizhe
 */
public class MongoDBClient {

    // Unique database password
    final String PASSWORD = "distributed95702";

    // Unique database URI
    MongoClientURI uri = new MongoClientURI(
            "mongodb://spongezzr:" + PASSWORD
            + "@dsproject4cluster-shard-00-00-57zb0.mongodb.net:27017,"
            + "dsproject4cluster-shard-00-01-57zb0.mongodb.net:27017,"
            + "dsproject4cluster-shard-00-02-57zb0.mongodb.net:27017/test?ssl=true&replicaSet=DSProject4Cluster-shard-0&authSource=admin&retryWrites=true");

    // Set up connection to database and get the collection we are using to store current data
    MongoClient mongoClient = new MongoClient(uri);
    MongoDatabase database = mongoClient.getDatabase("test");
    MongoCollection collection = database.getCollection("test");

    /**
     * Send data to database
     *
     * @param doc a Document that stores all useful information
     */
    public void recordData(Document doc) {
        collection.insertOne(doc);
    }

    /**
     * Get all the data from database and store in the appropriate form for jsp
     * display.
     *
     * @return an ArrayList that stores all the data in designated order
     */
    public ArrayList<String> displayData() {
        ArrayList<String> list = new ArrayList<>();
        int totalVisits = 0; // count the total visits
        Map<String, Integer> deviceCount = new HashMap<>(); // a map that stores device types and correspondent visits
        Map<String, Integer> keyWordCount = new HashMap<>(); // a map that stores search words and correspondent search times
        long timeSum = 0; // count total process time
        long minTime = Long.MAX_VALUE; // store the minimum process time for one single visit

        StringBuilder logs = new StringBuilder(); // a StringBuilder that stores all the logs

        for (Object o : collection.find()) { // process one piece of data each time
            Document doc = (Document) o; // get the data for one request
            logs.append(doc.toString()).append("\n\n"); // record it in the log
            totalVisits++; // increase the total visit count

            // fill the device count map
            String device = doc.getString("request_device");
            if (!deviceCount.containsKey(device)) {
                deviceCount.put(device, 0);
            }
            deviceCount.put(device, deviceCount.get(device) + 1);

            // fill the search word count map
            String word = doc.getString("client_request_keyWord");
            if (!keyWordCount.containsKey(word)) {
                keyWordCount.put(word, 0);
            }
            keyWordCount.put(word, keyWordCount.get(word) + 1);

            // process the time and check if this process time is smaller than min
            long time = (long) doc.get("total_process_time");
            timeSum += time;
            minTime = Math.min(time, minTime);
        }

        // find the top device and top search word
        String[] topDevice = findMost(deviceCount);
        String[] topWord = findMost(keyWordCount);

        // get average process time in seconds
        double avgTime = (double) timeSum / totalVisits * 0.001;

        // get shortest process time in seconds
        double minProcTime = (double) minTime * 0.001;

        // create a DecimalFormat object to display only 3 decimals for our time
        DecimalFormat df = new DecimalFormat("#.###");

        // add all the info to the ArrayList
        list.add(logs.toString());
        list.add(Integer.toString(totalVisits));
        list.add(topDevice[0]);
        list.add(topDevice[1]);
        list.add(df.format(avgTime) + "s");
        list.add(df.format(minProcTime) + "s");
        list.add(topWord[0]);
        list.add(topWord[1]);

        return list;
    }

    /**
     * A helper method to find the topmost element in a map by values
     *
     * @param map a map that stores string / count pairs
     * @return an array that stores the topmost object and its count
     */
    private String[] findMost(Map<String, Integer> map) {
        int max = 0; // initialize a max count
        String maxString = ""; // initialize a sting that stores the object which has the max count
        for (Map.Entry<String, Integer> entry : map.entrySet()) { // loop over all entries

            // if a larger value found, update max and maxString
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxString = entry.getKey();
            }
        }

        return new String[]{maxString, Integer.toString(max)};
    }

}
