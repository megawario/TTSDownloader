package xyz.casualcookie.ttsdownloader.model;

import com.sun.javafx.tk.Toolkit;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.*;
import java.nio.file.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Process to run to fetch the game
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
        File file;
        BufferedInputStream fis=null;
        String contentString;
        byte[] content;
        try {
            file = new File(gameFile);
            content = new byte[(int) file.length()];
            fis = new BufferedInputStream(new FileInputStream(file));
            fis.mark(Integer.MAX_VALUE); //we will reuse this so keep on buffer
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
                (o, arg) -> {
                    Resource resource = (Resource) arg;
                    if(resource.getFetchStatus() == Resource.DownloadState.UNDEFINED){return;} //ignore start notifications
                    if(resource.getFetchStatus() == Resource.DownloadState.SUCCESS){
                        console.appendText("Successfully Download "+resource.getURL()+" to "+resource.getFileSavePath()+"\n");
                    }
                    else {console.appendText("Failed Download "+resource.getURL()+"\n");}
                    progressBar.setProgress(progressBar.getProgress()+1.0/numberOfResources);
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
        //reset stream
        fis.reset();
        if(zipper!=null){
            zipper.compress(Resource.PATH_STRUCTURE_WORKSHOP + File.separatorChar+gameName + ".json",fis);
            zipper.close();
        }else{
            File target = new File(outputPath + File.separatorChar
                    + gameName + File.separatorChar + Resource.PATH_STRUCTURE_WORKSHOP + File.separatorChar + gameName + ".json");
            //create folder if does not exist
            if (target.getParentFile() != null) Files.createDirectories(target.getParentFile().toPath());
            System.out.println("copy!");
            Files.copy(fis,target.toPath(),StandardCopyOption.REPLACE_EXISTING);
        }

        fis.close();
        return true;
    }
}
