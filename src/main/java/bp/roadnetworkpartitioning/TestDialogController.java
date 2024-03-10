package bp.roadnetworkpartitioning;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
public class TestDialogController extends Dialog<Boolean> {
    /** Algorithm whose parameters are going to be set up. */
    private final Map<String, APartitionAlgorithm> algorithms;
    private final Graph graph;
    private final int partCount;
    /** Main button of the dialog with parameters setting. */
    @FXML
    private ButtonType testButtonType;
    /** VBox containing all parameters. */
    @FXML
    private VBox vBox;
    @FXML
    private Spinner<Integer> spinnerRoundCount;
    @FXML
    private CheckBox createCSVStatisticFile;
    @FXML
    private CheckBox exportResultingPartitions;
    @FXML
    private Button startTestingButton;
    @FXML
    private TextArea progressMessages;

    /**
     * Constructor of dialog for algorithm parameters setting with given stage/window and the algorithm instance for dialog.
     * @param window        stage/window hosting dialog.
     * @throws IOException  when loading fxml.
     */
    public TestDialogController(Window window, Map<String, APartitionAlgorithm> algorithms, Graph graph, int partCount) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("test-dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        dialogPane.lookupButton(testButtonType).addEventFilter(ActionEvent.ANY, this::onCloseButtonClick);
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
        this.algorithms = algorithms;
        this.graph = graph;
        this.partCount = partCount;
        startTestingButton.setOnAction(e -> onStartTestingButtonClick());
    }

    private void onStartTestingButtonClick() {
        for(APartitionAlgorithm algorithm : algorithms.values()) {
            for(int i = 0; i < spinnerRoundCount.getValue(); i++) {
                GraphPartition graphPartition = algorithm.getGraphPartition(graph, partCount);
                if (createCSVStatisticFile.isSelected()) {
                    addToStatistics(algorithm, graphPartition);
                }
                if (exportResultingPartitions.isSelected()) {
                    exportResultingPartition(algorithm, graphPartition);
                }
            }
        }
    }

    private void exportResultingPartition(APartitionAlgorithm algorithm, GraphPartition graphPartition) {
    }

    private void addToStatistics(APartitionAlgorithm algorithm, GraphPartition graphPartition) {
    }

    private <T extends Event> void onCloseButtonClick(T t) {
    }

}
