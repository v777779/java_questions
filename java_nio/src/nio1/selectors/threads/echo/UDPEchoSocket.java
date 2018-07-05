package nio1.selectors.threads.echo;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
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
public class UDPEchoSocket {
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final Charset UDP_CHARSET = Charset.forName("UTF-8");

    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final int UDP_PORT = 9920;

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
        DatagramSocket dc = null;
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            runPutty(HOST, PORT);
            InputServer inputServer = new InputServer(UDP_CHARSET, PORT, UDP_PORT);  // for putty
            exec.execute(inputServer);  // for putty

//            runTelnet(HOST, PORT);
//            exec.execute(new InputServer(TELNET_CHARSET, PORT, UDP_PORT);  // for telnet

            exec.shutdown();

// udp server
            SocketAddress socketAddress = new InetSocketAddress(HOST, UDP_PORT);
            dc = new DatagramSocket(socketAddress);  // bind
            DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
            inputServer.setDatagramSocket(dc);

            byte[] bytes;
            while (true) {
                if (exec.isTerminated()) {
                    break;
                }

                dc.receive(p);                          // блокирующий метод
                if (p.getLength() > 0) {
                    String s = "UDP:" + new String(p.getData(), 0, p.getLength(), UDP_CHARSET);
                    bytes = s.getBytes(UDP_CHARSET);

                    p.setData(bytes);                   // В ДАННОМ СЛУЧАЕ REUSE ОБЯЗАТЕЛЕН
                    dc.send(p);
                }
                Thread.sleep(100);
            }

        } catch (IOException | InterruptedException e) {
            System.out.printf("Exception:%s%n",e);
        } finally {
            IOUtils.close(dc);
        }

        System.out.printf("udp server closed...%n");
    }

    private static class InputServer implements Runnable {
        private BufferedReader br;
        private PrintWriter pw;
        private Charset consoleCharset;
        private int port;
        private int udpPort;

        private DatagramSocket ssds;

        public InputServer(Charset consoleCharset, int port, int udpPort) {
            this.consoleCharset = consoleCharset;
            this.port = port;
            this.udpPort = udpPort;
        }

        synchronized public void setDatagramSocket(DatagramSocket ssds) {
            this.ssds = ssds;
        }

        synchronized public void closeDatagramSocket() {  // close server datagram socket
            if (ssds != null) {
                ssds.close();
            }
        }


        @Override
        public void run() {
// socket
            ServerSocket ssc = null;
            Socket sc = null;
            System.out.printf("socket server started...%n");
// udp
            DatagramSocket dc = null;
            InetSocketAddress udpAddress = new InetSocketAddress(HOST, udpPort);

            DatagramPacket p = new DatagramPacket(new byte[1024], 1024, udpAddress);
            DatagramPacket p2 = new DatagramPacket(new byte[1024], 1024, udpAddress);
            try {
                ssc = new ServerSocket();
                ssc.bind(new InetSocketAddress(HOST, port));

                sc = ssc.accept();
                br = new BufferedReader(new InputStreamReader(sc.getInputStream(), consoleCharset));
                pw = new PrintWriter(sc.getOutputStream(), true, consoleCharset); // flush by /r/n
                pw.println(Arrays.stream(PUTTY_WELCOME).collect(Collectors.joining(String.format("%n"))));

//udp
                dc = new DatagramSocket();
                dc.connect(udpAddress);
                while (true) {
                    String s;
                    if ((s = br.readLine()) == null) break;  //блокирующий метод client closed
// udp
                    p.setData(s.getBytes(UDP_CHARSET));
                    dc.send(p);
                    dc.receive(p2);                         // ЗДЕСЬ НАОБОРОТ REUSE ЗАПРЕЩЕН
                    s = new String(p2.getData(), 0, p2.getLength(), UDP_CHARSET);
                    pw.println(s);
                }

                closeDatagramSocket();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(ssc, sc, br, pw, dc);
            }
            System.out.printf("server socket closed...%n");
        }
    }
}
