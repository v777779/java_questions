package nio1.selectors.threads.echo;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 05-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UDPEcho {
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final int PORT_UDP = 9910;

    private static final String[] PUTTY_WELCOME = {
            "Welcome to echo server!",
            "To close Putty without warning message:",
            "Change Putty>>Window>>Behaviour Settings then Save Default Session",
            "Type any text<Enter> (closewindow to exit):"
    };

    // parsers
    private static int parsePort(String[] args, int port) throws NumberFormatException {
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0].replaceAll("\\p{Punct}", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return port;
    }

    private static boolean parseBlock(String[] args, boolean isBlocking) {
        if (args.length > 1) {
            return !args[1].replaceAll("\\p{Punct}", "")
                    .toLowerCase()
                    .contains("n");
        }
        return isBlocking;
    }

// runners
    public static void runTelnet(String host, int port) throws IOException {
        Runtime.getRuntime().exec("cmd /c start telnet " + host + " " + port);
    }

    private static void runPutty(String host, int port) throws IOException {
        Runtime.getRuntime().exec("./_lib/putty -raw " + host + " " + port);
    }


    public static void main(String[] args) {


        System.out.printf("udp server started...%n");
        try {
            runPutty("localhost", 9990);
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new InputServer());
            exec.shutdown();
// udp server


            while (true) {
                if (exec.isTerminated()) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("udp server closed...%n");
    }


    private static class InputServer implements Runnable {


        @Override
        public void run() {

            ServerSocket ssc = null;
            Socket sc = null;
            BufferedReader br = null;
            PrintWriter pw = null;
            System.out.printf("socket server started...%n");

            DatagramChannel dc = null;
            try {
                ssc = new ServerSocket();
                ssc.bind(new InetSocketAddress(HOST, PORT));

                sc = ssc.accept();
                br = new BufferedReader(new InputStreamReader(sc.getInputStream(), TELNET_CHARSET));
                pw = new PrintWriter(sc.getOutputStream(), true, TELNET_CHARSET); // flush by /r/n
                pw.println(Arrays.stream(PUTTY_WELCOME).collect(Collectors.joining(String.format("%n"))));


                dc = DatagramChannel.open();
                dc.socket().bind(new InetSocketAddress(HOST, PORT_UDP));

                while (true) {
                    String s;
                    if ((s = br.readLine()) == null) break;  // client closed
                    pw.println(s);

                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(ssc, sc, br, pw, dc);
            }
            System.out.printf("server socket closed...%n");
        }
    }
}
