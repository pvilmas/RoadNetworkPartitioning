package bp.roadnetworkpartitioning;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;
import java.util.Objects;


/**
 * Controller of main window of the app.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class MainController {
    /** Instance of main stage. */
    private static Stage stage = null;
    @FXML
    private Spinner<Integer> spinnerPartCount;
    /** Graph to show. */
    private Graph graph = null;
    /** Instance of ScrollPane containing graph. */
    @FXML
    private ScrollPane scrollPane;
    /** TextField with zoom value. */
    @FXML
    private TextField textZoom;
    /** VBox containing radio buttons of all available
     * algorithms and buttons with their setting. */
    @FXML
    private VBox vboxRadioBtn;
    /** Label with zoom value. */
    @FXML
    private Label labelZoom;
    /** Default zoom value. */
    private static int zoom = 10;
    /** Default size of vertex. */
    private static final int size = 5;
    /** Toggle group containing radio buttons of all available algorithms. */
    @FXML
    private ToggleGroup group;
    /** Map with all available graph partitioning algorithms. */
    private static Map<String, APartitionAlgorithm> algorithms;
    /** */
    private GraphPartition graphPartition = null;
    /** */
    private static Color[] colors = {Color.BLUE, Color.RED};

    /**
     * Displays all available graph partitioning algorithms.
     */
    public void setAlgorithms(){
        algorithms = AlgorithmsLoader.findAlgorithms();
        for (Map.Entry<String, APartitionAlgorithm> algorithm: algorithms.entrySet()) {
            HBox hBox = new HBox(10);
            RadioButton radioButton = new RadioButton();
            radioButton.setText(algorithm.getKey());
            radioButton.setUserData(algorithm.getValue());
            radioButton.setId(algorithm.getKey());
            radioButton.setToggleGroup(group);
            Button btnSetting = new Button("Setting");
            btnSetting.getStyleClass().setAll("btn","btn-primary");
            btnSetting.setOnAction(e -> showSettingDialog(algorithm.getValue()));
            hBox.getChildren().addAll(radioButton, btnSetting);
            hBox.setAlignment(Pos.CENTER);
            vboxRadioBtn.getChildren().add(hBox);
        }
        group.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {

            if (group.getSelectedToggle() != null) {
                APartitionAlgorithm algorithm = (APartitionAlgorithm) group.getSelectedToggle().getUserData();
                graphPartition = algorithm.getGraphPartition(graph, spinnerPartCount.getValue());
                visualizeGraph();
            }

        });
    }

    /**
     * Shows setting dialog of given algorithm.
     * @param algorithm   name of the algorithm.
     */
    private void showSettingDialog(APartitionAlgorithm algorithm) {
        try {
            SettingDialogController dialog = new SettingDialogController(stage, algorithm);
            dialog.showAndWait().ifPresent(apply -> {
                if (group.getSelectedToggle() != null) {
                    graphPartition = algorithm.getGraphPartition(graph, spinnerPartCount.getValue());
                    visualizeGraph();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method called when MenuItem "Insert Graph" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onInsertGraphJSONMenuClick(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        this.graph = JSONParser.readFile(selectedFile);
        visualizeGraph();
    }

    /**
     * Method called on +5 button click.
     * Increasing zoom by 5.
     */
    @FXML
    protected void onIncreaseButtonClick(){
        zoom += 5;
        labelZoom.setText(""+zoom);
        visualizeGraph();
    }

    /**
     * Method called on -5 button click.
     * Decreasing zoom by 5.
     */
    @FXML
    protected void onDecreaseButtonClick(){
        zoom -= 5;
        labelZoom.setText(""+zoom);
        visualizeGraph();
    }

    /**
     * Method called when zoom button clicked.
     * This method zooms graph.
     */
    @FXML
    protected void onZoomButtonClick(){
        zoom = getNumberFromString(textZoom.getText());
        labelZoom.setText(""+zoom);
        visualizeGraph();
    }

    /**
     *
     */
    @FXML
    protected void onPickColorsButtonClick() {
        try {
            PartColorsDialogController dialog = new PartColorsDialogController(stage, spinnerPartCount.getValue(), colors);
            dialog.showAndWait().ifPresent(pickedColors -> {
                colors = pickedColors;
                visualizeGraph();
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onCreateGraphMenuClick(){
        try {
            GraphDialogController dialog = new GraphDialogController(stage);
            dialog.showAndWait().ifPresent(graph -> {
                this.graph = graph;
                visualizeGraph();
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Visualizes attribute graph.
     */
    private void visualizeGraph(){
        if (this.graph == null) {
            return;
        }
        Group group = new Group();
        for(Vertex vertex: this.graph.getVertices().values()){
            Circle circle = new Circle(vertex.getX()*zoom, vertex.getY()*zoom, size);
            if (graphPartition != null) {
                circle.setStroke(colors[graphPartition.getVerticesPlacements().get(vertex)]);
                circle.setFill(colors[graphPartition.getVerticesPlacements().get(vertex)]);
            }
            group.getChildren().add(circle);
            for(Edge edge: vertex.getStartingEdges()){
                Line line = new Line(vertex.getX()*zoom, vertex.getY()*zoom,
                        edge.getEndpoint().getX()*zoom, edge.getEndpoint().getY()*zoom);
                if ((graphPartition != null) && Objects.equals(graphPartition.getVerticesPlacements().get(vertex), graphPartition.getVerticesPlacements().get(edge.getEndpoint()))){
                    line.setStroke(colors[graphPartition.getVerticesPlacements().get(vertex)]);
                }
                group.getChildren().add(line);
            }
        }
        scrollPane.setPrefSize(1000, 1000);
        scrollPane.setContent(group);
    }


    /**
     * Method called when button "Create JSON File!" is clicked.
     * This method uses method createJSONFile from JSONParser class for creating JSON file.
     * And gets details from textFields for correct parsing of files.
     */
    @FXML
    protected void onCreateJSONMenuClick(){
        try {
            JSONDialogController dialog = new JSONDialogController(stage);
            dialog.showAndWait().ifPresent(System.out::println);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Gets positive integer from string.
     * @param text      String to convert to integer.
     * @return  integer made from string.
     */
    public static int getNumberFromString(String text){
        if(text.trim().matches("\\d+")){
            return Integer.parseInt(text.trim());
        }else {
            return -1;
        }
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