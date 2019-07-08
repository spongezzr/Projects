/**
 * Project3Task1 uses SOAP API. 
 * This is the Client class.
 * @author Zirui Zheng
 * @andrewID ziruizhe
 */
package project3task1client;

import java.util.Scanner;

public class Project3Task1Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in); // open a scanner for keyboard input
        // only exit the loop when user chooses to exit
        // use helper method for separation for concerns
        while (true) {

            setUpMenu(); // show the menu every time

            long startTime = 0; // initialize variable startTime
            long endTime = 0; // initialize variable endTime
            // get user's option
            String in = "";
            while (sc.hasNext()) {
                in = sc.nextLine().trim();
                if (!in.equals("")) {
                    break;
                }
            }

            switch (in) {    // user inputs options from 1,2,3,6, switch cases

                case "1": // 1. Add a transaction to the blockchain.
                    
                    // get difficulty and data from user's input
                    System.out.println("Enter difficulty > 0");
                    int difficulty = sc.nextInt();
                    System.out.println("Enter transaction");
                    String data = "";
                    while (sc.hasNextLine()) {
                        data = sc.nextLine().trim();
                        if (!data.equals("")) {
                            break;
                        }
                    }

                    startTime = System.currentTimeMillis(); // record start time
                    addTransaction(data, difficulty); // call the helper method.
                    endTime = System.currentTimeMillis(); // record end time
                    System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                    break;

                // 2. Verify the blockchain.
                case "2":
                    System.out.println("Verifying entire chain");
                    startTime = System.currentTimeMillis(); // record start time
                    System.out.println("Chain verification: " + verifyBlockChain());
                    endTime = System.currentTimeMillis(); // record end time
                    System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                    break;

                // 3. View the blockchain.
                case "3":
                    System.out.println("View the Blockchain");
                    System.out.println(viewBlockChain());
                    break;

                // 6. Exit.
                case "6":
                    return;

                // if anything else popped up, we ask the user to input again.
                default:
                    System.out.println("Please enter a valid number.");
            }
        }
    }
    
    // show the menu options
    private static void setUpMenu() {
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the block chian.");
        System.out.println("3. View the blockchain.");
        System.out.println("6. Exit.");
    }

        /**
     * A helper method call the remote method on server to add a transaction to the blockChain.
     * @param data - the data for the newly added Block
     * @param difficulty - the difficulty for the newly added Block
     */
    private static void addTransaction(java.lang.String data, int difficulty) {
        edu.cmu.andrew.mm6.Project3Task1WebService_Service service = new edu.cmu.andrew.mm6.Project3Task1WebService_Service();
        edu.cmu.andrew.mm6.Project3Task1WebService port = service.getProject3Task1WebServicePort();
        port.addTransaction(data, difficulty);
    }

    /**
     * A helper method call the remote method on server to verify the blockChain.
     * @return true if the blockChain is valid
     */
    private static boolean verifyBlockChain() {
        edu.cmu.andrew.mm6.Project3Task1WebService_Service service = new edu.cmu.andrew.mm6.Project3Task1WebService_Service();
        edu.cmu.andrew.mm6.Project3Task1WebService port = service.getProject3Task1WebServicePort();
        return port.verifyBlockChain();
    }

    /**
     * A helper method call the remote method on server to view the blockChain
     * @return a string representation of blockChain JSON
     */
    private static String viewBlockChain() {
        edu.cmu.andrew.mm6.Project3Task1WebService_Service service = new edu.cmu.andrew.mm6.Project3Task1WebService_Service();
        edu.cmu.andrew.mm6.Project3Task1WebService port = service.getProject3Task1WebServicePort();
        return port.viewBlockChain();
    }
}

