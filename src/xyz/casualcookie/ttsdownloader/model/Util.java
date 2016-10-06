package xyz.casualcookie.ttsdownloader.model;

import java.io.*;

/**
 * Created by Mpinto on 04/10/2016.
 */
public class Util {
    static final int BUFFER = 2048;

    /**
     * Copies the content of the inputStream to the outputStream provided.
     * It will read according to the value of <code>BUFFER</code>
     * @param inputStream The stream to be replicated.
     * @param outputStream The stream to be copied.
     * @throws IOException
     */
    public static void copy(InputStream inputStream,OutputStream outputStream) throws IOException {
        byte data[] = new byte[BUFFER];
        int count;
        while((count = inputStream.read(data, 0, BUFFER)) != -1) {
            outputStream.write(data, 0, count);
        }
    }
}
