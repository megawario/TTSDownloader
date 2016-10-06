package xyz.casualcookie.ttsdownloader.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Opens game file and parses.
 *
 * Path where things are saved are different from the install path.
 * Install path is the path that appears in the description.
 *
 * Install path is concatenate to take advantage of cache system from Tabletop Simulator.
 * URL will be eliminated alltogether
 *
 * Created by Mpinto on 09/09/2016.
 */
public class Parser {

    static final Pattern objectPattern,imagePattern;
    static {
        objectPattern = Pattern.compile("(file|http|https|ftp).*.obj");
        imagePattern = Pattern.compile("(file|http|https|ftp).*(jpg|jpeg|png|gif|bmp)");
    }

    private Matcher objectMatcher,imageMatcher;
    private HashSet<Resource> resourcesSet;

    private String outputDir;

    /**
     * Initializes parser, feeding content to the matchers and setting path variables
     * @param fileContent content of the file to parse
     * @throws MalformedURLException
     */
    public Parser(String fileContent,String outputDir) throws MalformedURLException {
        this.outputDir = outputDir;
        resourcesSet = new HashSet<>();
        objectMatcher = objectPattern.matcher(fileContent);
        imageMatcher = imagePattern.matcher(fileContent);
    };

    /**
     * Parses the file, searching for a match in images and models.
     * Places matches in a Map structure.Implementation uses HashMap.
     *
     * The map structure uses as key the original URL and as as value the new path for the file
     * @return Number of different hits.
     * @throws MalformedURLException paths on file are malformed URLs or gave error
     */
    public int parse() throws MalformedURLException {
        constructResources(objectMatcher,Resource.Type.OBJECT);
        constructResources(imageMatcher,Resource.Type.IMAGE);
        return resourcesSet.size();
    }

    private void constructResources(Matcher matcher,Resource.Type type) throws MalformedURLException {
        while (matcher.find()){
            String resourceURL = matcher.group(); //verifies if is valid url
            resourcesSet.add(new Resource(resourceURL,outputDir,type));
        }
    }

    public Set<Resource> getResourcesSet(){
        return resourcesSet;
    }
}
