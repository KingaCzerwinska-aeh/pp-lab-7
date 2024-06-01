import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main extends Application {

    private TextField directoryPathField;
    private TextField searchField;
    private TextArea resultArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Browser and Search");

        directoryPathField = new TextField();
        directoryPathField.setPromptText("Enter directory path");
        searchField = new TextField();
        searchField.setPromptText("Enter search phrase");

        resultArea = new TextArea();
        resultArea.setPrefHeight(400);

        Button browseButton = new Button("Browse");
        browseButton.setOnAction(event -> browseDirectory());

        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> searchFiles());

        HBox hBox = new HBox(10, directoryPathField, browseButton);
        VBox vBox = new VBox(10, hBox, searchField, searchButton, resultArea);

        Scene scene = new Scene(vBox, 600, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void browseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            directoryPathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void searchFiles() {
        String directoryPath = directoryPathField.getText();

        if (directoryPath.isEmpty()) {
            resultArea.setText("Please provide a directory path.");
            return;
        }

        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            resultArea.setText("The provided path is not a directory.");
            return;
        }

        String searchPhrase = searchField.getText();
        StringBuilder results = new StringBuilder();
        searchInDirectory(directory, searchPhrase, results);

        resultArea.setText(results.toString());
    }

    private void searchInDirectory(File directory, String searchPhrase, StringBuilder results) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchInDirectory(file, searchPhrase, results);
                } else {
                    try {
                        if (containsPhrase(file, searchPhrase)) {
                            results.append(file.getAbsolutePath()).append("\n");
                        }
                    } catch (IOException e) {
                        results.append("Error reading file: ").append(file.getAbsolutePath()).append("\n");
                    }
                }
            }
        }
    }

    private boolean containsPhrase(File file, String searchPhrase) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(searchPhrase)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + file.getAbsolutePath(), e);
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
