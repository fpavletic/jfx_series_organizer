package views;

import controllers.LogController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogView extends VBox {

    private Stage stage;

    public LogView(Stage stage){
        super(10);
        setPadding(new Insets(15, 12, 15, 12));
        this.stage = stage;
        init();
    }

    private void init(){

        final TextField filterTextField = new TextField();
        filterTextField.setPromptText("Enter your regex filter here...");
        final Button filterButton = new Button("Apply Filter");
        filterButton.setOnAction(e -> {
            LogController.getInstance().setFilter(filterTextField.getText());
        });

        HBox hbox = new HBox(10);
        getChildren().add(hbox);
        hbox.getChildren().addAll(filterTextField, filterButton);

        final ListView<String> filteredLogListView = new ListView<>(LogController.getInstance().getFilteredLog());
        filteredLogListView.setPrefHeight(300);

        getChildren().add(filteredLogListView);
    }
}
