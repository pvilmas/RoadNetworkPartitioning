package bp.roadnetworkpartitioning;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingDialogController extends Dialog<Boolean> {

    private final APartitionAlgorithm algorithm;

    private final Map<Label, TextField> parameters = new HashMap<>();

    @FXML
    private ButtonType settingButtonType;

    @FXML
    private VBox vBox;

    @FXML
    private Button btnAddParameter;

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
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onApplyAndCloseButtonClick(ActionEvent event){
        if (this.algorithm.getParameters() != null) {
            for (Map.Entry<Label, TextField> parameter: this.parameters.entrySet()) {
                this.algorithm.getParameters().put(parameter.getKey().getText(), parameter.getValue().getText());
            }
        }
    }

    private void addParameterHBox(Map.Entry<String, String> parameter, String parameterDescription) {
        VBox valueBox1 = new VBox(3);
        Label labelNameName = new Label("Parameter Name:");
        Label labelName = new Label(parameter != null ? parameter.getKey() : "");
        valueBox1.getChildren().addAll(labelNameName, labelName);
        VBox valueBox2 = new VBox(3);
        Label labelNameValue = new Label("Value:");
        TextField fieldValue = new TextField(parameter != null ? parameter.getValue() : "");
        valueBox2.getChildren().addAll(labelNameValue, fieldValue);
        VBox valueBox3 = new VBox(3);
        Label labelNameDescription = new Label("Detailed Description:");
        Label labelDescription = new Label(parameterDescription != null ? parameterDescription : "");
        valueBox3.getChildren().addAll(labelNameDescription, labelDescription);
        parameters.put(labelName, fieldValue);
        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(valueBox1, valueBox2, valueBox3);
        vBox.getChildren().add(hBox);
    }

    private void showParameters() {
        if (this.algorithm.getParameters() != null) {
            for (Map.Entry<String, String> parameter : this.algorithm.getParameters().entrySet()) {
                String parameterDescription = this.algorithm.getParametersDescription() != null ?
                        this.algorithm.getParametersDescription().get(parameter.getKey()) : null;
                addParameterHBox(parameter, parameterDescription);
            }
        }
    }
}
