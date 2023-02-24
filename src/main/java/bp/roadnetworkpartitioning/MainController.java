package bp.roadnetworkpartitioning;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Controller of main window of the app.
 */
public class MainController {
    /** Instance of main stage. */
    private static Stage stage = null;
    /** File with vertices coordinates. */
    private File coordinatesFile = null;
    /** File with edges information. */
    private File edgesFile = null;
    /** Graph to show. */
    private Graph graph = null;
    /** Instance of ScrollPane containing graph. */
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField textZoom;
    @FXML
    private Label labelZoom;
    /** TextField with number of line where data starts in edge file. */
    private final TextField textFieldLineEdge = new TextField();
    /** TextField with number of column where is delimiter of edge in edge file. */
    private final TextField textFieldDelimiterEdge = new TextField();
    /** TextField with number of column where is delimiter of node in node file. */
    private final TextField textFieldDelimiterVertex = new TextField();
    /** TextField with number of column where is start point of edge in edge file. */
    private final TextField textFieldColStart = new TextField();
    /** TextField with number of column where is endpoint of edge in edge file. */
    private final TextField textFieldColEnd = new TextField();
    /** TextField with number of column where is length of edge in edge file. */
    private final TextField textFieldColLength = new TextField();
    /** TextField with number of line where data starts in node file. */
    private final TextField textFieldLineVertex = new TextField();
    /** TextField with number of column where is ID of node in node file. */
    private final TextField textFieldColID = new TextField();
    /** TextField with number of column where is x-coordinate of node in node file. */
    private final TextField textFieldColX = new TextField();
    /** TextField with number of column where is y-coordinate of node in node file. */
    private final TextField textFieldColY = new TextField();
    /** TextField with number of vertices vertically in generated graph. */
    private TextField textFieldHorizontally = new TextField();
    /** TextField with number of vertices horizontally in generated graph. */
    private TextField textFieldVertically = new TextField();
    /** TextField with length of graph edges that will be generated. */
    private TextField textFieldLength = new TextField();
    private static int zoom = 10;
    private static final int size = 5;
    private final ScrollPane scrollPaneNode = new ScrollPane();
    private final ScrollPane scrollPaneEdge = new ScrollPane();


    /**
     * Method called when MenuItem "Insert Graph" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onInsertGraphJSONMenuClick(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        this.graph = JSONParser.readFile(selectedFile);
        visualizeGraph(zoom);
    }

    /**
     *
     */
    @FXML
    protected  void onIncreaseButtonClick(){
        zoom += 5;
        labelZoom.setText(""+zoom);
        visualizeGraph(zoom);
    }

    /**
     *
     */
    @FXML
    protected  void onDecreaseButtonClick(){
        zoom -= 5;
        labelZoom.setText(""+zoom);
        visualizeGraph(zoom);
    }

    /**
     * Method called when zoom button clicked.
     * This method zooms graph.
     */
    @FXML
    protected  void onZoomButtonClick(){
        zoom = getNumberFromString(textZoom.getText());
        labelZoom.setText(""+zoom);
        visualizeGraph(zoom);
    }

    /**
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onCreateGraphMenuClick(){
        Dialog dialog = new Dialog();
        VBox vbox = new VBox(10.0);
        Label lblVerticesH = new Label("Number of vertices horizontally");
        textFieldHorizontally = new TextField();
        Label lblVerticesV = new Label("Number of vertices vertically");
        textFieldVertically = new TextField();
        Label lblLength = new Label("Length of graph edges");
        textFieldLength = new TextField();
        ButtonType btnCreateGraph = new ButtonType("Create graph!", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(btnCreateGraph);
        vbox.getChildren().addAll(lblVerticesH, textFieldHorizontally, lblVerticesV,
                textFieldVertically, lblLength, textFieldLength);
        dialog.setTitle("Graph Generation");
        dialog.setHeaderText("Insert parameters of new graph:");
        dialog.getDialogPane().setContent(vbox);
        dialog.setResultConverter(b -> {
            if (b == btnCreateGraph) {
                onCreateGraphButtonClick();
            }
            return null;
        });
        dialog.showAndWait();

    }

    /**
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    protected void onCreateGraphButtonClick(){
        int verticesHorizontally = getNumberFromString(textFieldHorizontally.getText());
        int verticesVertically = getNumberFromString(textFieldVertically.getText());
        int edgeLength = getNumberFromString(textFieldLength.getText());
        this.graph = Graph.generateGraph(verticesHorizontally, verticesVertically, edgeLength);
        visualizeGraph(zoom);
    }

    /**
     * Visualizes attribute graph.
     * @param zoom  how much bigger the graph should be.
     */
    private void visualizeGraph(int zoom){
        Group group = new Group();
        for(Vertex vertex: this.graph.getVertices().values()){
            group.getChildren().add(new Circle(vertex.getX()*zoom, vertex.getY()*zoom, size));
            for(int j = 0; j < vertex.getEdges().size(); j++){
                Edge edge = vertex.getEdges().get(j);
                group.getChildren().add(new Line(vertex.getX()*zoom, vertex.getY()*zoom,
                        edge.getEndpoint().getX()*zoom, edge.getEndpoint().getY()*zoom));
            }
        }
        scrollPane.setPrefSize(1000, 1000);
        scrollPane.setContent(group);
    }


    /**
     * Method called when button "Upload file!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    private void onUploadCoordinatesButtonClick() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null) {
            coordinatesFile = selectedFile;
            showFirstTwentyLinesOfFile(edgesFile, scrollPaneNode);
        }
    }


    /**
     * Method called when button "Upload file!" is clicked.
     * This method opens a dialog for choosing a file with edges info.
     */
    private void onUploadEdgesButtonClick() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null) {
            edgesFile = selectedFile;
            showFirstTwentyLinesOfFile(edgesFile, scrollPaneEdge);
        }
    }

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


    /**
     * Method called when button "Create JSON File!" is clicked.
     * This method uses method createJSONFile from JSONParser class for creating JSON file.
     * And gets details from textFields for correct parsing of files.
     */
    protected void onCreateJSONButtonClick(){
        int[] details = new int[8];
        details[0] = getNumberFromString(textFieldLineVertex.getText());
        details[1] = getNumberFromString(textFieldLineEdge.getText());
        details[2] = getNumberFromString(textFieldColStart.getText());
        details[3] = getNumberFromString(textFieldColEnd.getText());
        details[4] = getNumberFromString(textFieldColLength.getText());
        details[5] = getNumberFromString(textFieldColID.getText());
        details[6] = getNumberFromString(textFieldColX.getText());
        details[7] = getNumberFromString(textFieldColY.getText());
        String delimiterEdge = textFieldDelimiterEdge.getText();
        String delimiterVertex = textFieldDelimiterVertex.getText();
        JSONParser.createJSONFile(details, delimiterEdge, delimiterVertex, coordinatesFile, edgesFile, "graph");
    }

    /**
     * Method called when button "Create JSON File!" is clicked.
     * This method uses method createJSONFile from JSONParser class for creating JSON file.
     * And gets details from textFields for correct parsing of files.
     */
    @FXML
    protected void onCreateJSONMenuClick(){
        Dialog dialog = new Dialog();
        HBox hbox = new HBox(5.0);
        VBox vbox = new VBox(5.0);
        VBox vbox1 = new VBox(5.0);
        VBox vbox2 = new VBox(5.0);
        hbox.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);
        vbox1.setAlignment(Pos.CENTER);
        vbox2.setAlignment(Pos.CENTER);
        Label lblFirstLineEdge = new Label("Number of first line with data in edge file");
        Label lblDelColEdge = new Label("Delimiter of columns in edge file");
        Label lblColStartPointsEdge = new Label("Column with start points in edge file");
        Label lblColEndpointsEdge = new Label("Column with endpoints in edge file");
        Label lblColLengthEdge = new Label("Column with lengths in edge file");
        Label lblFirstLineNode = new Label("Number of first line with data in node file");
        Label lblDelColNode = new Label("Delimiter of columns in node file");
        Label lblColIDNode = new Label("Column with IDs in node file");
        Label lblColXNode = new Label("Column with x-coordinates in node file");
        Label lblColYNode = new Label("Column with y-coordinates in node file");
        Label lblFileNode = new Label("Upload file with node coordinates");
        Button btnFileNode = new Button("Upload file!");
        btnFileNode.setOnAction(e -> onUploadCoordinatesButtonClick());
        Label lblFileEdge = new Label("Upload file with graph edges");
        Button btnFileEdge = new Button("Upload file!");
        btnFileEdge.setOnAction(e -> onUploadEdgesButtonClick());
        ButtonType btnCreateJSONFile = new ButtonType("Create JSON File!", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(btnCreateJSONFile);
        vbox1.getChildren().addAll(lblFileEdge, btnFileEdge, scrollPaneEdge, lblFirstLineEdge,
                textFieldLineEdge,lblDelColEdge, textFieldDelimiterEdge, lblColStartPointsEdge,
                textFieldColStart, lblColEndpointsEdge, textFieldColEnd, lblColLengthEdge, textFieldColLength);
        vbox2.getChildren().addAll(lblFileNode, btnFileNode, scrollPaneNode, lblFirstLineNode, textFieldLineVertex,
                lblDelColNode, textFieldDelimiterVertex, lblColIDNode, textFieldColID, lblColXNode, textFieldColX,
                lblColYNode, textFieldColY);
        hbox.getChildren().addAll(vbox1, vbox2);
        vbox.getChildren().addAll(hbox);
        dialog.setTitle("Graph Generation");
        dialog.setHeaderText("Insert parameters of new graph:");
        dialog.getDialogPane().setContent(vbox);
        dialog.setResizable(true);
        dialog.setResultConverter(b -> {
            if (b == btnCreateJSONFile) {
                onCreateJSONButtonClick();
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Gets positive integer from string.
     * @param text      String to convert to integer.
     * @return  integer made from string.
     */
    private int getNumberFromString(String text){
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