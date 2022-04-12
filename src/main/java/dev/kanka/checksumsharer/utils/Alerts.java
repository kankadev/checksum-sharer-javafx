package dev.kanka.checksumsharer.utils;

import javafx.scene.control.Alert;

/**
 * Util class for Alerts
 */
public class Alerts {

    /**
     *
     * @param windowTitle
     * @param header
     * @param description
     * @return Alert
     */
    public static Alert error(String windowTitle, String header, String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(description);
        return alert;
    }

    /**
     *
     * @param windowTitle
     * @param header
     * @param description
     * @return
     */
    public static Alert conformation(String windowTitle, String header, String description) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(description);
        return alert;
    }
}
