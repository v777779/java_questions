package nio2.async;

import java.io.IOException;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class AsyncSocketRunner {
    public static void main(String[] args) {
        System.out.printf(FORMAT, "AsynchronousServerSocketChannel:");
        try {
            Runtime.getRuntime()
                    .exec("cmd /c start java -cp out/production/java_nio nio2.async.sockets.MainServerSocket");
            Thread.sleep(500);

            Runtime.getRuntime()
                    .exec("cmd /c start java -cp out/production/java_nio nio2.async.sockets.MainClientSocket");
            Runtime.getRuntime()
                    .exec("cmd /c start java -cp out/production/java_nio nio2.async.sockets.MainClientSocket");

//            MainServerSocket.main(args);
//            MainClientSocket.main(args);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
