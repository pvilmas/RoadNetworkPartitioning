package bp.roadnetworkpartitioning;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
    private final Statistics statistics = new Statistics();
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

    private void onStartTestingButtonClick() {
        statistics.setNumberOfRounds(spinnerRoundCount.getValue());
        progressMessages.appendText("Testing started...\n");
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
        if (createCSVStatisticFile.isSelected()) {
            progressMessages.appendText("Creating CSV statistics file...\n");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
            LocalDateTime now = LocalDateTime.now();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("results_" + graph.getVertices().size() + "-" + graph.getEdges().size()
                    + "_" + spinnerRoundCount.getValue() + "_"+ partCount + "_" + dtf.format(now) + ".csv"))) {
                int i = 0;
                for(; i < statistics.getColumnNames().size() - 1; i++){
                    bw.write(statistics.getColumnNames().get(i) + ",");
                }
                bw.write(statistics.getColumnNames().get(i) + "\n");
                for(String algorithmName : algorithms.keySet()) {
                    for(int j = 0; j < statistics.getTimes().get(algorithmName).size(); j++) {
                        bw.write(algorithmName + " " + j +",");
                        bw.write(statistics.getTimes().get(algorithmName).get(j) + ",");
                        bw.write(statistics.getDeviations().get(algorithmName).get(j) + ",");
                        bw.write(statistics.getNumberOfCutEdges().get(algorithmName).get(j) + ",");
                        bw.write(statistics.getMinNumberOfNeighbours().get(algorithmName).get(j) + ",");
                        bw.write(statistics.getMaxNumberOfNeighbours().get(algorithmName).get(j) + ",");
                        bw.write(statistics.getAverageNumberOfNeighbours().get(algorithmName).get(j) + "\n");
                    }
                    bw.write("\n\n");
                }

                statistics.calculateAverage();
                for(String algorithmName : algorithms.keySet()) {
                    bw.write(algorithmName + " - average,");
                    bw.write(statistics.getTimes().get(algorithmName).get(0) + ",");
                    bw.write(statistics.getDeviations().get(algorithmName).get(0) + ",");
                    bw.write(statistics.getNumberOfCutEdges().get(algorithmName).get(0) + ",");
                    bw.write(statistics.getMinNumberOfNeighbours().get(algorithmName).get(0) + ",");
                    bw.write(statistics.getMaxNumberOfNeighbours().get(algorithmName).get(0) + ",");
                    bw.write(statistics.getAverageNumberOfNeighbours().get(algorithmName).get(0) + "\n");
                }
                bw.flush();
                progressMessages.appendText("CSV file was created successfully.\n");
            }
            catch (IOException e) {
                e.printStackTrace();
                progressMessages.appendText("CSV file creation failed.\n");
            }
        }
        progressMessages.appendText("Testing finished.\n");
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
