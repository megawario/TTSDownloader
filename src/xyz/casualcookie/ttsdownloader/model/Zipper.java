package xyz.casualcookie.ttsdownloader.model;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Archives and compresses the selected files.
 * It has a static method to compress a directory recursively.
 * It can also using the non-static structure to create a zip structure, adding elements using the <code>compress</code>
 * method.
 * In the end the <code>close</code> code must be called to close cleanly.
 * Created by Mpinto on 28/09/2016.
 */
public class Zipper {
    private ZipOutputStream zos;

    /**
     * Structure to zip files.
     * @param outputFile file where the zip archive will be created to.
     * @throws FileNotFoundException
     */
    public Zipper(File outputFile) throws FileNotFoundException {
        zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
    }

    /**
     * Compresses a file.
     * It adds the file to the zip entry and copies the content to the zip output stream.
     * @param archivePath path to archive on the zip file.
     * @param inputStream content of the file to zip.
     * @throws IOException
     */
    public synchronized void compress(String archivePath,InputStream inputStream) throws IOException {
        this.zos.putNextEntry(new ZipEntry(archivePath));
        Util.copy(inputStream,zos);
    }

    /**
     * Compresses the File. It opens a inputStream based of the file.
     * @param compressFile
     * @throws IOException
     */
    public void compress(File compressFile) throws IOException {
        compress(compressFile.getAbsolutePath(),new BufferedInputStream(new FileInputStream(compressFile)));
    }

    /**
     * Closes the zip creation stream.
     * Must be called at the end of the creation in order to close properly.
     * @throws IOException
     */
    public synchronized void close() throws IOException {
        if(zos!=null) zos.close();
    }

    /**
     * Given a directory and a output file, it will compress every subdirectory in the output file.
     * @param GameDirectory
     * @param outputFile
     * @throws IOException
     */
    public static void compressDir(File GameDirectory,File outputFile) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream( new FileOutputStream(outputFile)));
        List<File> subDirectories=new LinkedList<>(Arrays.asList(GameDirectory.listFiles()));
        BufferedInputStream origin = null;
        while(!subDirectories.isEmpty()){
            File f = subDirectories.remove(0);
            if(f.isDirectory()){
                subDirectories.addAll(Arrays.asList(f.listFiles()));
            }else{
                zos.putNextEntry(new ZipEntry(f.getAbsolutePath()));
                origin = new BufferedInputStream(new FileInputStream(f), Util.BUFFER);
                Util.copy(origin,zos);
                origin.close();
            }
        }
        zos.close();
    };
}
