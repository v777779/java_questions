package java02.jmx.jmxh;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 19-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainStart {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmx\\jmxh\\check");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
