
/**
 * This class serves as the Control in Project 4 Task 2.
 * It receives requests from android application or web browser and call the model to proceed.
 *
 * @author Zirui Zheng
 * @AndrewID ziruizhe
 */
import java.sql.Timestamp;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

// use different urlPatterns to differentiate Dashboard from other requests
@WebServlet(name = "AppServer", urlPatterns = {"/AppServer/*", "/dashboard"})
public class Project4Task2Servlet extends HttpServlet {

    // initialize the model
    Project4Task2Model m = new Project4Task2Model();

    // GET returns JSONObject of events found
    // When dashboard request comes, GET displays the "data.jsp" view which shows all data
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ua = request.getHeader("User-Agent"); // get users' device information 
        boolean mobile = false; // check if the user is requesting from a mobile phone

        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.contains("Android")) || (ua.contains("iPhone")))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }

        // get the search parameter
        String searchParam = request.getPathInfo();

        // if there's no search parameter, it means the request is for a dashboard application
        if (searchParam == null || searchParam.equals("/")) {

            if (mobile) {
                return; // we don't allow mobile user to access our dashboard application
            }
            request.setAttribute("list", m.getDBClient().displayData()); // display the data

            // Transfer control over the the correct "view"
            RequestDispatcher view = request.getRequestDispatcher("data.jsp");
            view.forward(request, response);
            return;
        }

        // The searchParam is on the path /name so skip over the '/'
        searchParam = searchParam.substring(1);

        // restart the data recorder to store a new piece of data
        m.initializeRecorder();

        // store device type
        String device = (ua == null || !ua.contains("("))
                ? ua : ua.substring(0, ua.indexOf("("));
        m.getRecorder().append("request_device", device);

        // store the keyword for search
        String keyWord = searchParam.substring("keyWord=".length());
        m.getRecorder().append("client_request_keyWord", keyWord);

        // store the request time
        long request_time = System.currentTimeMillis();
        m.getRecorder().append("client_request_time", new Timestamp(request_time).toString());

        // call the model to find events
        JSONArray events = m.search(searchParam);

        // return 401 if no events found
        if (events == null || events.length() == 0) {
            response.setStatus(401);
        } else {

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
        // record end time and total process time
        long response_time = System.currentTimeMillis();
        m.getRecorder().append("client_response_time", new Timestamp(response_time).toString());
        m.getRecorder().append("total_process_time", response_time - request_time);

        // record other information from request and response
        m.record(request, response, "client");

        // send the data to the database
        m.getDBClient().recordData(m.getRecorder());
    }
}
