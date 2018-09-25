package util;

import controllers.LogController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;

public class AlertGenerator {

    public static void showIoeAlert(IOException ioe){
        showIoeAlert(ioe, "An IO Exception has occured");
    }

    public static void showIoeAlert(IOException ioe, String title){
        showAlert(title, ioe.toString());
        LogController.getInstance().print("IOException: " + ioe.toString());
    }

    public static void showIaeAlert(IllegalArgumentException iae, String title){
        showAlert(title, iae.toString());
        LogController.getInstance().print("IllegalArgumentException: " + iae.toString());
    }

    private static void showAlert(String title, String message){
        Platform.runLater(() -> {Alert iaeAlert = new Alert(Alert.AlertType.ERROR);
            iaeAlert.setTitle(title);
            iaeAlert.setHeaderText(message);
            iaeAlert.show();
        });
    }
}
