package external;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "rVfSV9ULmq3KvikDfbyws8eRKIhnJrWF";
	
	public JSONArray search(double lat, double lon, String keyword) {
		// Encode keyword
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		// Convert lat/lon to geo hash
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		
		// Make URL "apikey=12345&geoPoint=abcd&keyword=music&radius=50";
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", 
				API_KEY, geoHash, keyword, 50);
		
		try {
			// Open a HTTP connection between your Java application and TicketMaster based URL
			HttpURLConnection connection = 
					(HttpURLConnection) new URL(URL + "?" + query).openConnection();
			
			// Set request method to GET
			connection.setRequestMethod("GET");
			
			// Send request to TicketMaster and get response, response code could be returned directly
			// response body is saved in inputStream of connection
			int responseCode = connection.getResponseCode();
			
			System.out.println("\nSending 'GET' request to URL: " + URL + "?" + query);
			System.out.println("Response code: " + responseCode);
			
			// Now read response body to get events data
			StringBuilder response = new StringBuilder();
			try(BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()))){
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}
			
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) {
				return new JSONArray();
			}
			
			JSONObject embedded = obj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			
			return events;
			
			
		} catch (Exception e) {
			e.getStackTrace();
		}
		
		return new JSONArray();
	}
	
	// Test if search can work
	private void queryAPI(double lat, double lon) {
		JSONArray events = search(lat, lon, null);
		try {
			for (int i = 0; i < events.length(); ++i) {
				JSONObject event = events.getJSONObject(i);
				System.out.println(event);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}

}
