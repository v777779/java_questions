package java02.jmx.jmxh.add;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainRun {
    public static void main(String[] args) {
        System.out.println("Run:");
        System.out.println("1. Create three tabs with Terminals");
        System.out.println("2. Terminal 1  run check.cmd");

        try {
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmxh\\check.cmd");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
