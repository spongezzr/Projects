/**
 * Project3Task3, repeat the former tasks with REST APIs. 
 * This class is the server.
 * 
 * I was considering using doPost method, but the instruction says "this differs from task2 that...",
 * which in my mind indicates that we should still use the one single argument.
 * Hence, I pass a string and use doGet to perform all the operations.
 * 
 * @author Zirui Zheng
 * @andrewID ziruizhe
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// This example demonstrates Java servlets and HTTP
// This web service operates on string keys mapped to string values.
@WebServlet(name = "Project3Task3", urlPatterns = {"/Project3Task3/*"})
public class Project3Task3 extends HttpServlet {

    // This blockChain holds all the Blocks
    private BlockChain blockChain = new BlockChain();
    private int index = 0;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Console: doGET visited");

        String[] resultArr = new String[]{" ", " "};

        // get the operand
        String operand = request.getPathInfo().substring(1);
        
        // return 401 if operand is not valid
        if (operand == null || operand.isEmpty()) {
            response.setStatus(401);
            return;
        }
        
        // split the operand to get all the info for the operation
        String[] operArray = operand.split(",");
        
        String operID = operArray[0].trim();
        if (operID.equals("1")) { // add transaction
            System.out.println(operArray[1]);
            String data = operArray[1].trim(); 
            System.out.println(data);
            int difficulty = Integer.parseInt(operArray[2].trim());
            addTransaction(data, difficulty); // find the corresponding add transaction procedure on server
        }else if (operID.equals("2")) {
            resultArr[0] = Boolean.toString(verifyBlockChain()); // find the corresponding verify procedure on server
        }else if (operID.equals("3")) {
            resultArr[1] = viewBlockChain(); // find the corresponding view procedure on server
        }

        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("text/plain;charset=UTF-8");

        // return the value from a GET request
        String result = String.join("%%%", resultArr);
        PrintWriter out = response.getWriter();
        out.println(result);
    }
    
    /**
     * A helper method to add a transaction to the blockChain.
     * @param data - the data for the newly added Block
     * @param difficulty - the difficulty for the newly added Block
     */
    private void addTransaction(String data, int difficulty) {
        blockChain.addBlock(new Block(++index, blockChain.getTime(), data, difficulty));
    }

    /**
     * A helper method to verify the blockChain.
     * @return true if the blockChain is valid
     */
    private boolean verifyBlockChain() {
        return blockChain.isChainValid();
    }
   
    /**
     * A helper method to view the blockChain.
     * @return A string that is the JSON representation of the blockChain 
     */
    private String viewBlockChain() {
        return blockChain.toString();
    }

}
