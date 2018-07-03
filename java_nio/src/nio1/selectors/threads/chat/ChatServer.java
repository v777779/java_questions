package nio1.selectors.threads.chat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ChatServer {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 500000;
    private static final String DEFAULT_HOST_NAME = "localhost";


    public static void main(String[] args) {
        System.out.printf("Open localhost:9990 in Firefox within 20 sec...%n");

        ExecutorService exec = Executors.newFixedThreadPool(1);
        LocalDateTime finishTime = LocalDateTime.now().plus(SESSION_LENGTH, ChronoUnit.MILLIS);
        try {
            String cp = "out/production/java_nio";
            String name = "nio1.selectors.threads.chat.ClientService";
            Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name);
            Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name);
            Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name);
            Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name);
            Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name);

// telnet commands
// connect::<Enter>         connect:Mike:<Enter>
// chat::message<Enter>     chat:Mike:message<Enter>
// disconnect::<Enter>      disconnect:Mike:<Enter>
//            Runtime.getRuntime().exec("cmd /c start telnet localhost 9990");
//            Runtime.getRuntime().exec("cmd /c start telnet localhost 9990");

            ServerAcceptService sas = new ServerAcceptService(DEFAULT_HOST_NAME, DEFAULT_PORT);
            exec.execute(sas);
            Thread.sleep(5000);
            while (!LocalDateTime.now().isAfter(finishTime)) {
                if (sas.isEmpty()) break;
                Thread.sleep(1000);
            }
            sas.closeServer();
            exec.shutdown();
            if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                exec.shutdownNow();
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (!exec.isTerminated()) {
            System.out.printf("can't stop server...%n");
        } else {
            System.out.printf("server closed normally...%n");
        }
    }
}
