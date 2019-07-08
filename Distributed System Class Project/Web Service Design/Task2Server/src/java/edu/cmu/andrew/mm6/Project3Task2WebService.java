/**
 * Project3Task2 uses single argument for remote procedure call. 
 * This is the Server class.
 * @author Zirui Zheng
 * @andrewID ziruizhe
 */
package edu.cmu.andrew.mm6;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

@WebService(serviceName = "Project3Task1WebService")
public class Project3Task2WebService {

    public int index = 0; // the counter for new Block objects
    public BlockChain blockChain = new BlockChain(); // create a new instance for further operations

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

    /**
     * Web service operation
     * @param operand - contains information of this operation
     * @return a string that contains all the results a client wants
     */
    @WebMethod(operationName = "operation")
    public String operation(@WebParam(name = "operand") String operand) {
        String[] operArray = operand.split(","); // seperate the operand for processing
        String operID = operArray[0].trim(); // extract the operation id
        String[] result = new String[2]; // create a string array to store all the results
        if (operID.equals("1")) { // add transaction
            String data = operArray[1].trim();
            int difficulty = Integer.parseInt(operArray[2].trim());
            addTransaction(data, difficulty); // find the corresponding add transaction procedure on server
        }else if (operID.equals("2")) {
            result[0] = Boolean.toString(verifyBlockChain()); // find the corresponding verify procedure on server
        }else if (operID.equals("3")) {
            result[1] = viewBlockChain(); // find the corresponding view procedure on server
        }
        return String.join("%%%", result); // return the string that contains verify and view information
    }

}

