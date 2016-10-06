import org.junit.Assert;
import org.junit.Test;
import xyz.casualcookie.ttsdownloader.model.Parser;
import xyz.casualcookie.ttsdownloader.model.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Mpinto on 06/10/2016.
 */
public class ParserTester {

    /**
     * Check if parser is working for all fields.
     * Correctness of fields must be checked manually as is changes with file.
     * @throws IOException
     */
    @Test
    public void parse() throws IOException {
        File file=null;
        FileInputStream fis=null;
        String contentString=null;
        byte[] content=null;

        file = new File(getClass().getClassLoader().getResource("361202128.json").getFile());
        content = new byte[(int) file.length()];
        fis = new FileInputStream(file);
        fis.read(content);
        contentString = new String(content);
        fis.close();

        Parser parser = new Parser(contentString,"c:"+File.separatorChar+"fakeGameName");
        int successes = parser.parse();
        Resource r = parser.getResourcesSet().iterator().next();
        System.out.println("extension: "+r.getExtension());
        System.out.println("status: "+r.getFetchStatus());
        System.out.println("file Name: "+r.getFileName());
        System.out.println("save path: "+r.getFileSavePath());
        System.out.println("type: "+r.getType());
        System.out.println("struct path: "+r.getStructurePath());
        System.out.println("URL: "+r.getURL());

        Assert.assertNotNull(r.getExtension());
        Assert.assertNotNull(r.getFetchStatus());
        Assert.assertNotNull(r.getFileName());
        Assert.assertNotNull(r.getFileSavePath());
        Assert.assertNotNull(r.getStructurePath());
        Assert.assertNotNull(r.getType());
        Assert.assertNotNull(r.getURL());
    }
}
