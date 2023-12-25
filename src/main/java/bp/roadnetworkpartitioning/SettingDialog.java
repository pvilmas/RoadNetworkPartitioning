package bp.roadnetworkpartitioning;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

public class SettingDialog extends Dialog<Boolean> {
    // TODO this method
    @FXML
    private ButtonType settingButtonType;

    public SettingDialog(Window window, APartitionAlgorithm algorithm) throws IOException {
        // TODO this method
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("setting-dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        dialogPane.lookupButton(settingButtonType).addEventFilter(ActionEvent.ANY, this::onCreateGraphButtonClick);
        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        setTitle("Graph Generation");
        setHeaderText("Insert parameters of new graph:");
        setDialogPane(dialogPane);
        setResultConverter(buttonType -> {
            if(!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                return null;
            }
            return true;
        });

        //setOnShowing(dialogEvent -> Platform.runLater(() -> textFieldHorizontally.requestFocus()));
    }

    /**
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onCreateGraphButtonClick(ActionEvent event){
        // TODO this method
        event.consume();

    }
}
