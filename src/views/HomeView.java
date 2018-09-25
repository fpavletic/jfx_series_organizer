package views;

import controllers.HomeController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ProgressBar;
import util.AlertGenerator;
import views.components.DirChooser;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeView extends VBox{

    private DirChooser source, target;
    private HomeController controller;
    private Stage stage;

    private CheckBox useSameCheckbox;
    private CheckBox deleteUnsupportedFilenameFormatsCheckbox;
    private CheckBox deleteUnsupportedExtensionsCheckbox;
    private CheckBox deleteSamplesCheckbox;

    private Button organizerButton;
    private ProgressBar organizerProgressBar;


    public HomeView(HomeController controller, Stage stage){
        super(10);
        setPadding(new Insets(15, 12, 15, 12));
        this.controller = controller;
        this.stage = stage;
        init();
    }

    private final void init(){
        source = new DirChooser(controller.getSource(), stage, "Source");
        target = new DirChooser(controller.getTarget(), stage, "Target");

        getChildren().addAll(source, target);

        useSameCheckbox = new CheckBox("Use the same directory for both source and target");
        useSameCheckbox.setOnAction( e -> {
            target.setDisable(useSameCheckbox.isSelected());
            if ( useSameCheckbox.isSelected() ){
                controller.getTarget().pathProperty().bind(controller.getSource().pathProperty());
            } else {
                controller.getTarget().pathProperty().unbind();
            }
        });

        deleteUnsupportedFilenameFormatsCheckbox = new CheckBox( "Delete files with unsupported filename formats");
        deleteUnsupportedFilenameFormatsCheckbox.selectedProperty().bindBidirectional(controller.deleteUnsupportedFilenameFormatProperty());

        deleteUnsupportedExtensionsCheckbox = new CheckBox( "Delete files with unsupported extensions");
        deleteUnsupportedExtensionsCheckbox.selectedProperty().bindBidirectional(controller.deleteUnsupportedExtensionProperty());

        deleteSamplesCheckbox = new CheckBox( "Delete sample files");
        deleteSamplesCheckbox.selectedProperty().bindBidirectional(controller.deleteSampleFilesProperty());

        organizerProgressBar = new ProgressBar();
        controller.getProgress().addListener(
            (obs, ov, nv) -> {
                Platform.runLater(() -> {
                    organizerProgressBar.setProgress(nv.doubleValue());
                    if ( nv.doubleValue() == (double) -1 ){
                        getChildren().remove(organizerProgressBar);
                    }
                    if ( nv.doubleValue() == (double) 0 ){
                        getChildren().add(organizerProgressBar);
                    }
                });
            });

        organizerButton = new Button("Organize");
        organizerButton.setOnAction( e ->
            new Thread( () -> {
                try {
                    controller.organize();
                } catch ( IOException ioe ){
                    AlertGenerator.showIoeAlert(ioe);
                }
            }).start()
        );

        getChildren().addAll(
                useSameCheckbox,
                deleteUnsupportedFilenameFormatsCheckbox,
                deleteUnsupportedExtensionsCheckbox,
                deleteSamplesCheckbox,
                organizerButton
        );
    }

}
