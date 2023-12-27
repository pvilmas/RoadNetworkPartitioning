package bp.roadnetworkpartitioning;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Class that serves as a controller for dialog with algorithm setting.
 * @author Lucie Roy
 * @version 27-12-2023
 */
public class SettingDialogController extends Dialog<Boolean> {
    /** Algorithm whose parameters are going to be set up. */
    private final APartitionAlgorithm algorithm;
    /** Mapping of label with parameter name and text field with parameter value. */
    private final Map<Label, TextField> parameters = new HashMap<>();
    /** Main button of the dialog with parameters setting. */
    @FXML
    private ButtonType settingButtonType;
    /** VBox containing all parameters. */
    @FXML
    private VBox vBox;

    /**
     * Constructor of dialog for algorithm parameters setting with given stage/window and the algorithm instance for dialog.
     * @param window        stage/window hosting dialog.
     * @param algorithm     algorithm whose parameters are going to be set up.
     * @throws IOException  when loading fxml.
     */
    public SettingDialogController(Window window, APartitionAlgorithm algorithm) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("setting-dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        dialogPane.lookupButton(settingButtonType).addEventFilter(ActionEvent.ANY, this::onApplyAndCloseButtonClick);
        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        setTitle("Parameters setting of the algorithm");
        setHeaderText("Insert parameters of the algorithm:");
        setDialogPane(dialogPane);
        setResultConverter(buttonType -> {
            if(!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                return null;
            }
            return true;
        });
        this.algorithm = algorithm;
        showParameters();
    }

    /**
     * Saves changes when button "Apply and close" is clicked.
     */
    @FXML
    protected void onApplyAndCloseButtonClick(ActionEvent event){
        if (this.algorithm.getParameters() != null) {
            for (Map.Entry<Label, TextField> parameter: this.parameters.entrySet()) {
                this.algorithm.getParameters().put(parameter.getKey().getText(), parameter.getValue().getText());
            }
        }
    }

    /**
     * Adds one parameter info box.
     * @param parameter             parameter to be displayed.
     * @param parameterDescription  detailed description of the parameter.
     */
    private void addParameterHBox(Map.Entry<String, String> parameter, String parameterDescription) {
        VBox valueBox1 = new VBox(3);
        Label labelNameName = new Label("Parameter Name:");
        Label labelName = new Label(parameter != null ? parameter.getKey() : "");
        valueBox1.getChildren().addAll(labelNameName, labelName);
        valueBox1.setAlignment(Pos.CENTER);
        VBox valueBox2 = new VBox(3);
        Label labelNameValue = new Label("Value:");
        TextField fieldValue = new TextField(parameter != null ? parameter.getValue() : "");
        valueBox2.getChildren().addAll(labelNameValue, fieldValue);
        valueBox2.setAlignment(Pos.CENTER);
        VBox valueBox3 = new VBox(3);
        Label labelNameDescription = new Label("Detailed Description:");
        Label labelDescription = new Label(parameterDescription != null ? parameterDescription : "");
        valueBox3.getChildren().addAll(labelNameDescription, labelDescription);
        valueBox3.setAlignment(Pos.CENTER);
        parameters.put(labelName, fieldValue);
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(valueBox1, valueBox2, valueBox3);
        vBox.getChildren().add(hBox);
    }

    /**
     * Loads and displays parameters of the algorithm.
     */
    private void showParameters() {
        if (this.algorithm.getParameters() != null && this.algorithm.getParameters().size() > 0) {
            for (Map.Entry<String, String> parameter : this.algorithm.getParameters().entrySet()) {
                String parameterDescription = this.algorithm.getParametersDescription() != null ?
                        this.algorithm.getParametersDescription().get(parameter.getKey()) : null;
                addParameterHBox(parameter, parameterDescription);
            }
        }
        else {
            Label noParameters = new Label("No Parameters Available");
            noParameters.setAlignment(Pos.CENTER);
            vBox.getChildren().add(noParameters);
        }
    }
}
