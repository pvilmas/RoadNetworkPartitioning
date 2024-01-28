package bp.roadnetworkpartitioning;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class StatisticsDialogController extends Dialog<Boolean> {

    /**
     *
     */
    private static class Data{
        /**  */
        private final SimpleStringProperty algorithmName = new SimpleStringProperty("");
        /**  */
        private final SimpleLongProperty algorithmTime = new SimpleLongProperty(0);
        /**  */
        private final SimpleDoubleProperty algorithmBalance = new SimpleDoubleProperty(0);
        /**  */
        private final SimpleIntegerProperty algorithmNumberOfCutEdges = new SimpleIntegerProperty(0);
        /**  */
        private final SimpleIntegerProperty algorithmMaxNumberOfNeighbours = new SimpleIntegerProperty(0);

        /**
         *
         * @param algorithmName
         * @param algorithmTime
         * @param algorithmBalance
         * @param numberOfCutEdges
         * @param maxNumberOfNeighbours
         */
        private Data(String algorithmName, long algorithmTime, double algorithmBalance, int numberOfCutEdges, int maxNumberOfNeighbours){
            this.algorithmName.set(algorithmName);
            this.algorithmTime.set(algorithmTime);
            this.algorithmBalance.set(algorithmBalance);
            this.algorithmNumberOfCutEdges.set(numberOfCutEdges);
            this.algorithmMaxNumberOfNeighbours.set(maxNumberOfNeighbours);
        }

        public String getAlgorithmName() {
            return algorithmName.get();
        }

        public SimpleStringProperty algorithmNameProperty() {
            return algorithmName;
        }

        public void setAlgorithmName(String algorithmName) {
            this.algorithmName.set(algorithmName);
        }

        public long getAlgorithmTime() {
            return algorithmTime.get();
        }

        public SimpleLongProperty algorithmTimeProperty() {
            return algorithmTime;
        }

        public void setAlgorithmTime(long algorithmTime) {
            this.algorithmTime.set(algorithmTime);
        }

        public double getAlgorithmBalance() {
            return algorithmBalance.get();
        }

        public SimpleDoubleProperty algorithmBalanceProperty() {
            return algorithmBalance;
        }

        public void setAlgorithmBalance(double algorithmBalance) {
            this.algorithmBalance.set(algorithmBalance);
        }

        public int getAlgorithmNumberOfCutEdges() {
            return algorithmNumberOfCutEdges.get();
        }

        public SimpleIntegerProperty algorithmNumberOfCutEdgesProperty() {
            return algorithmNumberOfCutEdges;
        }

        public void setAlgorithmNumberOfCutEdges(int algorithmNumberOfCutEdges) {
            this.algorithmNumberOfCutEdges.set(algorithmNumberOfCutEdges);
        }

        public int getAlgorithmMaxNumberOfNeighbours() {
            return algorithmMaxNumberOfNeighbours.get();
        }

        public SimpleIntegerProperty algorithmMaxNumberOfNeighboursProperty() {
            return algorithmMaxNumberOfNeighbours;
        }

        public void setAlgorithmMaxNumberOfNeighbours(int algorithmMaxNumberOfNeighbours) {
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
        dialogPane.lookupButton(statisticsButtonType).addEventFilter(ActionEvent.ANY, this::onCloseButtonClick);
        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        setTitle("Statistics");
        setHeaderText("Results:");
        setDialogPane(dialogPane);
        setResultConverter(buttonType -> {
            if(!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                return null;
            }
            return true;
        });
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
            bw.write(tableView.getColumns().get(tableView.getColumns().size() - 1).getText() + "\n");
            for(Data data: tableView.getItems()){
                bw.write(data.algorithmName.getValue() + ",");
                bw.write(data.algorithmTime.getValue() + ",");
                bw.write(data.algorithmBalance.getValue() + ",");
                bw.write(data.algorithmNumberOfCutEdges.getValue() + ",");
                bw.write(data.algorithmMaxNumberOfNeighbours.getValue() + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    protected void onCloseButtonClick(ActionEvent event) {}

    /**
     *
     */
    private void addRows() {
        ObservableList<Data> data = tableView.getItems();
        for(Map.Entry<String, APartitionAlgorithm> algorithmEntry: algorithms.entrySet()){
            GraphPartition partition = algorithmEntry.getValue().getGraphPartition();
            if (partition != null){
                data.add(new Data(algorithmEntry.getKey(), partition.getTime(), partition.getBalance(),
                        partition.getCutEdgesCount(), partition.getMaxNeighbours()));

            }
        }
    }
}