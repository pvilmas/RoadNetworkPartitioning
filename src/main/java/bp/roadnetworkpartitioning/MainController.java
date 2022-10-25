package bp.roadnetworkpartitioning;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Controller of main window of the app.
 */
public class MainController {
    /** Instance of main stage. */
    private static Stage stage = null;
    /** File with vertices coordinates. */
    private static File coordinatesFile = null;
    /** File with edges information. */
    private static File edgesFile = null;

    @FXML
    /**
     * Method called when button is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    protected void onUploadCoordinatesButtonClick() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(true) { //TODO add any condition?
            coordinatesFile = selectedFile;
        }
    }

    @FXML
    /**
     * Method called when button is clicked.
     * This method opens a dialog for choosing a file with edges info.
     */
    protected void onUploadEdgesButtonClick() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(true) { //TODO add any condition?
            edgesFile = selectedFile;
        }
    }

    @FXML
    /**
     * Method called when button is clicked.
     * This method uses method createJSONFile from JSONParser class for creating JSON file.
     */
    protected void onCreateJSONButtonClick(){
        JSONParser.createJSONFile(coordinatesFile, edgesFile, "graph");
    }

    /**
     * Sets primary stage.
     * @param primaryStage  Instance of Stage.
     */
    public static void setStage(Stage primaryStage){
        if(stage == null) {
            stage = primaryStage;
        }
    }
}