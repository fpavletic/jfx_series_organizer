package views;

import controllers.ListResourceController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import views.components.FileChooser;

import java.util.Optional;


public class ListResourceView extends VBox{

    private String resourceName;
    private ListResourceController controller;
    private Stage stage;

    public ListResourceView(String resourceName, ListResourceController controller, Stage stage){
        super(10);
        setPadding(new Insets(15, 12, 15, 12));
        this.resourceName = resourceName;
        this.controller = controller;
        this.stage = stage;
        init();
    }

    private final void init(){

        getChildren().add(new FileChooser(controller.getSource(), stage, resourceName));

        final ListView<String> availableResources = new ListView<>(controller.getAvailableResources());
        availableResources.setPrefSize(300, 200);

        HBox hBox = new HBox(10);
        getChildren().add(hBox);
        hBox.getChildren().add(availableResources);

        final Button addResourceButton = new Button("Add " + resourceName);
        addResourceButton.setOnAction(e -> {
            final TextInputDialog addResourceDialog = new TextInputDialog("Enter new " + resourceName + " here...");
            addResourceDialog.setHeaderText("Add " + resourceName);
            Optional<String> newResource = addResourceDialog.showAndWait();
            if ( newResource.isPresent() ){
                controller.addResource(newResource.get());
            }
        });

        final Button deleteResourceButton = new Button("Del " + resourceName);
        deleteResourceButton.setOnAction(e -> {
            final Alert deleteResourceConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            deleteResourceConfirm.setTitle("Delete " + resourceName);
            deleteResourceConfirm.setContentText("Are you sure you want to delete " +
                    availableResources.getSelectionModel().getSelectedItem());
            Optional<ButtonType> deleteResourceConfirmResult = deleteResourceConfirm.showAndWait();
            if ( deleteResourceConfirmResult.isPresent() && deleteResourceConfirmResult.get().equals(ButtonType.OK)){
                controller.deleteResource(availableResources.getSelectionModel().getSelectedItem());
            }
        });

        final Button editResourceButton = new Button("Edit " + resourceName);
        editResourceButton.setOnAction(e -> {
            final TextInputDialog editResourceDialog = new TextInputDialog(availableResources.getSelectionModel().getSelectedItem());
            editResourceDialog.setHeaderText("Edit " + resourceName);
            Optional<String> editedResource = editResourceDialog.showAndWait();
            if ( editedResource.isPresent() ){
                controller.editResource(availableResources.getSelectionModel().getSelectedItem(), editedResource.get());
            }
        });

        final VBox vBox = new VBox(10);
        hBox.getChildren().add(vBox);
        vBox.getChildren().addAll(addResourceButton, deleteResourceButton, editResourceButton);
    }


}
