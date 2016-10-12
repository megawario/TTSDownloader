package xyz.casualcookie.ttsdownloader.model;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Downloads the resources from the web into a defined folder.
 * If threaded is needed must be instantiated.
 *
 * Because we are using the cache system of TTS, the name of the file is the install path without the special characters.
 *
 * Class has observable in order to notify the number of downloaded objects while downloading.
 * Created by Mpinto on 09/09/2016.
 */
public class Downloader extends Observable {
    private static final int TIMEOUT_MINUTES = 5;

    private final Zipper zipper;
    private final boolean dryRun;
    private final Set<Resource> resourcesSet;

    private int successes;

    /**
     * Constructs a Downloader to manage the downloads.
     * A new class must be created for each new resourcesSet.
     * @param resourcesSet Set containing the resources to be downloaded
     * @param dryRun if true, the files will not be downloaded.
     * @param zipper Inject Class responsible for Archiving files, if null files will not be archived.
     * @param observer Injects an observer to notify when download is completed.
     */
    public Downloader(Set<Resource> resourcesSet,boolean dryRun,Zipper zipper, Observer observer){
        if(resourcesSet == null)
            throw new NullPointerException();
        this.resourcesSet=resourcesSet;
        this.zipper = zipper;
        this.dryRun = dryRun;
        if(observer!=null) this.addObserver(observer);
    }

    /**
     * Downloads all files as key values of the resourceSet using multithreading.
     *
     * @return number of successfully downloaded files - this must equal the size of the resourceSet
     * @throws InterruptedException
     * @throws IOException
     */
    public int download() throws InterruptedException, IOException {
        ExecutorService exec = Executors.newCachedThreadPool();

        for (Resource resource : resourcesSet) {
            exec.submit(new Worker(resource));
        }
        exec.shutdown();
        exec.awaitTermination(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        return successes;
    }

    /**
     * Downloads resource, to a file or a selected zipper
     * @param resource game resource
     * @return time in milliseconds that took to download
     * @throws IOException
     */
    private long download(Resource resource) throws IOException {
        InputStream is=null;
        try {
            long startTime = System.currentTimeMillis();
            is = new URL(resource.getURL()).openStream();

            if (zipper != null) {
                zipper.compress(resource.getStructurePath(), is);
            } else {
                File file = new File(resource.getFileSavePath());

                //create path for the file
                if (file.getParentFile() != null) Files.createDirectories(file.getParentFile().toPath());
                Files.copy(is, file.toPath());
            }
            return startTime - System.currentTimeMillis();
        } finally {
            if(is!=null) is.close();
        }
    }

    private synchronized void notifyObservers(Resource resource,Resource.DownloadState state){
        if(state == Resource.DownloadState.SUCCESS)
            successes++;
        resource.setFetchStatus(state);
        setChanged();
        notifyObservers(resource);
    }


    /**
     * Worker thread for multithreading.
     */
    private class Worker implements Runnable{

        private final Resource resource;

        protected Worker(Resource resource) throws IOException {
            this.resource = resource;
        }

        @Override
        public void run() {
            try {
                notifyObservers(resource,Resource.DownloadState.UNDEFINED);
                if(!dryRun) download(resource);
                notifyObservers(resource,Resource.DownloadState.SUCCESS);
            } catch (IOException e) {
                notifyObservers(resource,Resource.DownloadState.FAIL);
            }
        }
    }
}
