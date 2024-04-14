package bp.roadnetworkpartitioning;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Class that serves as a controller for dialog with JSON file creation.
 *  @author Lucie Roy
 *  @version 27-03-2023
 */
public class JSONDialogController extends Dialog<Boolean> {

    /** File with vertices coordinates. */
    private File coordinatesFile = null;
    /** File with edges information. */
    private File edgesFile = null;
    /** ScrollPane containing first few lines of file with coordinates. */
    @FXML
    private ScrollPane scrollPaneNode;
    /** ScrollPane containing first few lines of file with edges info. */
    @FXML
    private ScrollPane scrollPaneEdge;
    /** TextField with number of line where data starts in edge file. */
    @FXML
    private TextField textFieldLineEdge;
    /** TextField with number of column where is delimiter of edge in edge file. */
    @FXML
    private TextField textFieldDelimiterEdge;
    /** TextField with number of column where is delimiter of node in node file. */
    @FXML
    private TextField textFieldDelimiterVertex;
    /** TextField with number of column where is start point of edge in edge file. */
    @FXML
    private TextField textFieldColStart;
    /** TextField with number of column where is endpoint of edge in edge file. */
    @FXML
    private TextField textFieldColEnd;
    /** TextField with number of column where is length of edge in edge file. */
    @FXML
    private TextField textFieldColLength;
    /** TextField with number of line where data starts in node file. */
    @FXML
    private TextField textFieldLineVertex;
    /** TextField with number of column where is ID of node in node file. */
    @FXML
    private TextField textFieldColID;
    /** TextField with number of column where is x-coordinate of node in node file. */
    @FXML
    private TextField textFieldColX;
    /** TextField with number of column where is y-coordinate of node in node file. */
    @FXML
    private TextField textFieldColY;
    /** Button for uploading file with coordinates. */
    @FXML
    private Button btnFileNode;
    /** Button for uploading file with edges info. */
    @FXML
    private Button btnFileEdge;
    /** Main button of the dialog creating JSON file. */
    @FXML
    private ButtonType btnCreateJSONFile;
    /** True, if JSON file is created. */
    private boolean isCreated = false;

    /**
     * Constructor of dialog for JSON file creation with given stage/window for dialog.
     * @param window        stage/window hosting dialog.
     * @throws IOException  when loading fxml.
     */
    public JSONDialogController(Window window) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("create-json-dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        dialogPane.lookupButton(btnCreateJSONFile).addEventFilter(ActionEvent.ANY, this::onCreateJSONButtonClick);
        btnFileNode.setOnAction(e -> onUploadCoordinatesButtonClick(window));
        btnFileEdge.setOnAction(e -> onUploadEdgesButtonClick(window));
        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        setTitle("GeoJSON Creator");
        setHeaderText("Insert files and their parameters:");
        setDialogPane(dialogPane);
        setResultConverter(buttonType -> {
            if(!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                return null;
            }
            return this.isCreated;
        });

        setOnShowing(dialogEvent -> Platform.runLater(() -> btnFileEdge.requestFocus()));
    }

    /**
     * Method called when button "Create JSON File!" is clicked.
     * This method uses method createJSONFile from JSONParser class for creating JSON file.
     * And gets details from textFields for correct parsing of files.
     * @param event triggering event.
     */
    @FXML
    protected void onCreateJSONButtonClick(ActionEvent event){
        int[] details = new int[8];
        details[0] = MainController.getNumberFromString(textFieldLineVertex.getText());
        details[1] = MainController.getNumberFromString(textFieldLineEdge.getText());
        details[2] = MainController.getNumberFromString(textFieldColStart.getText());
        details[3] = MainController.getNumberFromString(textFieldColEnd.getText());
        details[4] = MainController.getNumberFromString(textFieldColLength.getText());
        details[5] = MainController.getNumberFromString(textFieldColID.getText());
        details[6] = MainController.getNumberFromString(textFieldColX.getText());
        details[7] = MainController.getNumberFromString(textFieldColY.getText());
        String delimiterEdge = textFieldDelimiterEdge.getText();
        String delimiterVertex = textFieldDelimiterVertex.getText();
        this.isCreated = JSONParser.createJSONFile(details, delimiterEdge, delimiterVertex,
                coordinatesFile, edgesFile, "graph");
    }

    /**
     * Method called when button "Upload file!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     * @param stage stage/window hosting file chooser.
     */
    private void onUploadCoordinatesButtonClick(Window stage) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null) {
            coordinatesFile = selectedFile;
            showFirstTwentyLinesOfFile(coordinatesFile, scrollPaneNode);
        }
    }

    /**
     * Method called when button "Upload file!" is clicked.
     * This method opens a dialog for choosing a file with edges info.
     * @param stage stage/window hosting file chooser.
     */
    private void onUploadEdgesButtonClick(Window stage) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null) {
            edgesFile = selectedFile;
            showFirstTwentyLinesOfFile(edgesFile, scrollPaneEdge);
        }
    }

    /**
     * Displays first twenty lines of file in ScrollPane.
     * @param selectedFile  file with twenty lines.
     * @param sp            ScrollPane for displaying lines.
     */
    private void showFirstTwentyLinesOfFile(File selectedFile, ScrollPane sp){
        final int LINES = 20;
        VBox vbox = new VBox();
        int i = 0;
        try (Scanner sc = new Scanner(selectedFile)){
            while(sc.hasNextLine() && i < LINES) {
                String line = sc.nextLine();
                vbox.getChildren().add(new Text(line));
                i++;
            }
            sp.setContent(vbox);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
