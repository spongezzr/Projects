/**
 * Project3Task3, repeat the former tasks with REST APIs. 
 * This class is the Client.
 * 
 * I was considering using doPost method, but the instruction says "this differs from task2 that...",
 * which in my mind indicates that we should still use the one single argument.
 * Hence, I pass a string and use doGet to perform all the operations.
 * 
 * @author Zirui Zheng
 * @andrewID ziruizhe
 */
package project3task3client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Project3Task3Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        start();
    }
    
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
                    operand[1] = String.join("+", data.split(" ")); // replace space with plus sign
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
     * A method to call the operation method.
     * It is designed for separation of concerns. 
     * @param operand the string array that stores all the information for operation
     */
    private static void callOperation(String[] operand) {
        String operandString = String.join(",", operand);
        long startTime = System.currentTimeMillis(); // record start time
        Result r = new Result();
        doGet(operandString, r); // call the remote method operation
        long endTime = System.currentTimeMillis(); // record end time
        String[] resultArr = r.getValue().split("%%%");

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

    // A simple class to wrap a result.
    static class Result {

        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static int doGet(String operand, Result r) {

        // Make an HTTP GET passing the name on the URL line
        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;

        try {

            // pass the name on the URL line
            URL url = new URL("http://localhost:8080/Project3Task3Server/Project3Task3/"+operand);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");
            
            // wait for response
            status = conn.getResponseCode();

            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                response += output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return value from server 
        // set the response object
        r.setValue(response);
        // return HTTP status to caller
        return status;
    }

}
