package xyz.casualcookie.ttsdownloader.model;

import com.sun.javafx.tk.Toolkit;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.*;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Mpinto on 29/09/2016.
 */
public class Process extends Task<Boolean>{
    private final TextArea console;
    private final ProgressBar progressBar;
    private final String gameFile,outputPath,gameName;
    private final boolean isDryRun,isZip;

    public Process(TextArea console,ProgressBar progressBar,String gameFile,String outputPath,String gameName,boolean isDryRun,boolean isZip){
        this.console = console;
        this.progressBar = progressBar;
        this.gameFile=gameFile;
        this.outputPath=outputPath;
        this.gameName=gameName;
        this.isDryRun=isDryRun;
        this.isZip=isZip;
    }


    @Override
    protected Boolean call() throws Exception {
        File file=null;
        FileInputStream fis=null;
        String contentString=null;
        byte[] content=null;
        try {
            file = new File(gameFile);
            content = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            fis.read(content);
            contentString = new String(content);
        }catch(FileNotFoundException e){
            console.appendText("File Not Found: "+e.getMessage()+"\n");
            if(fis!=null) fis.close();
            return false;
        } catch (IOException e) {
            console.appendText("Error while opening the file "+gameFile+" "+e.getMessage());
            return false;
        }

        //1 - Parse File
        Parser gp = new Parser(contentString,outputPath+File.separatorChar+gameName);
        final int numberOfResources = gp.parse();

        //2 - Download files
        console.appendText("Downloading " + numberOfResources + " resources\n");

        //create observer to notify the view
        Observer observer =
                new Observer(){
                    @Override
                    public void update(Observable o, Object arg) {
                        Resource resource = (Resource) arg;
                        if(resource.getFetchStatus() == Resource.DownloadState.UNDEFINED){return;} //ignore start notifications
                        if(resource.getFetchStatus() == Resource.DownloadState.SUCCESS){
                            console.appendText("Successfully Download "+resource.getURL()+" to "+resource.getFileSavePath()+"\n");
                        }
                        else {console.appendText("Failed Download "+resource.getURL()+"\n");}
                        progressBar.setProgress(progressBar.getProgress()+1.0/numberOfResources);
                    }
                };

        //3 - setup zipper to zip on download
        Zipper zipper = null;
        if(isZip){
            zipper = new Zipper(new File(outputPath+File.separatorChar+gameName+".zip"));
            console.appendText("Zipping to: "+outputPath+File.separatorChar+gameName+".zip");
        }

        //4 - begin download

        Downloader downloader = new Downloader(gp.getResourcesSet(),isDryRun,zipper,observer);
        downloader.download();

        //5 - Save copy workshop file
        if(zipper!=null){
            zipper.compress(Resource.PATH_STRUCTURE_WORKSHOP + File.separatorChar+gameName + ".json",fis);
            zipper.close();
        }else{
            Files.copy(fis,(new File(outputPath + File.separatorChar
                    + gameName + File.separatorChar + Resource.PATH_STRUCTURE_WORKSHOP + File.separatorChar+gameName + ".json")).toPath());
        }

        fis.close();
        return true;
    }
}
