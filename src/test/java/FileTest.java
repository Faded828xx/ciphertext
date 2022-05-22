import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author faded828x
 * @date 2022/5/15
 */
public class FileTest {
    @Test
    public  void fileIO() throws IOException {
        String name = "/Users/faded828x/Desktop/ciphertext/server/upload/file.txt";
        File file = new File(name);
        file.createNewFile();
        OutputStream out = new FileOutputStream(file);
        out.write("hello".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void split() {
        String name = "/Users/faded828x/Desktop/ciphertext/src/test/java/FileTest.java";
        String[] s = name.split("/");
        System.out.println(s[s.length - 1]);
    }
}
