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

    private static class Statistics {
        private int numberOfRounds = 1;
        private boolean isAverageCalculated = false;
        private final Map<String, Long> times = new HashMap<>();
        private final Map<String, Double> balances = new HashMap<>();
        private final Map<String, Integer> numberOfCutEdges = new HashMap<>();
        private final Map<String, Integer> maxNumberOfNeighbours = new HashMap<>();
        private final List<String> columnNames = new ArrayList<>();

        private void addTime(String algorithmName, long time) {
            times.putIfAbsent(algorithmName, 0L);
            times.put(algorithmName, times.get(algorithmName) + time);
        }

        private void addBalance(String algorithmName, double balance) {
            balances.putIfAbsent(algorithmName, 0.0);
            balances.put(algorithmName, balances.get(algorithmName) + balance);
        }

        private void addNumberOfCutEdges(String algorithmName, int numberOfCutEdge) {
            numberOfCutEdges.putIfAbsent(algorithmName, 0);
            numberOfCutEdges.put(algorithmName, numberOfCutEdges.get(algorithmName) + numberOfCutEdge);
        }

        private void addMaxNumberOfNeighbours(String algorithmName, int maxNumberOfNeighbour) {
            maxNumberOfNeighbours.putIfAbsent(algorithmName, 0);
            maxNumberOfNeighbours.put(algorithmName, maxNumberOfNeighbours.get(algorithmName) + maxNumberOfNeighbour);
        }

        private void calculateAverage() {
            if (isAverageCalculated) {
                return;
            }
            isAverageCalculated = true;
            for (Map.Entry<String, Long> timeEntry : times.entrySet()) {
                timeEntry.setValue(timeEntry.getValue() / numberOfRounds);
            }
            for (Map.Entry<String, Double> balanceEntry : balances.entrySet()) {
                balanceEntry.setValue(balanceEntry.getValue() / numberOfRounds);
            }
            for (Map.Entry<String, Integer> numberOfCutEdgeEntry : numberOfCutEdges.entrySet()) {
                numberOfCutEdgeEntry.setValue(numberOfCutEdgeEntry.getValue() / numberOfRounds);
            }
            for (Map.Entry<String, Integer> maxNumberOfNeighbourEntry : maxNumberOfNeighbours.entrySet()) {
                maxNumberOfNeighbourEntry.setValue(maxNumberOfNeighbourEntry.getValue() / numberOfRounds);
            }
            columnNames.add("Algorithm Name");
            columnNames.add("Time");
            columnNames.add("Balance");
            columnNames.add("Number of Cut Edges");
            columnNames.add("Maximal Number of Neighbours");
        }
    }
    private final Statistics statistics = new Statistics();

    private void onStartTestingButtonClick() {
        statistics.numberOfRounds = spinnerRoundCount.getValue();
        progressMessages.setText("Testing started...\n");
        for(APartitionAlgorithm algorithm : algorithms.values()) {
            progressMessages.setText(progressMessages.getText() + "Testing algorithm: " + algorithm.getName() + "\n");
            for(int i = 0; i < spinnerRoundCount.getValue(); i++) {
                progressMessages.setText(progressMessages.getText() + "Starting round " + i + "...\n");
                progressMessages.setText(progressMessages.getText() + "Partitioning...\n");
                GraphPartition graphPartition = algorithm.getGraphPartition(graph, partCount);
                progressMessages.setText(progressMessages.getText() + "Partition was created.\n");

                if (createCSVStatisticFile.isSelected()) {
                    progressMessages.setText(progressMessages.getText() + "Adding to statistics...\n");
                    addToStatistics(algorithm, graphPartition);
                }
                if (exportResultingPartitions.isSelected()) {
                    progressMessages.setText(progressMessages.getText() + "Recording result...\n");
                    exportResultingPartition(algorithm, graphPartition, i);
                }
            }
            progressMessages.setText(progressMessages.getText() + "Algorithm " + algorithm.getName() + " was tested.\n");
        }
        if (createCSVStatisticFile.isSelected()) {
            progressMessages.setText(progressMessages.getText() + "Creating CSV statistics file...\n");
            statistics.calculateAverage();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
            LocalDateTime now = LocalDateTime.now();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("results_" + dtf.format(now) + ".csv"))) {
                int i = 0;
                for(; i < statistics.columnNames.size() - 1; i++){
                    bw.write(statistics.columnNames.get(i) + ",");
                }
                bw.write(statistics.columnNames.get(i) + "\n");
                for(String algorithmName : algorithms.keySet()) {
                    bw.write(algorithmName + ",");
                    bw.write(statistics.times.get(algorithmName) + ",");
                    bw.write(statistics.balances.get(algorithmName) + ",");
                    bw.write(statistics.numberOfCutEdges.get(algorithmName) + ",");
                    bw.write(statistics.maxNumberOfNeighbours.get(algorithmName) + "\n");
                }
                bw.flush();
                progressMessages.setText(progressMessages.getText() + "CSV file was created successfully.\n");
            }
            catch (IOException e) {
                e.printStackTrace();
                progressMessages.setText(progressMessages.getText() + "CSV file creation failed.\n");
            }
        }
        progressMessages.setText(progressMessages.getText() + "Testing finished.\n");
    }

    private void exportResultingPartition(APartitionAlgorithm algorithm, GraphPartition graphPartition, int i) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String jsonName = algorithm.getName() + dtf.format(now) + "_" + i;
        int j = 0;
        for (Graph graphComponent : graphPartition.getGraphComponents()) {
            JSONParser.writeJSONFile(jsonName + "_" + j, graphComponent);
            j++;
        }
    }

    private void addToStatistics(APartitionAlgorithm algorithm, GraphPartition graphPartition) {
        statistics.addTime(algorithm.getName(), graphPartition.getTime());
        statistics.addBalance(algorithm.getName(), graphPartition.getBalance());
        statistics.addNumberOfCutEdges(algorithm.getName(), graphPartition.getCutEdgesCount());
        statistics.addMaxNumberOfNeighbours(algorithm.getName(), graphPartition.getMaxNeighbours());
    }


}
