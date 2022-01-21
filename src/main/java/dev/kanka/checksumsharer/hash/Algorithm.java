package dev.kanka.checksumsharer.hash;

/**
 * keeps supported algorithms for calculating checksums
 */
public enum Algorithm {
    // TODO: implement more Algorithms like MD5?
    SHA_256("SHA-256"),
    SHA_512("SHA-512"),
    SHA3_384("SHA3-384"),
    SHA3_512("SHA3-512");

    private String algorithm;

    Algorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public java.lang.String toString() {
        return this.algorithm;
    }
}
