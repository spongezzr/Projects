/**
 * Project3Task2 uses single argument for remote procedure call. 
 * This is the Client class.
 * @author Zirui Zheng
 * @andrewID ziruizhe
 */
package project3task2client;

import java.util.Scanner;

public class Project3Task2Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        start();
    }

    // for separate concerns
    private static void start() {
        Scanner sc = new Scanner(System.in); // open a scanner for keyboard input
        // only exit the loop when user chooses to exit
        // use helper method for separation for concerns
        while (true) {

            setUpMenu(); // show the menu every time
            String[] operand = new String[3]; // initialize a string array to store all the info for operation

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

                    // input new transaction information
                    operand[0] = "1";
                    operand[1] = data;
                    operand[2] = Integer.toString(difficulty);
                    break;

                case "2":  // 2. Verify the blockchain.
                    operand[0] = "2";
                    break;

                case "3": // 3. View the blockchain.
                    operand[0] = "3";
                    break;

                case "6": // 6. Exit.
                    return;

                default: // if anything else popped up, we ask the user to input again.
                    System.out.println("Please enter a valid number.");
            }

            callOperation(operand);

        }
    }

    /**
     * A method to call the operation method. It is designed for separation of
     * concerns.
     *
     * @param operand the string array that stores all the information for
     * operation
     */
    private static void callOperation(String[] operand) {
        String operandString = String.join(",", operand);
        long startTime = System.currentTimeMillis(); // record start time
        String result = operation(operandString); // call the remote method operation
        long endTime = System.currentTimeMillis(); // record end time
        String[] resultArr = result.split("%%%");

        switch (operand[0]) {    // user inputs options from 1,2,3, switch cases

            case "1": // 1. Add a transaction to the blockchain.

                System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                break;

            case "2":  // 2. Verify the blockchain.
                System.out.println("Verifying entire chain");
                System.out.println("Chain verification: " + resultArr[0]);
                System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                break;

            case "3": // 3. View the blockchain.
                System.out.println("View the Blockchain");
                System.out.println(resultArr[1]);
                break;
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
     * A method to call remote operation method.
     *
     * @param operand the string that contains all the information for the
     * operation, separated by ","
     * @return a string that contains all the information for the client,
     * separated by ","
     */
    private static String operation(java.lang.String operand) {
        edu.cmu.andrew.mm6.Project3Task1WebService service = new edu.cmu.andrew.mm6.Project3Task1WebService();
        edu.cmu.andrew.mm6.Project3Task2WebService port = service.getProject3Task2WebServicePort();
        return port.operation(operand);
    }

}
