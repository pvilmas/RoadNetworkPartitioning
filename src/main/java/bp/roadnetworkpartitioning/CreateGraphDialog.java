package bp.roadnetworkpartitioning;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that serves as a controller for dialog with graph creation.
 *  @author Lucie Roy
 *  @version 27-03-2023
 */
public class CreateGraphDialog extends Dialog<Graph> {

    /** Main button of dialog creating graph. */
    @FXML
    private ButtonType createButtonType;
    /** TextField with number of vertices vertically in generated graph. */
    @FXML
    private TextField textFieldHorizontally;
    /** TextField with number of vertices horizontally in generated graph. */
    @FXML
    private TextField textFieldVertically;
    /** TextField with length of graph edges that will be generated. */
    @FXML
    private TextField textFieldLength;
    /** Created graph. */
    private Graph graph = null;

    /**
     * Constructor of dialog for graph generation with given stage/window for dialog.
     * @param window        stage/window hosting dialog.
     * @throws IOException  when loading fxml.
     */
    public CreateGraphDialog(Window window) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("create-graph-dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        dialogPane.lookupButton(createButtonType).addEventFilter(ActionEvent.ANY, this::onCreateGraphButtonClick);
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
            return this.graph;
        });

        setOnShowing(dialogEvent -> Platform.runLater(() -> textFieldHorizontally.requestFocus()));
    }

    /**
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     * @param event triggering event.
     */
    @FXML
    protected void onCreateGraphButtonClick(ActionEvent event){
        if(!textFieldHorizontally.getText().isBlank() && !textFieldVertically.getText().isBlank()
            && !textFieldLength.getText().isBlank()){
            int verticesHorizontally = MainController.getNumberFromString(textFieldHorizontally.getText());
            int verticesVertically = MainController.getNumberFromString(textFieldVertically.getText());
            int edgeLength = MainController.getNumberFromString(textFieldLength.getText());
            this.graph = Graph.generateGraph(verticesHorizontally, verticesVertically, edgeLength);
        } else {
            event.consume();
        }
    }
}
