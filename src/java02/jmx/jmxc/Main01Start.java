package java02.jmx.jmxc;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01Start {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmxc\\check");
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmxc\\check2");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
