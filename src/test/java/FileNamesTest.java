import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class FileNamesTest {

    @Test
    public void testFileNames(){
        String filename = FileNames.IRIS_SHUFFELD_INPUT_DATA.getName();
        assertEquals(filename, "Iris_Shuffeld.csv");
        assertNotEquals(filename, "somefile");
    }



}