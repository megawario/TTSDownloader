import xyz.casualcookie.ttsdownloader.model.Downloader;
import org.junit.Assert;
import org.junit.Test;
import xyz.casualcookie.ttsdownloader.model.Resource;
import xyz.casualcookie.ttsdownloader.model.Zipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Test download functionality
 * Created by Mpinto on 09/09/2016.
 */
public class DownloaderTester {

    @Test
    public void DownloadWithoutZip() throws IOException, InterruptedException {
        Resource r = new Resource("https://dl.dropboxusercontent.com/u/7205900/Battle%20Line/cards_troops_back.jpg",
                "battleLine", Resource.Type.IMAGE);
        Set<Resource> set = new HashSet<>();
        set.add(r);
        Downloader downloader = new Downloader(set,false,null,null);
        downloader.download();
        File created = new File(r.getFileSavePath());
        Assert.assertTrue((created).exists());
    }

    @Test
    public void DownloadZip() throws IOException, InterruptedException {
        File saveFile = new File("battleline.zip");
        Zipper zip = new Zipper(saveFile);
        Resource r = new Resource("https://dl.dropboxusercontent.com/u/7205900/Battle%20Line/cards_troops_back.jpg",
                "battleLine", Resource.Type.IMAGE);
        Set<Resource> set = new HashSet<>();
        set.add(r);
        Downloader downloader = new Downloader(set,false,zip,null);
        downloader.download();
        Assert.assertTrue((saveFile).exists());
        Assert.assertNotEquals(0,saveFile.length());
    }

}
