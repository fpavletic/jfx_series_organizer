package views;


import controllers.Controller;
import controllers.HomeController;
import controllers.ListResourceController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import util.AlertGenerator;
import controllers.LogController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application{

    private LogController logger = LogController.getInstance();
    private List<Controller> controllers = new ArrayList<>();

    @Override
    public void start(Stage stage){

        //<editor-fold desc="Settings">
        Map<String, Path> settings = new HashMap<>();
        Path settingsPath = Paths.get("./settings.ini");
        if ( Files.exists(settingsPath.toAbsolutePath() )){
            try {
                Files.readAllLines(settingsPath)
                        .stream()
                        .filter(s -> !s.startsWith("//"))
                        .forEach(s -> {
                            String[] split = s.split(": ", 2);
                            if ( split.length <= 1 ){
                                logger.print("Unable to parse setting: " + s);
                                return;
                            }
                            settings.put(split[0], Paths.get(split[1]));
                        });
            } catch ( IOException ioe ) {
                AlertGenerator.showIoeAlert(ioe, "Unable to open settings file!");
            }
        }
        //</editor-fold>

        final LogView logView = new LogView(stage);
        final Tab logTab = new Tab("Log");
        logTab.setContent(logView);
        controllers.add(logger);

        final ListResourceController regexController =
                new ListResourceController(settings.get("Regexes"));
        final ListResourceView regexView =
                new ListResourceView("Regex", regexController, stage);
        final Tab regexTab = new Tab("Regex");
        regexTab.setContent(regexView);
        controllers.add(regexController);

        final ListResourceController extensionController =
                new ListResourceController(settings.get("Extensions"));
        final ListResourceView extensionView =
                new ListResourceView("Extension", extensionController, stage);
        final Tab extensionTab = new Tab("Extensions");
        extensionTab.setContent(extensionView);
        controllers.add(extensionController);

        final HomeController homeController =
                new HomeController(settings.get("Source"), settings.get("Target"), regexController.getAvailableResources(), extensionController.getAvailableResources());
        final HomeView homeView = new HomeView(homeController, stage);
        final Tab homeTab = new Tab("Home");
        homeTab.setContent(homeView);
        controllers.add(homeController);

        final TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(homeTab, regexTab, extensionTab, logTab);

        stage.setTitle("Series Organizer");
        stage.setScene(new Scene(tabs));
        stage.setOnCloseRequest(e -> controllers.forEach(Controller::onAppClose));
        stage.show();
    }

}
