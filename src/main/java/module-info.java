module dev.kanka.checksumsharer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires eu.hansolo.tilesfx;
    requires org.apache.logging.log4j;

    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens dev.kanka.checksumsharer to javafx.fxml;
    exports dev.kanka.checksumsharer;
    exports dev.kanka.checksumsharer.tabs;
    opens dev.kanka.checksumsharer.tabs to javafx.fxml;
}