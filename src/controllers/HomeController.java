package controllers;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import util.PathContainer;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HomeController implements Controller {

    private LogController logger = LogController.getInstance();

    private PathContainer source;
    private PathContainer target;

    private ObservableList<String> regexes;
    private ObservableList<String> extensions;

    private BooleanProperty deleteUnsupportedFilenameFormat = new SimpleBooleanProperty();
    private BooleanProperty deleteUnsupportedExtension = new SimpleBooleanProperty();
    private BooleanProperty deleteSampleFiles = new SimpleBooleanProperty();

    private DoubleProperty organizerProgress = new SimpleDoubleProperty(-1);

    public HomeController(Path source, Path target, ObservableList<String> regexes, ObservableList<String> extensions){
        this.source = new PathContainer(source);
        this.target = new PathContainer(target);
        this.regexes = regexes;
        this.extensions = extensions;

        deleteUnsupportedFilenameFormat.addListener((observable, oldValue, newValue) -> logger.print("DeleteUnsupportedFilenameFormat: " + newValue));
        deleteUnsupportedExtension.addListener((observable, oldValue, newValue) -> logger.print("DeleteUnsupportedExtension: " + newValue));
        deleteSampleFiles.addListener((observable, oldValue, newValue) -> logger.print("DeleteSampleFiles: " + newValue));
    }

    public PathContainer getSource(){
        return source;
    }

    public PathContainer getTarget(){
        return target;
    }

    public void organize() throws IOException{

        logger.print("Organizing, source: " + source.toString() + ", target: " + ( target.getPath() == null ? source.toString() : target.toString() ) );
        List<Pattern> patterns = regexes.stream().map(Pattern::compile).collect(Collectors.toList());

        long subfolderCount = Files.list(source.getPath()).count();
        Platform.runLater(() -> organizerProgress.setValue(0));

        Files.walkFileTree(source.getPath(), new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException{
                if ( source.getPath().equals(dir.getParent())){
                    organizerProgress.setValue(organizerProgress.doubleValue() + 1.0 / subfolderCount);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{

                String extension = getExtension(file);

                // if unsupported extension
                if ( !extensions.contains(extension)) {
                    logger.print("Unsupported file extension: " + file);
                    if ( deleteUnsupportedExtension.get() ){
                        Files.delete(file);
                        logger.print("Deleting: " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                // if sample file
                if ( attrs.size() < Math.pow(10, 6) &&
                    containsIgnoreCase( file.getFileName().toString(), "sample" ) ){

                    logger.print("Sample file detected: " + file);
                    if ( deleteSampleFiles.get() ) {
                        Files.delete(file);
                        logger.print("Deleting: " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                boolean found = false;
                for ( Pattern pattern : patterns ){
                    Matcher matcher = pattern.matcher(file.getFileName().toString());

                    if (!matcher.find()){
                        continue;
                    }

                    found = true;

                    Path targetPath = target.getPath()
                            .resolve(capitalizeAfterSpace(matcher.group(1).replaceAll("[-\\.]", " ").trim().toLowerCase()))
                            .resolve(String.format("Season %s", matcher.group(2)))
                            .resolve(String.format("%s.s%se%s.%s", trim(matcher.group(1), ".").toLowerCase(), matcher.group(2), matcher.group(3), extension));

                    if ( !targetPath.equals(file) ) {
                        Files.createDirectories(targetPath.getParent());
                        Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        logger.print("Moving file: " + file);
                    }
                }

                // if not matched by any regex
                if ( !found ){
                    logger.print("Unsupported filename format: " + file);
                    if ( deleteUnsupportedFilenameFormat.get() ){
                        Files.delete(file);
                        logger.print("Deleting: " + file);
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException{

                if ( isEmpty(dir) ){
                    Files.delete(dir);
                    logger.print("Deleting empty directory: " + dir);
                }
                return FileVisitResult.CONTINUE;
            }

            private boolean isEmpty(Path dir) throws IOException{
                try ( DirectoryStream<Path> tmp = Files.newDirectoryStream(dir)){
                    return !tmp.iterator().hasNext();
                }
            }

        });

        organizerProgress.setValue(-1);
        logger.print("Finished organizing!");

    }

    public BooleanProperty deleteUnsupportedFilenameFormatProperty(){
        return deleteUnsupportedFilenameFormat;
    }

    public BooleanProperty deleteUnsupportedExtensionProperty(){
        return deleteUnsupportedExtension;
    }

    public BooleanProperty deleteSampleFilesProperty(){
        return deleteSampleFiles;
    }

    public DoubleProperty getProgress(){
        return organizerProgress;
    }

    @Override
    public void onAppClose(){
        //Nothing to save here
    }

    /**
     * Method capitalizes every letter position after a whitespace character
     * @param text - String upon which to act
     * @return String with every letter positioned after a whitespace character capitalized
     */
    private static String capitalizeAfterSpace(String text){

        char[] textAsArray = text.trim().toCharArray();

        for ( int i = 0; i < textAsArray.length; i++ ){
            if ( i == 0 || textAsArray[i - 1] == ' '){
                textAsArray[i] = Character.toUpperCase(textAsArray[i]);
            }
        }

        return new String(textAsArray);

    }

    /**
     * Method gets the extension of the provided Path in String form
     * @param path - Path upon which to act
     * @return String containing the provided Path's extension
     */
    private static String getExtension (Path path){
        String filename = path.getFileName().toString();
        int lastIndexOfDot = filename.lastIndexOf('.');
        return lastIndexOfDot == -1 ? "" : filename.substring(lastIndexOfDot + 1).toLowerCase();
    }

    /**
     * Method trims all provided characters from the end and beginning of the provided String
     * @param text - String to be trimmed
     * @param trimmable - String containing all characters that we're allowed to trim
     * @return trimmed String
     */
    private static String trim (String text, String trimmable){
        while ( trimmable.indexOf(text.charAt(0)) != -1){
            text = text.substring(1);
        }

        while ( trimmable.indexOf(text.charAt(text.length() - 1)) != -1 ){
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    /**
     * Fast method for checking whether the first provided String contains the second provided String regardless of case.
     *
     * Always true if possibleSubstring is empty.
     *
     * @param string - string on which to do the check
     * @param possibleSubstring - substring we're checking for
     * @return true if string contains possibleSubstring, false otherwise
     */
    private static boolean containsIgnoreCase (String string, String possibleSubstring){
        final int length = possibleSubstring.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(possibleSubstring.charAt(0));
        final char firstUp = Character.toUpperCase(possibleSubstring.charAt(0));

        for (int i = string.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = string.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (string.regionMatches(true, i, possibleSubstring, 0, length))
                return true;
        }

        return false;
    }

}
