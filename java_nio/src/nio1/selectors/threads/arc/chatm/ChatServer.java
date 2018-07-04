package nio1.selectors.threads.arc.chatm;

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
    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final long SESSION_LENGTH = 500000;



    public static void main(String[] args) {
        System.out.printf("Open %s:%d in Firefox within 20 sec...%n", HOST, PORT);

        ExecutorService exec = Executors.newFixedThreadPool(1);
        LocalDateTime finishTime = LocalDateTime.now().plus(SESSION_LENGTH, ChronoUnit.MILLIS);
        try {
            String cp = "out/production/java_nio";
            String name = "nio1.selectors.threads.arc.chatm.ClientService";
            String denc = "-Dfile.encoding=cp1251";
            String chcp = "chcp 866";
            String cmd = String.format("cmd /c start java -cp %s %s", cp, name);
            String cmdL =String.format("cmd /c start call cmd /c \"%s & java %s -cp %s %s\"",chcp,denc,cp,name);
//            String cmdT =String.format("cmd /c start telnet %s %d", HOST, PORT);

// clients
            ClientService.runTelnet(HOST,PORT);
            ClientService.runTelnet(HOST,PORT);
            ClientService.run(HOST,PORT);
            ClientService.runPutty(HOST,PORT);

//            Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name);
            Runtime.getRuntime().exec(cmdL);
//            Runtime.getRuntime().exec("cmd /c start call cmd /c \"chcp 1251 & java -Dfile.encoding=cp1251 -cp " + cp + " " + name + "\"");
//            Runtime.getRuntime().exec(cmdT);
//            Runtime.getRuntime().exec("cmd /c start telnet localhost 9990");

            ServerAcceptService sas = new ServerAcceptService(HOST, PORT);
            exec.execute(sas);
            Thread.sleep(5000);
            while (!LocalDateTime.now().isAfter(finishTime)) {
                if (sas.isEmpty()) break;
                Thread.sleep(100);
            }

            sas.closeServer();  // close server socket
            exec.shutdown();
            if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                System.out.printf("can't stop server accept service...%n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (exec.isTerminated()) {
            System.out.printf("server closed normally...%n");
        }
    }
}
