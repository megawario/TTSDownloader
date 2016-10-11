package xyz.casualcookie.ttsdownloader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import xyz.casualcookie.ttsdownloader.controller.Controller;

import java.io.*;

/**
 * Starts the app
 * Created by Mpinto on 09/09/2016.
 */
public class App extends Application{
    private Stage stage;
    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser=new DirectoryChooser();

    public File pickFile(){
        return fileChooser.showOpenDialog(stage);
    }

    public File pickFolder(){
        return directoryChooser.showDialog(stage);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader((App.class.getClassLoader().getResource("fxml/TTSDownloader.fxml")));
        Parent root = loader.load();
        ((Controller) loader.getController()).setApplication(this);

        Scene scene = new Scene(root);
        stage.setTitle("TTSDownloader");
        stage.setScene(scene);
        this.stage=stage;
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(App.class,args);
    }
}
