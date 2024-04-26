package bp.roadnetworkpartitioning;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 *
 */
public class StatisticsDialogController extends Dialog<Boolean> {

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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("results_" + dtf.format(now) + ".csv"))) {
            int i = 0;
            for(; i < tableView.getColumns().size() - 1; i++){
                bw.write(tableView.getColumns().get(i).getText() + ",");
            }
            bw.write(tableView.getColumns().get(i).getText() + "\n");
            for(Data data: tableView.getItems()){
                bw.write(data.getAlgorithmName() + ",");
                bw.write(data.getAlgorithmTime() + ",");
                bw.write(data.getAlgorithmDeviation() + ",");
                bw.write(data.getAlgorithmNumberOfCutEdges() + ",");
                bw.write(data.getAlgorithmMinNumberOfNeighbours() + ",");
                bw.write(data.getAlgorithmMaxNumberOfNeighbours() + ",");
                bw.write(data.getAlgorithmAverageNumberOfNeighbours() + "\n");
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
            GraphPartition partition = algorithmEntry.getValue().getGraphPartition(algorithmEntry.getValue().getGraph());
            if (partition != null){
                data.add(new Data(algorithmEntry.getKey(), partition.getTime(), partition.getRelativeStandardDeviation(),
                        partition.getCutEdgesCount(), partition.getMinNeighbours(), partition.getMaxNeighbours(), partition.getAverageNeighbours()));

            }
        }
    }
}
