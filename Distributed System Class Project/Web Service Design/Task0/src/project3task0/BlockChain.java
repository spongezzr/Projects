/**
 * The BlockChain class implemented with the help of the doc.
 * @author Zirui Zheng
 * @AndrewID ziruizhe
 */
package project3task0;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author michaelz
 */
public class BlockChain {

    private ArrayList<Block> blockArray; //an ArrayList to hold Blocks.
    private String chainHash; //a chain hash to hold a SHA256 hash of the most recently added Block.

    public static void main(String[] args) {

        int index = 0; // the counter for new Block objects
        BlockChain blockChain = new BlockChain(); // create a new instance for further operations
        Block genesis = new Block(index++, blockChain.getTime(), "Genesis", 2); // create the defaulted first Block
        blockChain.addBlock(genesis); // add the genesis Block into the chain
        Scanner sc = new Scanner(System.in); // open a scanner for keyboard input

        // only exit the loop when user chooses to exit
        // use helper method for separation for concerns
        while (true) {

            // show the menu every time
            setUpMenu();

            // get user's option
            String in = "";
            while (sc.hasNext()) {
                in = sc.nextLine().trim();
                if (!in.equals("")) {
                    break;
                }
            }

            // user inputs options from 0-5, switch cases
            switch (in) {
                
                // 0. View basic blockchain status
                case "0":
                    viewBasic(blockChain);
                    break;

                // 1. Add a transaction to the blockchain.
                case "1":
                    
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

                    // call the helper method.
                    addTransaction(blockChain, index, data, difficulty);
                    index++;
                    break;

                // 2. Verify the blockchain.
                case "2":
                    verifyBlockChain(blockChain);
                    break;

                // 3. View the blockchain.
                case "3":
                    viewBlockChain(blockChain);
                    break;

                // 4. Corrupt the chain.
                case "4":
                    
                    // get Block ID and data from user's input
                    System.out.println("Corrupt the Blockchain");
                    System.out.println("Enter block ID of block to Corrupt");
                    int id = sc.nextInt();
                    System.out.println("Enter new data for block " + id);
                    data = "";
                    while (sc.hasNextLine()) {
                        data = sc.nextLine().trim();
                        if (!data.equals("")) {
                            break;
                        }
                    }

                    // call the helper method
                    corrupt(blockChain, id, data);
                    break;

                // 5. Hide the Corruption by repairing the chain.
                case "5":
                    hideCorruption(blockChain);
                    break;

                // 6. Exit.
                case "6":
                    return;

                // if anything else popped up, we ask the user to input again.
                default:
                    System.out.println("Please enter a valid number between 0 and 6");
            }
        }

    }

    /**
     * A helper method to print out menu.
     */
    private static void setUpMenu() {
        System.out.println("0. View basic blockchain status.");
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the block chian.");
        System.out.println("3. View the blockchain.");
        System.out.println("4. Corrupt the chain.");
        System.out.println("5. Hide the Corruption by repairing the chain.");
        System.out.println("6. Exit.");
    }

    /**
     * A helper method to view basic blockChain status.
     * @param blockChain - current blockChain
     */
    private static void viewBasic(BlockChain blockChain) {
        System.out.println("Current size of chain: " + blockChain.getChainSize());
        System.out.println("Current hashes per second by this machine: " + blockChain.hashesPerSecond());
        System.out.println("Difficulty of most recent block: " + blockChain.getLatestBlock().getDifficulty());
        System.out.println("Nonce for most recent block: " + blockChain.getLatestBlock().getNonce());
        System.out.println("Chain hash:");
        System.out.println(blockChain.chainHash);      
    }

    /**
     * A helper method to add a transaction to the blockChain.
     * @param blockChain - current blockChain
     * @param index - the index of newly added Block
     * @param data - the data for the newly added Block
     * @param difficulty - the difficulty for the newly added Block
     */
    private static void addTransaction(BlockChain blockChain, int index, String data, int difficulty) {
        
        long startTime = System.currentTimeMillis(); // record start time
        blockChain.addBlock(new Block(index, blockChain.getTime(), data, difficulty));
        long endTime = System.currentTimeMillis(); // record end time
        System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
    }

    /**
     * A helper method to verify the blockChain.
     * @param blockChain - current blockChain
     */
    private static void verifyBlockChain(BlockChain blockChain) {
        System.out.println("Verifying entire chain");
        long startTime = System.currentTimeMillis(); // record start time
        int[] array = new int[2];
        boolean verify = blockChain.isChainValid(array); // verify the blockChain
        if (!verify) { // if verification failed, print out the mal node information
            int id = array[0];
            int difficulty = array[1];
            
            // generate the "0" string the represents level of difficulty
            String zeros = "";
            while(difficulty > 0) {
                zeros += "0";
                difficulty--;
            }
            System.out.printf("..Improper hash on node %d Does not begin with %s%n", id, zeros);
        }
        System.out.println("Chain verification: " + verify);
        long endTime = System.currentTimeMillis(); // record end time
        System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
    }

    /**
     * A helper method to view the blockChain.
     * @param blockChain - current blockChain
     */
    private static void viewBlockChain(BlockChain blockChain) {
        System.out.println("View the Blockchain");
        System.out.println(blockChain.toString());
    }

    /**
     * A helper method for a user to corrupt the chain.
     * @param blockChain - current blockChain
     * @param id - the Block to change
     * @param data - the content to change
     */
    private static void corrupt(BlockChain blockChain, int id, String data) {
        blockChain.blockArray.get(id).setData(data);
        System.out.printf("Block %d now holds %s%n", id, data);
    }

    /**
     * A helper method to re-compute the hashes so that validation will be true.
     * @param blockChain - current blockChain 
     */
    private static void hideCorruption(BlockChain blockChain) {
        System.out.println("Repairing the entire chain");
        long startTime = System.currentTimeMillis(); // record start time
        blockChain.repairChain();
        long endTime = System.currentTimeMillis(); // record end time
        System.out.println("Total execution time to repair the chain was " + (endTime - startTime) + " milliseconds");
    }

    /**
     * This constructor creates an empty ArrayList for Block storage. This
     * constructor sets the chain hash to the empty string.
     */
    public BlockChain() {
        blockArray = new ArrayList<>();
        chainHash = "";
    }

    /**
     * Simple getter method.
     *
     * @return the current system time
     */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * A method to get the most recent Block.
     *
     * @return a reference to the most recently added Block.
     */
    public Block getLatestBlock() {
        return blockArray.get(blockArray.size() - 1);
    }

    /**
     * Get the size of the chain in blocks.
     *
     * @return the size of the chain in blocks.
     */
    public int getChainSize() {
        return blockArray.size();
    }

    /**
     * Use a sample code to hash and find the computer hashing speed.
     *
     * @return hashes per second of the computer holding this chain. It uses a
     * simple string - "00000000" to hash.
     */
    public int hashesPerSecond() {
        String sample = "00000000";

        // record teh start time
        long startTime = System.currentTimeMillis();

        int count = 0;
        
        while (System.currentTimeMillis() - startTime <= 1000) {
            // hash the sample string
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.digest(sample.getBytes());
            } catch (NoSuchAlgorithmException e) {
                System.out.print(e.getMessage());
            }
            count++;
        }
        return count;
    }

    /**
     * A new Block is being added to the BlockChain. This new block's previous
     * hash must hold the hash of the most recently added block. After this call
     * on addBlock, the new block becomes the most recently added block on the
     * BlockChain.
     *
     * @param newBlock - is added to the BlockChain as the most recent block
     */
    public void addBlock(Block newBlock) {

        // update the previousHash if there's at least a Block in the array list
        if (blockArray.size() > 0) {
            newBlock.setPreviousHash(chainHash);
        }
        blockArray.add(newBlock);

        // update the chianHash with the newly added Block
        chainHash = blockArray.get(blockArray.size() - 1).proofOfWork();
    }

    @Override
    /**
     * This method uses the toString method defined on each individual block.
     *
     * @return a String representation of the entire chain is returned.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ds_chain\" : [");

        // append the all the Block objects
        for (int i = 0; i < blockArray.size(); i++) {
            sb.append(blockArray.get(i).toString());
            if (i < blockArray.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("], ");

        // append the 
        sb.append("\"chainHash\":\"").append(chainHash).append("\"}");
        return sb.toString();
    }

    /**
     * If the chain only contains one block, the genesis block at position 0,
     * this routine computes the hash of the block and checks that the hash has
     * the requisite number of leftmost 0's (proof of work) as specified in the
     * difficulty field.
     * @param index an array to store the node information (index, difficulty) if verification failed
     * @return true if and only if the chain is valid
     */
    public boolean isChainValid(int[] index) {

        // Arbitrarily set that we return true if there's no Block
        if (blockArray.isEmpty()) {
            return true;
        }
        String prevHash = "";

        for (int i = 0; i < blockArray.size(); i++) {

            // scan each Block in the arraylist
            Block cur = blockArray.get(i);
            int curDifficulty = cur.getDifficulty();
            String hexString = cur.calculateHash();
            char[] hexArr = hexString.toCharArray();

            // check if leading zeros match the difficulty
            for (int j = 0; j < curDifficulty; j++) {
                if (hexArr[j] != '0') {
                    // if false, return the correspondant node info
                    index[0] = i;
                    index[1] = curDifficulty;
                    return false;
                }
            }

            // if there's only one block, we finish the check
            if (blockArray.size() == 1) {
                return true;
            }

            // check if previous hashes match
            if (i != 0) {
                if (!prevHash.equals(cur.getPreviousHash())) {
                    // if false, return the correspondant node info
                    index[0] = i;
                    index[1] = curDifficulty;
                    return false;
                }
            }
            
            // change the prevHash to the current hashed string
            prevHash = hexString;
        }
        return true;
    }

    /**
     * This routine repairs the chain. It checks the hashes of each block and
     * ensures that any illegal hashes are recomputed. After this routine is
     * run, the chain will be valid. The routine does not modify any difficulty
     * values. It computes new proof of work based on the difficulty specified
     * in the Block.
     */
    public void repairChain() {

        if (blockArray.isEmpty()) {
            return;// Arbitrarily return if the chain is empty
        }
        boolean reCompute = false;// A flag to show if we needs to recompute hashes

        String prevHash = "";
        int curIndex = 0;

        for (int i = 0; i < blockArray.size(); i++) {

            // scan each Block in the arraylist
            Block cur = blockArray.get(i);
            int curDifficulty = cur.getDifficulty();
            String hexString = cur.calculateHash();
            char[] hexArr = hexString.toCharArray();

            // check if leading zeros match the difficulty
            for (int j = 0; j < curDifficulty; j++) {
                if (hexArr[j] != '0') {
                    reCompute = true;
                    break;
                }
            }

            // if there's only one block, we only check this one 
            if (blockArray.size() == 1) break;
            
            if (reCompute) {
                // if first check failed, start recompute with current index
                curIndex = i;
                break;
            }

            // check if previous hashes match 
            if (!prevHash.equals(cur.getPreviousHash())) {
                
                // if not match, break, and start with the current index
                reCompute = true;
                curIndex = i;
                break;
            }
            
            // set prevHash to current hashed string to continue the loop
            prevHash = hexString;
        }

        // if all the check passes, we do not need to do anything
        if (!reCompute) {
            return;
        }

        // from the current index to the end, we recompute the hash
        for (int j = curIndex; j < blockArray.size(); j++) {
            
            // if there's only one Block, we do not set the prevHash
            if (j == 0) {
                prevHash = blockArray.get(j).proofOfWork();
                continue;
            }
            
            // recompute the hash
            Block cur = blockArray.get(j);
            cur.setPreviousHash(prevHash);
            prevHash = cur.proofOfWork();
        }
    }

}
