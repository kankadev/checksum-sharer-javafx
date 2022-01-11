package dev.kanka.checksumsharer.tabs;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import dev.kanka.checksumsharer.models.Settings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsTabController implements Initializable {


    @FXML
    VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Settings settings = new Settings();

        Form dateFormatForm = Form.of(
                Group.of(Field.ofSingleSelectionType(settings.getDateFormatProperty(), 0)
                        .label("Date Format")
                        .labelDescription("Select how the date format should be displayed.")
                        .render(new SimpleRadioButtonControl<>())
                )
        ).title("Date Format");

        vBox.getChildren().add(new FormRenderer(dateFormatForm));

    }
}
