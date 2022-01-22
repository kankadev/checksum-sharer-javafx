package dev.kanka.checksumsharer.hash;

import dev.kanka.checksumsharer.models.KnkFile;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumCalculationTask extends Task<String> {

    private static final Logger logger = LogManager.getLogger();

    private final KnkFile knkFile;
    private final Algorithm algorithm;

    public ChecksumCalculationTask(KnkFile f, Algorithm algorithm) {
        this.knkFile = f;
        this.algorithm = algorithm;
    }

    private String getChecksumOfFile() throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm.toString());

        // Get knkFile input stream for reading the knkFile content
        try (FileInputStream fis = new FileInputStream(this.knkFile)) {
            // Create byte array to read data in chunks
            byte[] byteArray = new byte[8192];
            int bytesCount = 0;

            // Read knkFile data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        // bytes[] has bytes in decimal format;
        // Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    @Override
    public String call() throws Exception {
        return getChecksumOfFile();
    }


    public KnkFile getKnkFile() {
        return this.knkFile;
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }
}
