
/**
 * This class serves as the Control in Project 4 Task 1.
 * It receives requests from android application or web browser and call the model to proceed.
 *
 * @author Zirui Zheng
 * @AndrewID ziruizhe
 */
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

// It takes all the requests beginning with "...AppServer/"
@WebServlet(name = "AppServer", urlPatterns = {"/AppServer/*"})
public class Project4Task1Servlet extends HttpServlet {

    // initialize the model
    Project4Task1Model m = new Project4Task1Model();

    // GET returns JSONObject of events found
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // The searchParam is on the path /name so skip over the '/'
        String searchParam = (request.getPathInfo()).substring(1);

        // return 401 if nothing is provided
        if (searchParam.equals("")) {
            response.setStatus(401);
            return;
        }

        // call the model to find events
        JSONArray events = m.search(searchParam);

        // return 401 if name not in map
        if (events == null || events.length() == 0) {
            // no variable name found in map
            response.setStatus(401);
            return;
        }

        // parse the string into a JSONObject
        JSONObject result = m.operation(events);
        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("application/json");

        // return the value from a GET request
        PrintWriter out = response.getWriter();
        out.println(result);
    }
}
