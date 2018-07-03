package nio1.selectors.threads.encoding;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class EncodingOutputApp {
    public static void main(String[] args) throws IOException,InterruptedException {
        String cp = "out/production/java_nio";
        String name = "nio1.selectors.threads.encoding.EncodingOutput";
        String s = "cmd /k \"chcp 65001 & java -Dfile.encoding=UTF-8 -cp " + cp + " " + name+"\"";
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        byte[] copy = new byte[bytes.length];
        RandomAccessFile raf = new RandomAccessFile("c.cmd","rw");
        raf.read(copy);
        if(Arrays.compare(bytes,copy)!= 0) {
         raf.write(bytes);
        }
        raf.close();
        Thread.sleep(500);
        Runtime.getRuntime().exec("cmd /c start c.cmd.lnk");

//        Runtime.getRuntime().exec("cmd /c  start call java -cp " + cp + " " + name+"\"");
//        Runtime.getRuntime().exec("cmd /c \"chcp 65001 & java -Dfile.encoding=UTF-8 -cp " + cp + " " + name+"\"");

    }

}
