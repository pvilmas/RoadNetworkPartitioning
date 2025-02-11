package bp.roadnetworkpartitioning;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

/**
 * Main window of app.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class MainView extends Application {

    private String jsonFilePath;
    private String xmlFilePath;
    private double initialZoom = 10.0;

    @Override
    public void init() {
        Parameters parameters = getParameters();
        var namedParams = parameters.getNamed();
        if (namedParams.containsKey("json")) jsonFilePath = namedParams.get("json");
        if (namedParams.containsKey("xml")) xmlFilePath = namedParams.get("xml");
        if (namedParams.containsKey("zoom")) initialZoom = Double.parseDouble(namedParams.get("zoom"));
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        MainController.setStage(stage);
        MainController controller = fxmlLoader.getController();
        controller.setAlgorithms();
        if (jsonFilePath != null) controller.onJSONParameter(jsonFilePath);
        if (xmlFilePath != null) controller.onXMLParameter(xmlFilePath);
        stage.setTitle("Main Window - Road Network Partitioning");
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        controller.onZoomParameter(initialZoom);
    }

    /**
     * Main method launches the app.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
