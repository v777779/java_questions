package nio1.selectors.threads.encoding;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class EncodingApp {
    public static void main(String[] args) throws IOException {
        String cp = "out/production/java_nio";
        String name = "nio1.selectors.threads.encoding.Encoding";
        Runtime.getRuntime().exec("cmd /c start call java -Dfile.encoding=cp1251 -cp " + cp + " " + name);
//        Runtime.getRuntime().exec("cmd /c start call cmd /c \"chcp 1251 & java -Dfile.encoding=cp1251 -cp " + cp + " " + name+"\"");


    }

}
