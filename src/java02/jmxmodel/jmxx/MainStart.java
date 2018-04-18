package java02.jmxmodel.jmxx;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainStart {
    public static void main(String[] args) {
        System.out.println("Run Main Start");
        System.out.println("Run SimpleClient");
        try {
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmxmodel\\jmxx\\check");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
