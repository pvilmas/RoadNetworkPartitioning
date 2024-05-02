package bp.roadnetworkpartitioning;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Map;


/**
 * Class that serves as a controller for dialog with algorithm setting.
 * @author Lucie Roy
 * @version 27-12-2023
 */
public class TestDialogController extends Dialog<Boolean> {
    /** Algorithm whose parameters are going to be set up. */
    private final Map<String, APartitionAlgorithm> algorithms;
    /** Graph to be tested. */
    private final Graph graph;
    /** Number of parts to divide the graph. */
    private final int partCount;
    /** Instance of statistics so results of testing can be recorded. */
    private final Statistics statistics = new Statistics();
    /** Main button of the dialog with parameters setting. */
    @FXML
    private ButtonType testButtonType;
    /** VBox containing all parameters. */
    @FXML
    private VBox vBox;
    /** Spinner with number of testing rounds. */
    @FXML
    private Spinner<Integer> spinnerRoundCount;
    /** If checked CSV statistics file will be created. */
    @FXML
    private CheckBox createCSVStatisticFile;
    /** If checked all resulting partition will be exported to GeoJSON file. */
    @FXML
    private CheckBox exportResultingPartitions;
    /**  Main button for start of testing. */
    @FXML
    private Button startTestingButton;
    /** Text area showing progress mesages. */
    @FXML
    private TextArea progressMessages;

    /**
     * Constructor of dialog for algorithm parameters setting with given stage/window
     * and the algorithm instance for dialog.
     * @param window        stage/window hosting dialog.
     * @throws IOException  when loading fxml.
     */
    public TestDialogController(Window window, Map<String, APartitionAlgorithm> algorithms, Graph graph, int partCount) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("test-dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        setTitle("Parameters setting of the algorithm");
        setHeaderText("Insert parameters of the algorithm:");
        setDialogPane(dialogPane);
        setResultConverter(buttonType -> true);
        this.algorithms = algorithms;
        this.graph = graph;
        this.partCount = partCount;
        startTestingButton.setOnAction(e -> onStartTestingButtonClick());
    }

    /**
     * Main testing method executed when Start testing button is clicked.
     */
    private void onStartTestingButtonClick() {
        statistics.setNumberOfRounds(spinnerRoundCount.getValue());
        progressMessages.appendText("Testing started...\n");
        Task<Void> graphRepartitioningTask = new Task<>() {
            @Override
            protected Void call() {
                for(APartitionAlgorithm algorithm : algorithms.values()) {
                    progressMessages.appendText(progressMessages.getText() + "Testing algorithm: " + algorithm.getName() + "\n");
                    for(int i = 0; i < spinnerRoundCount.getValue(); i++) {
                        progressMessages.appendText("Starting round " + i + "...\n");
                        progressMessages.appendText("Partitioning...\n");
                        GraphPartition graphPartition = algorithm.getGraphPartition(graph, partCount);
                        progressMessages.appendText("Partition was created.\n");

                        if (createCSVStatisticFile.isSelected()) {
                            progressMessages.appendText("Adding to statistics...\n");
                            addToStatistics(algorithm, graphPartition);
                        }
                        if (exportResultingPartitions.isSelected()) {
                            progressMessages.appendText("Recording result...\n");
                            JSONParser.exportResultingPartition(algorithm, graphPartition, i);
                        }
                    }
                    progressMessages.appendText("Algorithm " + algorithm.getName() + " was tested.\n");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (createCSVStatisticFile.isSelected()) {
                    progressMessages.appendText("Creating CSV statistics file...\n");
                    if (statistics.recordResultsToCSV(spinnerRoundCount.getValue(), partCount, graph, algorithms)) {
                        progressMessages.appendText("CSV file was created successfully.\n");
                    }
                    else {
                        progressMessages.appendText("CSV file creation failed.\n");
                    }

                }
                progressMessages.appendText("Testing finished.\n");            }

            @Override
            protected void failed() {
                super.failed();
                progressMessages.appendText("Something went wrong.\n");
            }
        };
        Thread graphPartitioningThread = new Thread(graphRepartitioningTask);
        graphPartitioningThread.setDaemon(true);
        graphPartitioningThread.start();
    }

    /**
     * Adds graph partition characteristics to statistics.
     * @param algorithm         algorithm that created partition.
     * @param graphPartition    graph partition.
     */
    private void addToStatistics(APartitionAlgorithm algorithm, GraphPartition graphPartition) {
        statistics.addTime(algorithm.getName(), graphPartition.getTime());
        statistics.addDeviation(algorithm.getName(), graphPartition.getRelativeStandardDeviation());
        statistics.addNumberOfCutEdges(algorithm.getName(), graphPartition.getCutEdgesCount());
        statistics.addMinNumberOfNeighbours(algorithm.getName(), graphPartition.getMinNeighbours());
        statistics.addMaxNumberOfNeighbours(algorithm.getName(), graphPartition.getMaxNeighbours());
        statistics.addAverageNumberOfNeighbours(algorithm.getName(), graphPartition.getAverageNeighbours());
    }


}
