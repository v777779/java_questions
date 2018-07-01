package nio1.selectors.threads.console;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 27-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UserServerSocket {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 50000;
    private static final long SESSION_LENGTH2 = 25000;  // 5500 SocketTimeoutException
    public static final int SOCKETS_NUMBER = 2;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.printf("Use default port:%d", port);
            }
        }

        try {
            String cp = "out/production/java_nio";
            String name = "nio1.selectors.threads.console.UserClientSocket";
            for (int i = 0; i < SOCKETS_NUMBER; i++) {
                Runtime.getRuntime().exec("cmd /c start  java -cp " + cp + " " + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
// Server
        ServerAcceptService sas = null;
        LocalDateTime sessionTime = LocalDateTime.now().plus(SESSION_LENGTH, ChronoUnit.MILLIS);
        ExecutorService exec = Executors.newFixedThreadPool(SOCKETS_NUMBER);
        try {
            sas = new ServerAcceptService("localhost", port, (int) SESSION_LENGTH2);
            exec.execute(sas);

            while (!LocalDateTime.now().isAfter(sessionTime)) {
                Thread.sleep(2000);
                int count  = sas.isClients();
                System.out.printf("alive clients:%d%n",count);
                if(count == 0){
                    System.out.printf("all clients closed...%n");
                    break;
                }
            }

            sas.closeServer();              // initiate close all threads of client service
            exec.shutdown();
            if (!exec.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                System.out.printf("Can't terminate clients...%n");
                System.out.printf("Make shutdownNow()...%n");
                exec.shutdownNow();
                Thread.sleep(1000);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (sas != null) sas.closeServer();
        }
        if (exec.isTerminated()) {
            System.out.printf("server terminated normally...%n");
        } else {
            System.out.printf("server not terminated...%n");
        }
    }

    private static void sendChannel(SocketChannel sc, String s, ByteBuffer b) throws IOException {
        b.clear();
        b.put(s.getBytes(Charset.defaultCharset()));
        b.flip();
        while (b.hasRemaining()) {
            sc.write(b);
        }
    }

    private static String readChannel(SocketChannel sc, ByteBuffer b) throws IOException {
        b.clear();
        if (sc.read(b) == 0) return null;
        b.flip();
        return new String(b.array(), 0, b.limit(), Charset.defaultCharset());
    }
}
