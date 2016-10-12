package xyz.casualcookie.ttsdownloader.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Stores the resources.
 * Install path is the url used in the game config file, for when installing.
 * DownloadPath is the path where the resources will be downloaded to create the install package.
 *
 * Substitution should be made according to this.
 * Install path will be in the config file, and used when copying files.
 * Download path will be used only when creating the package for installation.
 */
public class Resource{
    public enum Type{IMAGE,OBJECT,WORKSHOP}
    public enum DownloadState{SUCCESS,FAIL,UNDEFINED}


    public static final String SPECIAL_CHARACTERS_REMOVE = "[/|\\.|%|:|_|-|\\s+]";
    public static final String PATH_STRUCTURE_OBJECTS="Mods"+ File.separatorChar+"Models";
    public static final String PATH_STRUCTURE_IMAGES="Mods"+ File.separatorChar+"Images";
    public static final String PATH_STRUCTURE_WORKSHOP="Mods"+ File.separatorChar+"Workshop";

    private final String sourceFileName;
    private final String sourceFileExtension;
    private final URL sourceURL;
    private final Type type;

    private final String fileName;
    private final String fileSavePath;

    private DownloadState fetchStatus;

    /**
     * The source URL usually must be parsed in order to obtain the 2 required params.
     * @param sourceURL file URL from where everything else is constructed.
     * @param type Type of file if an Image or Object
     */
    public Resource(String sourceURL, String savePath, Type type) throws MalformedURLException {
        if(sourceURL == null || type == null)
            throw new NullPointerException();

        this.sourceURL = new URL(sourceURL);
        this.type=type;
        this.fetchStatus=DownloadState.UNDEFINED;

        this.sourceFileName=this.sourceURL.getFile().substring(this.sourceURL.getFile().lastIndexOf("/")+1);
        this.sourceFileExtension=sourceFileName.substring(sourceFileName.lastIndexOf(".")+1);

        this.fileName = this.sourceURL.toString().replaceAll(SPECIAL_CHARACTERS_REMOVE,"")+"."+this.sourceFileExtension;
        this.fileSavePath = savePath +File.separatorChar+getStructurePath();
    }

    /**
     * Returns the source file name.
     * According to TTS it is a concatenation of the path + name + extension with the source extension at the end.
     * @return The name of the file according to TTS cache.
     */
    public String getFileName(){
       return fileName;
    }


    public DownloadState getFetchStatus(){
        return fetchStatus;
    }
    public void setFetchStatus(DownloadState status) {
        this.fetchStatus = status;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public String getStructurePath(){
        String path;
        switch(type){
            case IMAGE: path = PATH_STRUCTURE_IMAGES; break;
            case OBJECT: path = PATH_STRUCTURE_OBJECTS;break;
            default: path = PATH_STRUCTURE_WORKSHOP;break;
        }
        return path+File.separatorChar+fileName;
    }
    public String getExtension() {
        return sourceFileExtension;
    }
    public String getURL() {
        return sourceURL.toString();
    }
    public Type getType() {
        return type;
    }


}
