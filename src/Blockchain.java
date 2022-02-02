
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;
import java.time.Instant;

public class Blockchain {

    ArrayList<Block> blockValues;
    public static int MINING_DIFFICULTY = 3;
    public static int SALT_LENGTH = 10;
    public static char HASH_STARTING_CHAR = '0';

    public Blockchain() throws NoSuchAlgorithmException {
        this.blockValues = new ArrayList<Block>();
        createRootBlock();
    }

    //    public static String generateStringValue(int _index, String _previous_hash, long _timestamp, String _data) {
    public void mineNew(String _data) throws NoSuchAlgorithmException {

        // Generate new values for the block.
        int index = blockValues.size();
        String previousHash = getFinalBlockHash();
        long timestamp = generateTimestamp();

        String dataString = Block.generateStringValue(index, _data, previousHash, timestamp);

        while (true) {
            String saltValue = Security.generateSalt(MINING_DIFFICULTY);
            String newHash = Security.generateHash(dataString, saltValue);
            if (isValidHash(newHash)) {
                createNewBlock(_data, newHash, saltValue);
                return;
            }
        }

    }

    public boolean isValidHash(String hash) {
        for (int i=0; i < MINING_DIFFICULTY; i++) {
            if (hash.charAt(i) != HASH_STARTING_CHAR) {
                return false;
            }
        }
        return true;
    }

    private void createRootBlock() throws NoSuchAlgorithmException {
        int index = 0;
        String data = "";
        long timestamp = generateTimestamp();
        String prevHash = "";

        while (true) {
            String salt = Security.generateSalt(SALT_LENGTH);
            String currentHash = Security.generateHash(data, salt);

            if (isValidHash(currentHash)) {
                Block rootBlock = new Block(index, data, timestamp, prevHash, currentHash, salt);
                blockValues.add(rootBlock);
                return;
            }
        }


    }

    public void createNewBlock(String _data, String _hash, String _salt) throws NoSuchAlgorithmException {
        int index = blockValues.size();
        String data = _data;
        long unixTimestamp = generateTimestamp();
        String prevHash = getFinalBlockHash();

        Block newBlock = new Block(index, data, unixTimestamp, prevHash, _hash, _salt);
        addNewBlock(newBlock);
    }

    private void addNewBlock(Block new_block) throws NoSuchAlgorithmException {
        this.blockValues.add(new_block);
    }

    private long generateTimestamp() {
        return Instant.now().getEpochSecond();
    }

    private static void handleException(Exception e) {
        String errorString = e.toString();
        String error_to_display = String.format("""
            BLOCKCHAIN CLASS ERROR:
            %s
        """, errorString);
        System.out.println(error_to_display);
    }

    public String getFinalBlockHash() {
        int lastIndex = blockValues.size() - 1;
        Block tailBlock = blockValues.get(lastIndex);
        String lastBlockHash = tailBlock.getHash();
        return lastBlockHash;
    }

    public boolean checkIsValid() {
        try {
            for (int i = 1; i < blockValues.size(); i++) {
                Block currentBlock = blockValues.get(i);
                Block prevBlock = blockValues.get(i - 1);

                if (currentBlock.getHash() != currentBlock.computeHash()) {
                    return false;
                }

                if (currentBlock.getPreviousHash() != prevBlock.getHash()) {
                    return false;
                }
            }

            return true;
        }
        catch (Exception e) {
            handleException(e);
            return false;
        }
    }



    public String toString() {
        String str_to_return = "";

        for (int i=0; i<blockValues.size()-1; i++) {
            str_to_return += blockValues.get(i).toString();
            str_to_return += "\t*\n\t*\n\t*\n";
        }
        str_to_return += blockValues.get(blockValues.size()-1);
        return str_to_return;
    }

}

class Block {

    static private final String HASHING_ALGORITHM = "SHA-256";
    static private final Charset TEXT_ENCODING = StandardCharsets.UTF_8;
    static private final int NUM_BYTES_FOR_SALT = 10;

    private int index;
    private long unixTimestamp;
    private String data;
    private String hash;
    private String previousHash;
    private String salt;

    public Block(int _index, String _data, long _unix_timestamp, String _prev_hash, String _current_hash, String _salt) throws NoSuchAlgorithmException {
        this.index = _index;
        this.data = _data;
        this.unixTimestamp = _unix_timestamp;
        this.previousHash = _prev_hash;
        this.hash = _current_hash;
        this.salt = _salt;
    }

    public static String generateStringValue(int _index, String _data, String _previous_hash, long _timestamp) {
        String str_to_return = String.valueOf(_index)
                + _data
                + _previous_hash
                + String.valueOf(_timestamp);
        return str_to_return;
    }

    public String computeHash() throws NoSuchAlgorithmException {
        String _hash = Security.generateHash(this.data, this.salt);
        return _hash;
    }

    public String getHash() {
        return this.hash;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public String toString() {
        String str_to_return = String.format("""
            +-------------------+
            | Index: %d
            | Hash: %s
            | PreviousHash: %s
            +-------------------+
        """, this.index, this.hash, this.previousHash);
        return str_to_return;
    }
}
