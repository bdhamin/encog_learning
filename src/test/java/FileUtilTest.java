import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.testng.Assert.*;

public class FileUtilTest {

    private static final String TEST_RESOURCES_PATH = "src/test/resources/";
    private static final String FILE_NAME = "test.xml";

    private static final String RESOURCE_FILE_NAME = "IrisData.csv";

    @BeforeMethod
    public void beforeMethod(){
        File file = new File(TEST_RESOURCES_PATH + FILE_NAME);
        if(file.exists()){
            file.delete();
        }
    }
    @Test
    public void testLoadFile() throws URISyntaxException {
//        File file = FileUtil.loadFile(this.getClass().getResource("/TestIrisData.csv").toString());
        File file = FileUtil.loadFile(RESOURCE_FILE_NAME, "");
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    public void testSaveFile() throws URISyntaxException, IOException {
        String filename = "test.xml";
        FileUtil.saveFile(filename, TEST_RESOURCES_PATH);
        assertTrue(Paths.get(TEST_RESOURCES_PATH + filename).toFile().exists());
    }

}