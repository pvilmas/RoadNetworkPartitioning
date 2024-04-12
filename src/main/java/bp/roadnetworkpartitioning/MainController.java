package bp.roadnetworkpartitioning;

import javafx.event.ActionEvent;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Controller of main window of the app.
 * @author Lucie Roy
 * @version 28-12-2023
 */
public class MainController {
    /** Instance of main stage. */
    private static Stage stage = null;
    @FXML
    private TextArea progressMessages;
    /** Default zoom value. */
    private int zoom = 10;
    /** Stores color of each part. */
    private Color[] colors = {Color.BLUE, Color.RED};
    /** Sets number of parts. */
    @FXML
    private Spinner<Integer> spinnerPartCount;
    /** Instance of ScrollPane containing graph. */
    @FXML
    private ScrollPane scrollPane;
    /** TextField with zoom value. */
    @FXML
    private TextField textZoom;
    /**
     * VBox containing radio buttons of all available
     * algorithms and buttons with their setting.
     */
    @FXML
    private VBox vboxRadioBtn;
    /** Label with zoom value. */
    @FXML
    private Label labelZoom;
    /** Toggle group containing radio buttons of all available algorithms. */
    @FXML
    private ToggleGroup group;
    /** Graph to show. */
    private Graph graph = null;
    /** Computed partition of the graph. */
    private GraphPartition graphPartition = null;
    /**  */
    private Map<String, APartitionAlgorithm> algorithms = new HashMap<>();

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
                progressMessages.appendText("Getting Graph Partition by "+ algorithm.getName() +"...\n");
                graphPartition = algorithm.getGraphPartition();
                if (graphPartition != null) {
                    visualizeGraph();
                    progressMessages.appendText("Graph Partition was displayed.\n");
                }
                else {
                    progressMessages.appendText("Graph Partition could not be displayed.\n");
                }
            }

        });
    }

    /**
     * Method called when MenuItem "Insert Graph" is clicked.
     * This method opens a dialog for choosing a json file with graph parameters.
     */
    @FXML
    protected void onInsertGraphJSONMenuClick(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        progressMessages.appendText("Reading selected file...\n");
        this.graph = JSONParser.readFile(selectedFile);
        progressMessages.appendText("Reading is done, visualizing graph...\n");
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
        progressMessages.appendText("Zoomed.\n");
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
        progressMessages.appendText("Zoomed.\n");
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
        progressMessages.appendText("Zoomed.\n");
        visualizeGraph();
    }

    /**
     * Called when button "Pick colors" is clicked.
     * It opens dialog with color picker for each part.
     */
    @FXML
    protected void onPickColorsButtonClick() {
        try {
            PartColorsDialogController dialog = new PartColorsDialogController(stage, spinnerPartCount.getValue(), colors);
            dialog.showAndWait().ifPresent(pickedColors -> {
                colors = pickedColors;
                progressMessages.appendText("You picked these colors: " + Arrays.toString(colors) + ". Repainting...\n");
                visualizeGraph();
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @FXML
    protected void onShowStatisticsButtonClick() {
        try {
            StatisticsDialogController dialog = new StatisticsDialogController(stage, algorithms);
            dialog.showAndWait();
        } catch (Exception e){
            progressMessages.appendText("Statistics could not be displayed.\n");
            e.printStackTrace();
        }
    }

    /**
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for inserting graph parameters.
     */
    @FXML
    protected void onCreateGraphMenuClick(){
        try {
            progressMessages.appendText("lol\n");
            GraphDialogController dialog = new GraphDialogController(stage);
            dialog.showAndWait().ifPresent(graph -> {
                this.graph = graph;
                visualizeGraph();
            });
            progressMessages.appendText("lol\n");
        } catch (Exception e){
            progressMessages.appendText("lol\n");
            e.printStackTrace();
        }
    }

    /**
     * Method called when button "Create JSON File!" is clicked.
     * It opens dialog for entering details and files for requested JSON file.
     */
    @FXML
    protected void onCreateJSONMenuClick(){
        try {
            JSONDialogController dialog = new JSONDialogController(stage);
            dialog.showAndWait().ifPresent(isCreated -> {
                if (isCreated) {
                    progressMessages.appendText("JSON file was created.\n");
                }
                else {
                    progressMessages.appendText("JSON file could not be created.\n");

                }
            });
        } catch (Exception e){
            progressMessages.appendText("lol\n");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onTestMenuClick(ActionEvent actionEvent) {
        try {
            TestDialogController dialog = new TestDialogController(stage, algorithms, graph, spinnerPartCount.getValue());
            dialog.showAndWait();
        } catch (Exception e){
            progressMessages.appendText("lol\n");
            e.printStackTrace();
        }
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
                    progressMessages.appendText("lol\n");
                    graphPartition = algorithm.getGraphPartition(graph, spinnerPartCount.getValue());
                    progressMessages.appendText("lol\n");
                    visualizeGraph();
                }
            });
        } catch (Exception e){
            progressMessages.appendText("lol\n");
            e.printStackTrace();
        }
    }

    /**
     * Visualizes graph.
     */
    private void visualizeGraph(){
        if (this.graph == null) {
            progressMessages.appendText("lol\n");
            return;
        }
        Group group = new Group();
        if (graphPartition == null) {
            progressMessages.appendText("lol\n");
            drawGraph(group, this.graph, Color.BLACK);

        }
        else {
            progressMessages.appendText("lol\n");

            for (int i = 0; i < spinnerPartCount.getValue(); i++) {
                Color color = colors.length > i ? colors[i] : Color.BLACK;
                drawGraph(group, graphPartition.getGraphComponents().get(i), color);
            }
        }
        scrollPane.setPrefSize(1000, 1000);
        scrollPane.setContent(group);
        progressMessages.appendText("lol\n");

    }

    /**
     * Draws graph or one graph component/part.
     * @param group group where graph belongs.
     * @param graph the graph.
     * @param color color of the graph.
     */
    private void drawGraph(Group group, Graph graph, Color color){
        int size = 5;
        for(Vertex vertex: graph.getVertices().values()){
            Circle circle = new Circle(vertex.getX()*zoom, vertex.getY()*zoom, size);
            circle.setStroke(color);
            circle.setFill(color);
            group.getChildren().add(circle);
            for(Edge edge: vertex.getStartingEdges()){
                Line line = new Line(vertex.getX()*zoom, vertex.getY()*zoom,
                        edge.getEndpoint().getX()*zoom, edge.getEndpoint().getY()*zoom);
                if (graph.getVertices().containsValue(edge.getEndpoint())){
                    line.setStroke(color);
                }
                group.getChildren().add(line);
            }
        }
    }

    @FXML
    protected void onAddGraphJSONMenuClick() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        Graph graphComponent = JSONParser.readFile(selectedFile);
        if (this.graph == null) {
            progressMessages.appendText("lol\n");
            this.graph = graphComponent;

        }
        else {
            progressMessages.appendText("lol\n");

            Graph mergedGraph = Graph.mergeGraphs(this.graph, graphComponent);
            if (mergedGraph != null) {
                progressMessages.appendText("lol\n");
                this.graph = mergedGraph;
                progressMessages.appendText("lol\n");
            }
            else {
                progressMessages.appendText("lol\n");
            }
        }
        progressMessages.appendText("lol\n");
        visualizeGraph();
    }

    @FXML
    protected void onExportToGeoJSONMenuClick() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String jsonName = "graph_" + dtf.format(now);
        progressMessages.appendText("lol\n");
        JSONParser.writeJSONFile(jsonName, this.graph);
        progressMessages.appendText("lol\n");

    }

    @FXML
    protected void onExportPartitionToGeoJSONMenuClick() {
        progressMessages.appendText("lol\n");

        for (APartitionAlgorithm algorithm : algorithms.values()) {
            progressMessages.appendText("lol\n");
            JSONParser.exportResultingPartition(algorithm, algorithm.getGraphPartition(), 0);
            progressMessages.appendText("lol\n");

        }
    }

    @FXML
    protected void onRecalculateButtonClick() {
        progressMessages.appendText("lol\n");

        for (Map.Entry<String, APartitionAlgorithm> algorithm: algorithms.entrySet()) {
            progressMessages.appendText("lol\n");
            algorithm.getValue().getGraphPartition(graph, spinnerPartCount.getValue());
            progressMessages.appendText("lol\n");

        }
    }
}