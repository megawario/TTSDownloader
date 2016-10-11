package xyz.casualcookie.ttsdownloader.controller;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import xyz.casualcookie.ttsdownloader.App;
import xyz.casualcookie.ttsdownloader.model.*;
import xyz.casualcookie.ttsdownloader.model.Process;


import java.io.*;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;

/**
 * Controller for fx view
 * Created by Mpinto on 11/09/2016.
 */
public class TTSController implements Controller {
    private App application;
    private boolean isActionEnabled=true;


    @FXML private TextField gameFile;
    @FXML private TextField gameName;
    @FXML private TextField outputPath;
    @FXML private TextArea console;
    @FXML private ProgressBar progress;

    @FXML private CheckBox isDryRunCheckbox;
    @FXML private CheckBox isZipCheckbox;

    @FXML void initialize(){
        console.setScrollTop(Double.MIN_VALUE);
    }

    @FXML protected void downloadButtonAction(ActionEvent event) throws IOException, InterruptedException {
        if(!isActionEnabled){
            console.appendText("Running previous action");
            return;
        }
        String gameName = this.gameName.getText();
        String gameFile = this.gameFile.getText();
        String outputPath = this.outputPath.getText();

        progress.setProgress(0);
        console.appendText("Downloading\n");
        console.appendText("Game Json Path: "+gameFile+"\n");
        console.appendText("Game Name: "+gameName+"\n");
        console.appendText("Output Path: "+outputPath+"\n");


        //download files
        Task<Boolean> downloadFiles = new Process(console,progress,gameFile,outputPath,gameName,isDryRunCheckbox.isSelected(),isZipCheckbox.isSelected());
        downloadFiles.setOnSucceeded((event1) -> {console.appendText("Process was successful\n");isActionEnabled=true;});
        downloadFiles.setOnFailed(event1 -> {console.appendText("Process was unsuccessful\n");isActionEnabled=true;});
        downloadFiles.setOnCancelled(event1 -> {console.appendText("Process was canceled\n");isActionEnabled=true;});

        isActionEnabled=false;
        new Thread(downloadFiles).start();
    }

    @FXML protected void gameFilePicker(ActionEvent event){
        File file = application.pickFile();
        if(file!=null) gameFile.setText(file.getAbsolutePath());
    }

    @FXML protected void outputPathPicker(ActionEvent event){
        File folder = application.pickFolder();
        if(folder!=null) outputPath.setText(folder.getAbsolutePath());
    }

    @Override
    public void setApplication(App application) {
        this.application = application;
    }
}
