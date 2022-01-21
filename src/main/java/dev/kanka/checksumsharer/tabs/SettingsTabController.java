package dev.kanka.checksumsharer.tabs;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import dev.kanka.checksumsharer.models.Settings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsTabController implements Initializable {

    @FXML
    VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Settings settings = new Settings();

        Form form = Form.of(
                Group.of(Field.ofSingleSelectionType(settings.languageProperty(), 0)
                                .label("Language")
                                .labelDescription("Select your language.")
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofSingleSelectionType(settings.dateFormatProperty(), 0)
                                .label("Date Format")
                                .labelDescription("Select how the date format should be displayed.")
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofStringType(settings.exportLocationProperty())
                                .label("Export Location")
                                .labelDescription("Select where to save the checksum files.")
                                .render(new SimpleTextControl())
                )
        ).title("Settings");

        Button saveButton = createSaveButton(form);

        vBox.getChildren().addAll(new FormRenderer(form), saveButton);

    }

    private Button createSaveButton(Form form) {
        Button btn = new Button("Save");

        btn.getStyleClass().addAll("btn", "btn-primary");

        btn.disableProperty().bind(form.changedProperty().not().and(form.validProperty()));

        btn.setOnAction(event -> {
            if (form.isValid()) {
                form.persist();
            }
        });

        return btn;
    }
}
