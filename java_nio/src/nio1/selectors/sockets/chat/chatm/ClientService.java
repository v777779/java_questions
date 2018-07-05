package nio1.selectors.sockets.chat.chatm;

import util.IOUtils;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 27-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ClientService {
    private static final String HOST = "localhost";
    private static final int PORT = 23;
    private static final long RESPONSE_LENGTH = 60000;

    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final Charset INPUT_CHARSET = Charset.forName("UTF-8");
    private static final String MARKER_DISCONNECT = "disconnect";

    private static final Random rnd = new Random();

    public static void run(String host, int port) throws IOException {
        String cp = "out/production/java_nio";
        String name = "nio1.selectors.sockets_CHECK_THIS.chat.chatm.ClientService";
        Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name + " " + host + " " + port);
   }
    public static void runTelnet(String host, int port) throws IOException {
        Runtime.getRuntime().exec("cmd /c start telnet " + host + " " + port);
    }

    public static void runPutty(String host, int port) throws IOException {
        Runtime.getRuntime().exec("./_lib/putty -raw " + host + " " + port);

    }

    public static void main(String[] args) {
        String host = HOST;
        int port = PORT;
        if (args != null && args.length > 1) {
            try {
                host = args[0];
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.printf("Use default port:%d", port);
            }
        }
// Client
        Socket sc = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        ClientInput ci = null;

        StringBuilder sb = new StringBuilder();
        ExecutorService exec = Executors.newCachedThreadPool();
        String s;
        try {
            sc = new Socket();
            InetSocketAddress address = new InetSocketAddress(host, port);
            sc.connect(address);
            br = new BufferedReader(new InputStreamReader(sc.getInputStream(), TELNET_CHARSET));
            bw = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream(), TELNET_CHARSET));

            Charset inputCharset = TELNET_CHARSET;
            if (System.getProperty("file.encoding").equals("UTF-8")) {  // running from IDEA
                inputCharset = INPUT_CHARSET;
            }
            ci = new ClientInput(inputCharset);
            exec.execute(ci);

// main loop
            while (true) {
                sb.setLength(0);
// from server
                while (br.ready() && (s = br.readLine()) != null) {
                    sb.append(s).append(String.format("%n"));
                }
                if (sb.toString().length() > 0) {
                    System.out.printf("%s", sb.toString());
                }
// from input
                if (!(s = ci.getMessage()).isEmpty()) {
                    String message = String.format("%s%n", s);
                    if (s.equals(MARKER_DISCONNECT)) {
                        break;
                    }
                    bw.write(message);
                    bw.flush();
                }
                Thread.sleep(100);
            }
        } catch (ConnectException e) {
            System.out.printf("can't connect to server:%s%n", e);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(sc);
        }

// shutdown
        exec.shutdown();            // ordered shutdown after thread exit
        try {
            if (!exec.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                System.out.printf("can't stop client service...%n");
                exec.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (exec.isTerminated()) {
            System.out.printf("client service terminated%n");
        }
    }


    private static class ClientInput implements Runnable {
        private BufferedReader br;
        private StringBuffer sb;

        public ClientInput(Charset charset) {
            this.br = new BufferedReader(new InputStreamReader(System.in, charset));
            this.sb = new StringBuffer();
        }

        public String getMessage() {        // read StringBuffer and clear, thread safe
            String s = sb.toString();
            sb.setLength(0);
            return s;
        }

        @Override
        public void run() {
            String s;
            try {
                while (true) {
                    if ((s = br.readLine()) != null) {  // блокирующий метод
                        sb.append(s);
                        if (s.equals(MARKER_DISCONNECT)) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.printf("client input thread closed...%n");
        }

    }
}
