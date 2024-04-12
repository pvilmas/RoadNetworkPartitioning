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
        private final Map<String, List<Long>> times = new HashMap<>();
        private final Map<String, List<Double>> deviations = new HashMap<>();
        private final Map<String, List<Integer>> numberOfCutEdges = new HashMap<>();
        private final Map<String, List<Integer>> minNumberOfNeighbours = new HashMap<>();
        private final Map<String, List<Integer>> maxNumberOfNeighbours = new HashMap<>();
        private final Map<String, List<Double>> averageNumberOfNeighbours = new HashMap<>();
        private final List<String> columnNames = new ArrayList<>();

        private void addTime(String algorithmName, long time) {
            times.putIfAbsent(algorithmName, new ArrayList<>(12));
            times.get(algorithmName).add(time);
        }

        private void addDeviation(String algorithmName, double deviation) {
            deviations.putIfAbsent(algorithmName, new ArrayList<>(12));
            deviations.get(algorithmName).add(deviation);
        }

        private void addNumberOfCutEdges(String algorithmName, int numberOfCutEdge) {
            numberOfCutEdges.putIfAbsent(algorithmName, new ArrayList<>(12));
            numberOfCutEdges.get(algorithmName).add(numberOfCutEdge);
        }

        private void addMinNumberOfNeighbours(String algorithmName, int minNumberOfNeighbour) {
            minNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
            minNumberOfNeighbours.get(algorithmName).add(minNumberOfNeighbour);
        }

        private void addMaxNumberOfNeighbours(String algorithmName, int maxNumberOfNeighbour) {
            maxNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
            maxNumberOfNeighbours.get(algorithmName).add(maxNumberOfNeighbour);
        }

        private void addAverageNumberOfNeighbours(String algorithmName, double averageNumberOfNeighbour) {
            averageNumberOfNeighbours.putIfAbsent(algorithmName, new ArrayList<>(12));
            averageNumberOfNeighbours.get(algorithmName).add(averageNumberOfNeighbour);
        }

        private void calculateAverage() {
            if (isAverageCalculated) {
                return;
            }
            isAverageCalculated = true;
            int n = numberOfRounds - 2;
            for (Map.Entry<String, List<Long>> timeEntry : this.times.entrySet()) {
                List<Long> times = timeEntry.getValue();
                prepareList(times);
                long totalTime = 0;
                for (Long time: times) {
                    totalTime += time;
                }
                timeEntry.setValue(new ArrayList<>(1));
                timeEntry.getValue().add(totalTime/n);
            }
            for (Map.Entry<String, List<Double>> deviationEntry : this.deviations.entrySet()) {
                List<Double> deviations = deviationEntry.getValue();
                prepareList(deviations);
                double totalDeviation = 0;
                for (Double deviation: deviations) {
                    totalDeviation += deviation;
                }
                deviationEntry.setValue(new ArrayList<>(1));
                deviationEntry.getValue().add(totalDeviation/n);
                deviationEntry.setValue(new ArrayList<>(1));
            }
            for (Map.Entry<String, List<Integer>> numberOfCutEdgeEntry : this.numberOfCutEdges.entrySet()) {
                List<Integer> numberOfCutEdges = numberOfCutEdgeEntry.getValue();
                prepareList(numberOfCutEdges);
                int totalNumberOfCutEdges = 0;
                for (Integer numberOfCutEdge: numberOfCutEdges) {
                    totalNumberOfCutEdges += numberOfCutEdge;
                }
                numberOfCutEdgeEntry.setValue(new ArrayList<>(1));
                numberOfCutEdgeEntry.getValue().add(totalNumberOfCutEdges/n);
                numberOfCutEdgeEntry.setValue(new ArrayList<>(1));
            }
            for (Map.Entry<String, List<Integer>> minNumberOfNeighbourEntry : this.minNumberOfNeighbours.entrySet()) {
                List<Integer> minNumberOfNeighbours = minNumberOfNeighbourEntry.getValue();
                prepareList(minNumberOfNeighbours);
                int totalMinNumberOfNeighbours = 0;
                for (Integer minNumberOfNeighbour: minNumberOfNeighbours) {
                    totalMinNumberOfNeighbours += minNumberOfNeighbour;
                }
                minNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
                minNumberOfNeighbourEntry.getValue().add(totalMinNumberOfNeighbours/n);
                minNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
            }
            for (Map.Entry<String, List<Integer>> maxNumberOfNeighbourEntry : this.maxNumberOfNeighbours.entrySet()) {
                List<Integer> maxNumberOfNeighbours = maxNumberOfNeighbourEntry.getValue();
                prepareList(maxNumberOfNeighbours);
                int totalMaxNumberOfNeighbours = 0;
                for (Integer maxNumberOfNeighbour: maxNumberOfNeighbours) {
                    totalMaxNumberOfNeighbours += maxNumberOfNeighbour;
                }
                maxNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
                maxNumberOfNeighbourEntry.getValue().add(totalMaxNumberOfNeighbours/n);
                maxNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
            }
            for (Map.Entry<String, List<Double>> averageNumberOfNeighbourEntry : this.averageNumberOfNeighbours.entrySet()) {
                List<Double> averageNumberOfNeighbours = averageNumberOfNeighbourEntry.getValue();
                prepareList(averageNumberOfNeighbours);
                double totalAverageNumberOfNeighbours = 0;
                for (Double averageNumberOfNeighbour: averageNumberOfNeighbours) {
                    totalAverageNumberOfNeighbours += averageNumberOfNeighbour;
                }
                averageNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
                averageNumberOfNeighbourEntry.getValue().add(totalAverageNumberOfNeighbours/n);
                averageNumberOfNeighbourEntry.setValue(new ArrayList<>(1));
            }

            columnNames.add("Algorithm Name");
            columnNames.add("Time [ms]");
            columnNames.add("Relative Standard Deviation [%]");
            columnNames.add("Number of Cut Edges");
            columnNames.add("Minimal Number of Neighbours");
            columnNames.add("Maximal Number of Neighbours");
            columnNames.add("Average Number of Neighbours");
        }

        private void prepareList(List attributes) {
            attributes.sort(null);
            attributes.remove(0);
            attributes.remove(times.size() - 1);
        }
    }
    private final Statistics statistics = new Statistics();

    private void onStartTestingButtonClick() {
        statistics.numberOfRounds = spinnerRoundCount.getValue();
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
                    bw.write(statistics.times.get(algorithmName).get(0) + ",");
                    bw.write(statistics.deviations.get(algorithmName).get(0) + ",");
                    bw.write(statistics.numberOfCutEdges.get(algorithmName).get(0) + ",");
                    bw.write(statistics.minNumberOfNeighbours.get(algorithmName).get(0) + ",");
                    bw.write(statistics.maxNumberOfNeighbours.get(algorithmName).get(0) + ",");
                    bw.write(statistics.averageNumberOfNeighbours.get(algorithmName).get(0) + "\n");

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

    private void addToStatistics(APartitionAlgorithm algorithm, GraphPartition graphPartition) {
        statistics.addTime(algorithm.getName(), graphPartition.getTime());
        statistics.addDeviation(algorithm.getName(), graphPartition.getRelativeStandartDeviation());
        statistics.addNumberOfCutEdges(algorithm.getName(), graphPartition.getCutEdgesCount());
        statistics.addMinNumberOfNeighbours(algorithm.getName(), graphPartition.getMinNeighbours());
        statistics.addMaxNumberOfNeighbours(algorithm.getName(), graphPartition.getMaxNeighbours());
        statistics.addAverageNumberOfNeighbours(algorithm.getName(), graphPartition.getAverageNeighbours());
    }


}
