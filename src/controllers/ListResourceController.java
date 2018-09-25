package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.AlertGenerator;
import util.PathContainer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;

public class ListResourceController implements Controller{

    private PathContainer source;
    private ObservableList<String> resources = FXCollections.observableArrayList();

    public ListResourceController ( Path source ){
        this.source = new PathContainer(source);
        this.source.pathProperty().addListener((observable, oldValue, newValue) -> updateResources(newValue));
        updateResources(source);
    }

    private void updateResources(Path path){
        resources.clear();

        //if invalid source path
        if ( path == null || !Files.exists(path)){
            return;
        }

        try {
            resources.addAll(Files.readAllLines(path));
        } catch ( IOException ioe ) {
            AlertGenerator.showIoeAlert(ioe);
        }
    }

    public PathContainer getSource(){
        return source;
    }

    public ObservableList<String> getAvailableResources(){
        return resources;
    }

    public void addResource(String resource){
        resources.add(resource);
    }

    public void deleteResource(String resource){
        resources.remove(resource);
    }

    public void editResource(String oldResource, String newResource){
        deleteResource(oldResource);
        addResource(newResource);
    }

    @Override
    public void onAppClose(){
        try {
            Files.write(source.getPath(), resources, StandardOpenOption.CREATE);
        } catch ( IOException ioe ) {
            AlertGenerator.showIoeAlert(ioe);
        }
    }
}
