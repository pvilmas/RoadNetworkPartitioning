package bp.roadnetworkpartitioning;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that serves as a controller for dialog with color picking for each part.
 *  @author Lucie Roy
 *  @version 28-12-2023
 */
public class PartColorsDialogController extends Dialog<Color[]> {

    /** Picked colors. */
    private final Color[] colors;
    /** Color pickers for each part. */
    private final ColorPicker[] colorPickers;
    /** Main button of dialog for picking colors of each part. */
    @FXML
    private ButtonType applyAndCloseButtonType;
    /** VBox containing HBoxes with color pickers. */
    @FXML
    protected VBox vBox;

    /**
     * Constructor of dialog for color picking with given stage/window for dialog,
     * number of parts and previously picked colors.
     * @param window        stage/window hosting dialog.
     * @param partsCount    number of parts.
     * @param colors        previously picked colors.
     * @throws IOException  when loading fxml.
     */
    public PartColorsDialogController(Window window, int partsCount, Color[] colors) throws IOException {
        this.colors = new Color[partsCount];
        this.colorPickers = new ColorPicker[partsCount];
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("part-colors-dialog.fxml"));
        fxmlLoader.setController(this);
        DialogPane dialogPane = fxmlLoader.load();
        dialogPane.lookupButton(applyAndCloseButtonType).addEventFilter(ActionEvent.ANY, this::onApplyAndCloseButtonClick);
        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        setTitle("Color of each part");
        setHeaderText("Pick color for each part:");
        setDialogPane(dialogPane);
        setResultConverter(buttonType -> {
            if(!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                return null;
            }
            return this.colors;
        });
        setPartColors(colors, partsCount);
    }

    /**
     * Sets previously picked colors or default value.
     * @param colors        previously picked colors.
     * @param partsCount    number of parts.
     */
    private void setPartColors(Color[] colors, int partsCount) {
        for (int i = 0; i < partsCount; i++){
            HBox hBox = new HBox(10);
            Label labelPartNumber = new Label("Part " + i + ": ");
            if (i < colors.length) {
                this.colors[i] = colors[i];
            }
            else {
                this.colors[i] = Color.BLACK;
            }
            ColorPicker picker = new ColorPicker(this.colors[i]);
            colorPickers[i] = picker;
            hBox.getChildren().addAll(labelPartNumber, picker);
            hBox.setAlignment(Pos.CENTER);
            vBox.getChildren().add(hBox);
        }
    }

    /**
     * Saves picked colors.
     * @param event triggering event.
     */
    @FXML
    protected void onApplyAndCloseButtonClick(ActionEvent event){
        for (int i = 0; i < colorPickers.length; i++){
            this.colors[i] = colorPickers[i].getValue();
        }
    }
}
