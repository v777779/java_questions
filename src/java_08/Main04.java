package java_08;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 17-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04 {
    public static void main(String[] args) throws IOException {
//IO,NIO
        String format = "%n%s%n------------------------%n";
        System.out.printf(format,"IO Stream, NIO Stream:");

        File file = new File("./data/Ch2P.txt");
        if(!file.exists()) {
            return;
        }
        FileReader fr = new FileReader(file);
        CharBuffer charBuffer = CharBuffer.allocate((int)file.length());


        int len = fr.read(charBuffer);
        System.out.println(fr.getEncoding()+" len:"+len);
        String s = Stream.of(charBuffer.array()).limit(len).map(String::new).collect(Collectors.joining());
        System.out.println(s);

    }
}
