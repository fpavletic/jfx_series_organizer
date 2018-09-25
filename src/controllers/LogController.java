package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.AlertGenerator;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class LogController implements Controller{

    private static LogController logger;
    private final ObservableList<String> log = FXCollections.observableArrayList();

    private Pattern filter = null;
    private ObservableList<String> filteredLog = FXCollections.observableArrayList();

    private LogController(){} //disable instantiation of this class

    public ObservableList<String> getLog (){
        return log;
    }

    public ObservableList<String> getFilteredLog(){
        return filteredLog;
    }

    public void print(String line){
        log.add(line);
        if ( filter == null || filter.matcher(line).find()){
            Platform.runLater(() -> filteredLog.add(line));
        }
    }

    public void setFilter(String f){
        filter = f == null || f.isEmpty() ? null : Pattern.compile(f);
        filteredLog.clear();
        log
            .stream()
            .filter(s -> filter == null ? true : filter.matcher(s).find())
            .forEach(s -> filteredLog.add(s));
    }

    @Override
    public void onAppClose(){
        Path logPath = Paths.get("./logs/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd_HH-mm-ss")) + ".log");
        try {

            if(Files.notExists(logPath.getParent())){
                Files.createFile(logPath).getParent();
            }

            if (Files.notExists(logPath) ){
                Files.createFile(logPath);
            }
            Files.write(logPath, log, StandardOpenOption.CREATE);
        } catch ( IOException ioe ) {
            AlertGenerator.showIoeAlert(ioe);
        }
    }

    public static LogController getInstance(){
        if ( logger == null ){
            logger = new LogController();
        }
        return logger;
    }
}
