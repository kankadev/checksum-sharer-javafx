package dev.kanka.checksumsharer.tabs;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.Section;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ViewMixin;
import dev.kanka.checksumsharer.enums.ResourceBundles;
import dev.kanka.checksumsharer.models.Settings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsTabController implements Initializable, ViewMixin {

    private static final Logger logger = LogManager.getLogger();

    private final ResourceBundle rbEn = ResourceBundle.getBundle(ResourceBundles.SETTINGS.getBundleName(), new Locale("en", "UK"));
    private final ResourceBundle rbDe = ResourceBundle.getBundle(ResourceBundles.SETTINGS.getBundleName(), new Locale("de", "DE"));
    private final ResourceBundleService rbs = new ResourceBundleService(rbEn);

    private final Settings settings = new Settings();
    private Form formInstance;
    private FormRenderer formRenderer;

    private HBox buttonsBox;
    private Button saveButton;
    private Button resetButton;

    @FXML
    VBox vBox;

    public Form getFormInstance() {
        if (formInstance == null) {
            createForm();
        }
        return formInstance;
    }

    private void createForm() {
        formInstance = Form.of(
                Group.of(
                        Field.ofSingleSelectionType(settings.allLanguagesProperty(), settings.languageProperty())
                                .label("language.label")
                                .labelDescription("language.desc"),
                        Field.ofSingleSelectionType(settings.allDateFormatsProperty(), settings.dateFormatProperty())
                                .label("date_format.label")
                                .labelDescription("date_format.desc")
                ),
                Section.of(
                        Field.ofStringType(settings.exportLocationProperty())
                                .label("export_location.label")
                                .labelDescription("export_location.desc")
                ).title("export.section")
        ).title("settings").i18n(rbs);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    @Override
    public void initializeParts() {
        buttonsBox = new HBox();
        saveButton = new Button(rbs.translate("save_button.text"));
        saveButton.getStyleClass().addAll("btn", "btn-primary");
        resetButton = new Button(rbs.translate("reset_button.text"));
        resetButton.getStyleClass().addAll("btn", "btn-warning");

        formRenderer = new FormRenderer(getFormInstance());
    }

    @Override
    public void setupBindings() {
        saveButton.disableProperty().bind(formInstance.persistableProperty().not());
        resetButton.disableProperty().bind(formInstance.changedProperty().not());
    }

    @Override
    public void setupValueChangedListeners() {

    }

    @Override
    public void setupEventHandlers() {
        saveButton.setOnAction(event -> {
            formInstance.persist();
            changeLanguage(settings.getLanguage());
        });
        resetButton.setOnAction(event -> formInstance.reset());
    }

    @Override
    public void layoutParts() {
        buttonsBox.setSpacing(20);
        buttonsBox.getChildren().addAll(saveButton, resetButton);
        vBox.getChildren().addAll(formRenderer, buttonsBox);
    }

    /**
     * Sets the locale of the form.
     *
     * @param language The language identifier for the new locale.
     */
    public void changeLanguage(String language) {
        switch (language) {
            case "Deutsch":
                rbs.changeLocale(rbDe);
                break;
            default:
                rbs.changeLocale(rbEn);
        }
    }

    @Override
    public List<String> getStylesheets() {
        return null;
    }
}
