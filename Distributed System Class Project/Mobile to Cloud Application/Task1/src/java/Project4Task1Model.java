
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class serves as the Model in Project 4 Task 1. It calls the third-party
 * API to fetch data and process data
 *
 * @author Zirui Zheng
 * @AndrewID ziruizhe
 */
public class Project4Task1Model {

    // URL to the third-party API
    private static final String BASE_URL = "http://app.ticketmaster.com/discovery/v2/events.json";
    // Unique API KEY 
    private static final String API_KEY = "rVfSV9ULmq3KvikDfbyws8eRKIhnJrWF";

    /**
     * Public method that will be called by the servlet. It calls the
     * third-party API and search for events.
     *
     * @param searchParam the keyword for searching
     * @return a JSONArray that contains all the response provided by the
     * TicketMaster API
     */
    public JSONArray search(String searchParam) {

        // construct the last part of the API url
        String query = "&apikey=" + API_KEY;

        try {
            // Open a HTTP connection between your Java application and TicketMaster based URL
            HttpURLConnection connection
                    = (HttpURLConnection) new URL(BASE_URL + "?" + searchParam + query).openConnection();

            // Set request method to GET
            connection.setRequestMethod("GET");

            // Now read response body to get events data
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            /*
            * Per instructions from TicketMaster API, at most 20 events will be returned.
            * In order to clearly read the events, we convert the response to a JSONArray.
             */
            JSONObject obj = new JSONObject(response.toString());
            if (obj.isNull("_embedded")) {
                return new JSONArray();
            }
            JSONObject embedded = obj.getJSONObject("_embedded");
            JSONArray events = embedded.getJSONArray("events");
            return events;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    /**
     * Public method that will be called by the servlet. It process the
     * JSONArray so that we get only the data we want for our app.
     *
     * @param events the JSONArray returned by the TicketMaster API
     * @return a JSONObject that is ready to send back to the app
     */
    public JSONObject operation(JSONArray events) {

        // initialize the JSONObject to store the array of events
        JSONObject array = new JSONObject();

        // we only send back at most 5 events back to our app
        // we pick randomly use Random to generate index and Set to de-duplicate
        // if less than 5 events are found, we send back all of them
        int numberOfEvents = Math.min(events.length(), 5);

        // initialize the set for de-duplication
        HashSet<Integer> set = new HashSet<>();

        for (int i = 0; i < numberOfEvents; i++) { // only run at most 5 times

            // generate a random index that has not been generated
            Random rand = new Random();
            int index = rand.nextInt(numberOfEvents);
            while (set.contains(index)) {
                index = rand.nextInt(numberOfEvents);
            }
            set.add(index);

            try { // process the data
                JSONObject json = events.getJSONObject(index); // get the object out of the JSONArray
                JSONObject newJson = new JSONObject(); // create a JSONObject to store info we need

                // there are 6-10 different image urls
                // we retrieve all the urls and get the one that we want for our app
                String images = json.get("images").toString();
                String imageURL = searchImageURL(images);

                String eventName = json.get("name").toString(); // get event title

                // use helper method to get event type
                JSONArray classifications = (JSONArray) json.get("classifications");
                JSONObject classification = (JSONObject) classifications.getJSONObject(0);
                String segment = getSegment(classification);
                String genre = getGenre(classification);

                String date = getDate(json.get("dates")); // get event date 
                String address = getAddress(json.get("_embedded")); // get event address
                String url = json.get("url").toString(); // get ticket purchase url

                // put all the info into that JSONObject we created
                newJson.put("name", eventName);
                newJson.put("image", imageURL);
                newJson.put("segment", segment);
                newJson.put("genre", genre);
                newJson.put("date", date);
                newJson.put("address", address);
                newJson.put("url", url);

                // append this JSONObject into our result JSONObject
                // in this case, when the app receives the JSONObject, 
                // it can easily get each event by JSONObject.get(key) method
                array.put("event" + i, newJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    /**
     * A helper method to fetch the right image URL for an event
     *
     * @param images the string that contains all available images and
     * information
     * @return one URL that fits our app
     */
    private String searchImageURL(String images) {

        // The optimal size should be width=305
        // If no such image is found, we accpet 640, 1024
        // If still no, we simply get the first image we found
        String[] sizes = {"305", "640", "1024", ""};

        int imageIndex = 0; // initialize the index of image URL
        for (String size : sizes) {
            // if an image is found, we exit the loop
            imageIndex = images.indexOf("\"width\":" + size);
            if (imageIndex != -1) {
                break;
            }
        }

        // If not found, then no such photo is available.
        if (imageIndex == -1) {
            return null;
        }

        // Look for the url
        int cutLeft = images.indexOf("\"url\":\"", imageIndex);

        // Look for the close quotation marks
        int cutRight = images.indexOf("\"", cutLeft + "\"url\":\"".length());
        return images.substring(cutLeft + "\"url\":\"".length(), cutRight);
    }

    /**
     * A helper method to get the segment of an event, e.g. Sports
     *
     * @param classification the JSONObject that stores all kinds of type
     * information
     * @return a string of segment
     * @throws JSONException
     */
    private String getSegment(JSONObject classification) throws JSONException {
        JSONObject segment = classification.getJSONObject("segment");
        return segment.getString("name");
    }

    /**
     * A helper method to get the genre of an event, e.g. Basketball
     *
     * @param classification the JSONObject that stores all kinds of type
     * information
     * @return a string of genre
     * @throws JSONException
     */
    private String getGenre(JSONObject classification) throws JSONException {
        JSONObject genre = classification.getJSONObject("genre");
        return genre.getString("name");
    }

    /**
     * A helper method to get the date of an event
     *
     * @param dates the JSONObject that stores all date information
     * @return a string of date
     * @throws JSONException
     */
    private String getDate(Object dates) throws JSONException {
        String date = dates.toString();

        // all we want is the local date and time
        int dateIndex = date.indexOf("\"localDate\":\"");
        int timeIndex = date.indexOf("\"localTime\":\"");

        // if not found, the event date is TBD
        if (dateIndex == -1 || timeIndex == -1) {
            return "TBD";
        }

        // substring the date and time info by looking for "\" sign on both sides
        int addOn = "\"localDate\":\"".length();
        String dateResult = date.substring(dateIndex + addOn, date.indexOf("\"", dateIndex + addOn));
        String timeResult = date.substring(timeIndex + addOn, date.indexOf("\"", timeIndex + addOn));

        // construct a meaningful date/time string to return
        return "Date: " + dateResult + "\nTime: " + timeResult;
    }

    /**
     * A helper method to get the address of an event
     *
     * @param addressJSON the JSONObject that stores all address information
     * @return a string of address
     * @throws JSONException
     */
    private String getAddress(Object addressJSON) throws JSONException {
        JSONArray venues = (JSONArray) ((JSONObject) addressJSON).get("venues");
        JSONObject venue = venues.getJSONObject(0); // get the venue information
        String city = ((JSONObject) venue.get("city")).get("name").toString(); // right now we just want the city information
        return city;
    }
}
