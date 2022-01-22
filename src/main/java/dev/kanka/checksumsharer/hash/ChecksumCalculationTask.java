package dev.kanka.checksumsharer.hash;

import dev.kanka.checksumsharer.models.KnkFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

public class ChecksumCalculationTask implements Callable<KnkFile> {

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
    public KnkFile call() throws Exception {
        String checksum = getChecksumOfFile();

        switch (this.algorithm) {
            case SHA_256 -> knkFile.setSha256(checksum);
            case SHA_512 -> knkFile.setSha512(checksum);
            case SHA3_384 -> knkFile.setSha3384(checksum);
            case SHA3_512 -> knkFile.setSha3512(checksum);
        }

        return this.knkFile;
    }
}
