package io;

import java.io.IOException;

import static nio.Main01.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 24-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02CC {
    public static void main(String[] args) {

// Console
        System.out.printf(FORMAT, "Console:");
// class
        try {
            System.out.printf(FORMAT,"Run classes:");
            String path = "out/production/java_nio/io;out/production/java_nio;";
            String fileClass = "io.console.MainConsoleC";
            Runtime.getRuntime().exec("cmd /c start java -cp "+path+" "+fileClass);

        } catch (IOException e) {
            e.printStackTrace();
        }



//// DecoratorInputStream
//        System.out.printf(FORMAT, "DecoratorInputStream:");
//        in = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
//            IOUtils.readout(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in);
//        }
//        System.exit(0);



    }
}
