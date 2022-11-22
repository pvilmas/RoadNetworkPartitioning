package bp.roadnetworkpartitioning;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
    private File coordinatesFile = null;
    /** File with edges information. */
    private File edgesFile = null;
    /** Graph to show. */
    private Graph graph = null;
    /** Instance of ScrollPane containing graph. */
    @FXML
    private ScrollPane scrollPane;
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
    /** TextField with number of vertices vertically in generated graph. */
    @FXML
    private TextField textFieldHorizontally;
    /** TextField with number of vertices horizontally in generated graph. */
    @FXML
    private TextField textFieldVertically;
    /** TextField with length of graph edges that will be generated. */
    @FXML
    private TextField textFieldLength;


    /**
     * Method called when button "Create graph from JSON File!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onInsertGraphJSONButtonClick(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        this.graph = JSONParser.readFile(selectedFile);
        visualizeGraph();
    }

    /**
     * Method called when button "Create graph!" is clicked.
     * This method opens a dialog for choosing a file with vertices coordinates.
     */
    @FXML
    protected void onCreateGraphButtonClick(){
        int verticesHorizontally = getNumberFromString(textFieldHorizontally.getText());
        int verticesVertically = getNumberFromString(textFieldVertically.getText());
        int edgeLength = getNumberFromString(textFieldLength.getText());
        this.graph = Graph.generateGraph(verticesHorizontally, verticesVertically, edgeLength);
        visualizeGraph();
    }

    private void visualizeGraph(){
        int zoom = 50;
        int size = 5;
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
    @FXML
    protected void onUploadCoordinatesButtonClick() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(true) { //TODO add any condition?
            coordinatesFile = selectedFile;
        }
    }


    /**
     * Method called when button "Upload file!" is clicked.
     * This method opens a dialog for choosing a file with edges info.
     */
    @FXML
    protected void onUploadEdgesButtonClick() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(true) { //TODO add any condition?
            edgesFile = selectedFile;
        }
    }


    /**
     * Method called when button "Create JSON File!" is clicked.
     * This method uses method createJSONFile from JSONParser class for creating JSON file.
     * And gets details from textFields for correct parsing of files.
     */
    @FXML
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
     * Gets integer from string.
     * @param text      String to convert to integer.
     * @return  integer made from string.
     */
    private int getNumberFromString(String text){
        //TODO security
        if(true){
            return Integer.parseInt(text.trim());
        }
        return -1;
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