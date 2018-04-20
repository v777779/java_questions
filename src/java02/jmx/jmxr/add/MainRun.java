package java02.jmx.jmxr;

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
        System.out.println("   Terminal 2  run check.cmd");
        System.out.println("   Terminal 3  run check2.cmd");
        System.out.println("3. When JConsole runned copy and paste string");
        System.out.println("   \"service:jmx:rmi:///jndi/rmi://localhost:9999/server\"  ");
        System.out.println("   to JConcole tab \"remote\"" );
        try {
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmx\\jmxr\\check.cmd");
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmx\\jmxr\\check1.cmd");
            Runtime.getRuntime().exec("cmd /c start src\\java02\\jmx\\jmxr\\check2.cmd");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
