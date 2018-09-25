package util;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.nio.file.Path;

//DONE
public class PathContainer {

    private Property<Path> path;
    private StringProperty filename;

    public PathContainer(Path path){
        this.path = new SimpleObjectProperty<>();
        this.path.setValue(path);

        filename = new SimpleStringProperty();
        filename.setValue(path == null ? "None" : path.toString());

        this.path.addListener((observable, oldValue, newValue) ->
                filename.setValue( newValue == null ? "None" : newValue.toString()));
    }

    public void setPath(Path path){
        this.path.setValue(path);
    }

    public Path getPath(){
        return path.getValue();
    }

    public Property<Path> pathProperty(){
        return path;
    }

    public String getFilename(){
        return filename.get();
    }

    public StringProperty filenameProperty(){
        return filename;
    }

    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        PathContainer that = (PathContainer) o;

        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode(){
        return path != null ? path.hashCode() : 0;
    }

}
