import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Bashar on 12/21/2015.
 */
public final class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
    private static final String PATH = "src"+File.separator+"main"+File.separator+"resources"+File.separator;

    private FileUtil(){}

    public static File loadFile(String fileName, String path){

        try {
            if(!"".equals(fileName.trim())) {
                return new File(FileUtil.class.getResource(fileName).toURI());
            }
        } catch (URISyntaxException e) {
           LOGGER.error("File could not be loaded {}, {}", fileName, e);
        }
        return null;
    }

    public static File saveFile(String filename, String path){

        if("".equals(path.trim())){
            path = PATH;
        }

        File resourceFile = null;
        try {
            resourceFile = new File(path.concat(filename));
            if(resourceFile.createNewFile()){
                return resourceFile;
            }
        } catch (IOException e) {
            LOGGER.error("Could not create a file with name {}", filename, e);
        }

        return resourceFile;
    }

}
