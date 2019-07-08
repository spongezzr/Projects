/**
 * The BlockChain class implemented with the help of the doc.
 * @author Zirui Zheng
 * @AndrewID ziruizhe
 */
package edu.cmu.andrew.mm6;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author michaelz
 */
public class BlockChain {

    private ArrayList<Block> blockArray; //an ArrayList to hold Blocks.
    private String chainHash; //a chain hash to hold a SHA256 hash of the most recently added Block.

    /**
     * This constructor creates an empty ArrayList for Block storage. This
     * constructor sets the chain hash to the empty string.
     */
    public BlockChain() {
        blockArray = new ArrayList<>();
        chainHash = "";
        Block genesis = new Block(0, getTime(), "Genesis", 2);
        addBlock(genesis);
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
     *
     * @return true if and only if the chain is valid
     */
    public boolean isChainValid() {

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
