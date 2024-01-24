package bp.roadnetworkpartitioning;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class StatisticsDialogController extends Dialog<Boolean> {
    private static class Data{

    }

    private final Map<String, APartitionAlgorithm> algorithms;
    @FXML
    private ButtonType statisticsButtonType;

    private TableView<Data> tableView;

    public StatisticsDialogController(Window window, Map<String, APartitionAlgorithm> algorithms) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("statistics-dialog.fxml"));
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
        this.algorithms = algorithms;
        tableView.setEditable(false);
        addRows();
    }

    private void addRows() {
        ObservableList<Data> data = tableView.getItems();
        data.add(new Data());
    }

    @FXML
    protected void onCloseButtonClick(ActionEvent event) {

    }
}
