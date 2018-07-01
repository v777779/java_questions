package nio1.selectors.threads.worker;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MultithreadedServer {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 10000;

    public static void main(String[] args) {
        System.out.printf("Open localhost:9990 in Firefox within 20 sec...%n");

        ExecutorService exec = Executors.newFixedThreadPool(1);
        try {
            ServerAcceptService sas = new ServerAcceptService(DEFAULT_PORT);
            exec.execute(sas);
            Thread.sleep(SESSION_LENGTH);
            sas.stop();
            exec.shutdown();
            if(!exec.awaitTermination(500,TimeUnit.MILLISECONDS)) {
                System.out.printf("can't stop server accept service...%n");
            }
             if(exec.isTerminated()) {
                 System.out.printf("server accept service terminated normally...%n");
             }
        } catch (IOException |InterruptedException e) {
            e.printStackTrace();
        }
        if(exec.isTerminated())
        System.out.printf("server closed...%n");
    }
}
