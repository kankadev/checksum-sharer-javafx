package dev.kanka.checksumsharer.enums;

public enum ResourceBundles {

    GENERAL("dev/kanka/checksumsharer/general"),
    SETTINGS("dev/kanka/checksumsharer/settings"),
    MESSAGES("dev/kanka/checksumsharer/messages");

    private String bundleName;

    ResourceBundles(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleName() {
        return bundleName;
    }

    @Override
    public String toString() {
        return bundleName;
    }
}
