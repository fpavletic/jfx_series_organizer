package views.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.PathContainer;

import java.io.File;

public class FileChooser extends GridPane {

    private static final javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
    static { fileChooser.setInitialDirectory(new File("./")); }

    private final PathContainer pathContainer;

    public FileChooser(PathContainer pathContainer, Stage stage, String text){

        text = text.trim();
        this.pathContainer = pathContainer;

        Label titleLabel = new Label(text + " File: ");
        titleLabel.setFont(new Font("Verdana", 20));

        Label selectedFileLabel = new Label();
        selectedFileLabel.textProperty().bind(pathContainer.filenameProperty());
        titleLabel.setFont(new Font("Verdana", 20));

        Button selectFileButton = new Button("Pick " + text + " File");
        selectFileButton .setOnAction(e -> {
            File tmp = fileChooser.showOpenDialog(stage);
            if ( tmp != null ){
                this.pathContainer.setPath(tmp.toPath());
                stage.sizeToScene();
            }
        });

        add(titleLabel, 0, 0);
        add(selectedFileLabel, 1, 0);
        add(selectFileButton , 0, 1, 2, 1);
    }

}

