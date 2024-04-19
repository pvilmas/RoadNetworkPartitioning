package bp.roadnetworkpartitioning;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class StatisticsDialogController extends Dialog<Boolean> {

    /**
     *
     */
    public static class Data{
        /**  */
        private final SimpleStringProperty algorithmName = new SimpleStringProperty("");
        /**  */
        private final SimpleLongProperty algorithmTime = new SimpleLongProperty(0);
        /**  */
        private final SimpleDoubleProperty algorithmDeviation = new SimpleDoubleProperty(0);
        /**  */
        private final SimpleIntegerProperty algorithmNumberOfCutEdges = new SimpleIntegerProperty(0);
        /**  */
        private final SimpleIntegerProperty algorithmMinNumberOfNeighbours = new SimpleIntegerProperty(0);
        /**  */
        private final SimpleIntegerProperty algorithmMaxNumberOfNeighbours = new SimpleIntegerProperty(0);
        /**  */
        private final SimpleDoubleProperty algorithmAverageNumberOfNeighbours = new SimpleDoubleProperty(0);

        /**
         *
         * @param algorithmName
         * @param algorithmTime
         * @param algorithmDeviation
         * @param numberOfCutEdges
         * @param maxNumberOfNeighbours
         */
        private Data(String algorithmName, long algorithmTime, double algorithmDeviation, int numberOfCutEdges,
                     int minNumberOfNeighbours, int maxNumberOfNeighbours, double averageNumberOfNeighbours){
            this.algorithmName.set(algorithmName);
            this.algorithmTime.set(algorithmTime);
            this.algorithmDeviation.set(algorithmDeviation);
            this.algorithmNumberOfCutEdges.set(numberOfCutEdges);
            this.algorithmMinNumberOfNeighbours.set(minNumberOfNeighbours);
            this.algorithmMaxNumberOfNeighbours.set(maxNumberOfNeighbours);
            this.algorithmAverageNumberOfNeighbours.set(averageNumberOfNeighbours);
        }

        public final String getAlgorithmName() {
            return algorithmName.get();
        }

        public final SimpleStringProperty algorithmNameProperty() {
            return algorithmName;
        }

        public final void setAlgorithmName(String algorithmName) {
            this.algorithmName.set(algorithmName);
        }

        public final long getAlgorithmTime() {
            return algorithmTime.get();
        }

        public final SimpleLongProperty algorithmTimeProperty() {
            return algorithmTime;
        }

        public final void setAlgorithmTime(long algorithmTime) {
            this.algorithmTime.set(algorithmTime);
        }

        public final double getAlgorithmDeviation() {
            return algorithmDeviation.get();
        }

        public final SimpleDoubleProperty algorithmDeviationProperty() {
            return algorithmDeviation;
        }

        public final void setAlgorithmDeviation(double algorithmDeviation) {
            this.algorithmDeviation.set(algorithmDeviation);
        }

        public final int getAlgorithmNumberOfCutEdges() {
            return algorithmNumberOfCutEdges.get();
        }

        public final SimpleIntegerProperty algorithmNumberOfCutEdgesProperty() {
            return algorithmNumberOfCutEdges;
        }

        public final void setAlgorithmNumberOfCutEdges(int algorithmNumberOfCutEdges) {
            this.algorithmNumberOfCutEdges.set(algorithmNumberOfCutEdges);
        }

        public final int getAlgorithmMinNumberOfNeighbours() {
            return algorithmMinNumberOfNeighbours.get();
        }

        public final SimpleIntegerProperty algorithmMinNumberOfNeighboursProperty() {
            return algorithmMinNumberOfNeighbours;
        }

        public final void setAlgorithmMinNumberOfNeighbours(int algorithmMinNumberOfNeighbours) {
            this.algorithmMinNumberOfNeighbours.set(algorithmMinNumberOfNeighbours);
        }

        public final double getAlgorithmAverageNumberOfNeighbours() {
            return algorithmAverageNumberOfNeighbours.get();
        }

        public final SimpleDoubleProperty algorithmAverageNumberOfNeighboursProperty() {
            return algorithmAverageNumberOfNeighbours;
        }

        public final void setAlgorithmAverageNumberOfNeighbours(int algorithmAverageNumberOfNeighbours) {
            this.algorithmAverageNumberOfNeighbours.set(algorithmAverageNumberOfNeighbours);
        }

        public final int getAlgorithmMaxNumberOfNeighbours() {
            return algorithmMaxNumberOfNeighbours.get();
        }

        public final SimpleIntegerProperty algorithmMaxNumberOfNeighboursProperty() {
            return algorithmMaxNumberOfNeighbours;
        }

        public final void setAlgorithmMaxNumberOfNeighbours(int algorithmMaxNumberOfNeighbours) {
            this.algorithmMaxNumberOfNeighbours.set(algorithmMaxNumberOfNeighbours);
        }

    }

    /**  */
    private final Map<String, APartitionAlgorithm> algorithms;
    /**  */
    @FXML
    private ButtonType statisticsButtonType;
    /**  */
    @FXML
    private Button exportCSVButtonType;
    /**  */
    @FXML
    private TableView<Data> tableView;

    /**
     *
     * @param window
     * @param algorithms
     * @throws IOException
     */
    public StatisticsDialogController(Window window, Map<String, APartitionAlgorithm> algorithms) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("statistics_dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        setTitle("Statistics");
        setHeaderText("Results:");
        setDialogPane(dialogPane);
        setResultConverter(buttonType -> true);
        exportCSVButtonType.setOnAction(e -> onExportToCSVButtonClick());
        this.algorithms = algorithms;
        tableView.setEditable(false);
        addRows();
    }

    /**
     *
     */
    @FXML
    protected void onExportToCSVButtonClick() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("results.csv"))) {
            int i = 0;
            for(; i < tableView.getColumns().size() - 1; i++){
                bw.write(tableView.getColumns().get(i).getText() + ",");
            }
            bw.write(tableView.getColumns().get(i).getText() + "\n");
            for(Data data: tableView.getItems()){
                bw.write(data.algorithmName.getValue() + ",");
                bw.write(data.algorithmTime.getValue() + ",");
                bw.write(data.algorithmDeviation.getValue() + ",");
                bw.write(data.algorithmNumberOfCutEdges.getValue() + ",");
                bw.write(data.algorithmMinNumberOfNeighbours.getValue() + ",");
                bw.write(data.algorithmMaxNumberOfNeighbours.getValue() + ",");
                bw.write(data.algorithmAverageNumberOfNeighbours.getValue() + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void addRows() {
        ObservableList<Data> data = tableView.getItems();
        for(Map.Entry<String, APartitionAlgorithm> algorithmEntry: algorithms.entrySet()){
            GraphPartition partition = algorithmEntry.getValue().getGraphPartition();
            if (partition != null){
                data.add(new Data(algorithmEntry.getKey(), partition.getTime(), partition.getRelativeStandardDeviation(),
                        partition.getCutEdgesCount(), partition.getMinNeighbours(), partition.getMaxNeighbours(), partition.getAverageNeighbours()));

            }
        }
    }
}
