/**
 * The Block class implemented with the help of the doc.
 * @author Zirui Zheng
 * @AndrewID ziruizhe
 */
package edu.cmu.andrew.mm6;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.xml.bind.DatatypeConverter;

public class Block {
    
    private int index; //the position of the block on the chain. The first block (the so called Genesis block) has an index of 0.
    private Timestamp timestamp; //a Java Timestamp object, it holds the time of the block's creation.
    private String data; //a String holding the block's single transaction details.
    private String previousHash = ""; //the SHA256 hash of a block's parent. This is also called a hash pointer.
    private int difficulty; //it is an int that specifies the exact number of left most hex digits needed by a proper hash.
    private BigInteger nonce = new BigInteger("0");  //a BigInteger value determined by a proof of work routine
    
    /**
     * This the Block constructor.
     * @param index This is the position within the chain. Genesis is at 0.
     * @param timestamp This is the time this block was added.
     * @param data This is the transaction to be included on the blockChain.
     * @param difficulty This is the number of leftmost nibbles that need to be 0.
     */
    public Block(int index, Timestamp timestamp,String data,int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }
    
    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * @return a String holding Hexadecimal characters
     */
    public String calculateHash() {
        
        // concatenate the required strings
        String hashString = Integer.toString(index)+timestamp.toString()+data+previousHash+nonce.toString()+Integer.toString(difficulty);
        String hexString = "";
        
        // hash and return hex string
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(hashString.getBytes());
            hexString = DatatypeConverter.printHexBinary(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            System.out.print(e.getMessage());
        }
        return hexString;
    }

    /**
     * This method returns the nonce for this block. 
     * The nonce is a number that has been found to cause the hash of this block to have the correct number of leading hexadecimal zeroes.
     * @return a BigInteger representing the nonce for this block.
     */
    public BigInteger getNonce() {
        return nonce;
    }
    
    /**
     * The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
     * This method calls calculateHash() to compute a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * If the hash has the appropriate number of leading hex zeroes, it is done and returns that proper hash.
     * If the hash does not have the appropriate number of leading hex zeroes, it increments the nonce by 1 and tries again.
     * It continues this process, burning electricity and CPU cycles, until it gets lucky and finds a good hash.
     * @return a String with a hash that has the appropriate number of leading hex zeroes. 
     */
    public String proofOfWork() {
        String hex = "";
        boolean continueHash;
        
        // the loop operates the calculateHash() until a valid hash is found
        do {
            hex = calculateHash();
            
            // get char array to examine the hex string
            char[] arr = hex.toCharArray();
            continueHash = false;
            
            // for n difficulty, check the top n characters to be 0
            for (int i = 0; i < difficulty; i++) { 
                if (arr[i] != '0') {
                    continueHash = true;
                    nonce = nonce.add(new BigInteger("1")); // increment the nonce everytime with failed hash
                    break;
                }
            }
        } while (continueHash);
        return hex;
    }
  
    /**
     * Simple getter method.
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Simple setter method.
     * @param difficulty - determines how much work is required to produce a proper hash
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    /**
     * Override JAVA's toString method.
     * @return A JSON representation of all of this block's data is returned.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"index\" : ").append(index); //concat index
        sb.append(",");
        sb.append("\"time stamp \" : ").append("\"").append(timestamp.toString()).append("\""); //concat timestamp
        sb.append(",");
        sb.append("\"Tx \" : ").append("\"").append(data).append("\""); // concat transaction data
        sb.append(",");
        sb.append("\"PrevHash\" : ").append("\"").append(previousHash).append("\""); // concat previous hash
        sb.append(",");
        sb.append("\"nonce\" : ").append(nonce); // concat nonce
        sb.append(",");
        sb.append("\"difficulty\" : ").append(difficulty); // concat difficulty
        sb.append("}");
        return sb.toString();
    }

    /**
     * Simple setter method.
     * @param previousHash - a hash pointer to this block's parent
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    
    /**
     * Simple getter method.
     * @return previous hash
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Simple getter method.
     * @return index of block
     */
    public int getIndex() {
        return index;
    }

    /**
     * Simple setter method.
     * @param index - the index of this block in the chain
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Simple setter method.
     * @param timestamp - of when this block was created
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Simple getter method.
     * @return timestamp of this block
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Simple getter method.
     * @return this block's transaction
     */
    public String getData() {
        return data;
    }

    /**
     * Simple setter method.
     * @param data - represents the transaction held by this block
     */
    public void setData(String data) {
        this.data = data;
    }
}

