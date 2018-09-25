package views.components;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import util.PathContainer;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class DirChooser extends GridPane {

    private static final DirectoryChooser dirChooser = new DirectoryChooser();
    static { dirChooser.setInitialDirectory(new File("./")); }

    private final PathContainer pathContainer;

    public DirChooser(PathContainer pathContainer, Stage stage, String text){

        text = text.trim();
        this.pathContainer = pathContainer;

        Label titleLabel = new Label(text + " Folder: ");
        titleLabel.setFont(new Font("Verdana", 20));

        Label selectedFolderLabel = new Label();
        selectedFolderLabel.setFont(new Font("Verdana", 20));
        selectedFolderLabel.textProperty().bind(pathContainer.filenameProperty());

        Button selectFolderButton = new Button("Pick " + text + " Folder");
        selectFolderButton .setOnAction(e -> {
            File tmp = dirChooser.showDialog(stage);
            if ( tmp != null ){
                this.pathContainer.setPath(tmp.toPath());
                stage.sizeToScene();
            }
        });

        add(titleLabel, 0, 0);
        add(selectedFolderLabel, 1, 0);
        add(selectFolderButton , 0, 1, 2, 1);
    }

}
