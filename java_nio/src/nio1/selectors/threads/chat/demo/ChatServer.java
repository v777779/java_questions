package nio1.selectors.threads.chat.demo;

import util.IOUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 04-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ChatServer {
    private static final int PORT = 9990;
    private static final String HOST = "localhost";
    private static final long SESSION = 50000;
    private static List<ClientService> list = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        LocalDateTime finishTime = LocalDateTime.now().plus(SESSION, ChronoUnit.MILLIS);
        ExecutorService exec = Executors.newCachedThreadPool();

        try {
            AcceptService as = new AcceptService();
            exec.execute(as);

// clients
            Runtime.getRuntime().exec("cmd /c start telnet " + HOST + " " + PORT);
            Runtime.getRuntime().exec("cmd /c start telnet " + HOST + " " + PORT);
            nio1.selectors.threads.chat.chatm.ClientService.run(HOST, PORT);
            nio1.selectors.threads.chat.chatm.ClientService.runPutty(HOST, PORT);

            Thread.sleep(5000);

            while (!LocalDateTime.now().isAfter(finishTime) && !list.isEmpty()) {
                Thread.sleep(1000);
            }
            as.ssc.close();

            exec.shutdown();
            exec.awaitTermination(100, TimeUnit.MILLISECONDS);


        } catch (IOException | InterruptedException e) {
            System.out.printf("%s%n", e);
        }
        if (!exec.isTerminated()) {
            System.out.printf("can't close server%n");
        } else {
            System.out.printf("server closed%n");
        }

    }

    private static class AcceptService implements Runnable {
        private ServerSocket ssc;

        public AcceptService() throws IOException {
            this.ssc = new ServerSocket();
            this.ssc.bind(new InetSocketAddress(HOST, PORT));
        }

        @Override
        public void run() {
            ExecutorService exec = Executors.newCachedThreadPool();

            try {
                while (true) {
                    Socket sc = ssc.accept();  // блокирующий метод выход по ssc.close SocketException
                    if (sc == null) continue;
                    ClientService cs = new ClientService(sc);
                    list.add(cs);
                    exec.execute(cs);
                    System.out.printf("%s:entered to chat...%n", cs.name);
                }
            } catch (IOException e) {
                System.out.printf("%s%n", e);
            }
            try {
                exec.shutdown();
                exec.awaitTermination(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(ssc);
            }

            if (!exec.isTerminated()) {
                System.out.printf("can't close accept service...%n");
            } else {
                System.out.printf("accept service terminated...%n");
            }
        }
    }

    private static class ClientService implements Runnable {
        private static final Charset TELNET_CHARSET = Charset.forName("CP866");
        private final Socket sc;
        private final String name;
        private boolean isClosed;
        private BufferedReader br;
        private BufferedWriter bw;


        public ClientService(Socket sc) throws IOException {
            if (sc == null) throw new IllegalArgumentException();
            this.sc = sc;
            this.br = new BufferedReader(new InputStreamReader(sc.getInputStream(), TELNET_CHARSET));
            this.bw = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream(), TELNET_CHARSET));

            this.name = String.format("SC%d", sc.getPort());
            this.isClosed = false;
        }

        @Override
        public void run() {
            try {
                String s = String.format("Welcome to Socket IO Chat!%n");
                bw.write(s);
                bw.flush();

                while (!isClosed) {
                    s = br.readLine();
                    if (s == null) break;            // termination upon client closing
                    s = String.format("%s:%s%n", name, s);
                    broadcast(s);
                }
                isClosed = true;
            } catch (SocketException e) {
                System.out.printf("Exception:%s%n", e);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(sc, br, bw);
            }
            list.remove(this);
            System.out.printf("%s:left chat...%n", name);

        }

        private void broadcast(String s) throws IOException {
            for (ClientService cs : list) {
                if (cs == null || cs.isClosed) continue;
                cs.bw.write(s);
                cs.bw.flush();
            }
        }

    }
}
