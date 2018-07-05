package nio1.selectors.threads.echo;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
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
    private static final Charset UDP_CHARSET = Charset.forName("UTF-8");

    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final int UDP_PORT = 9910;
    private static final int UDP_PORT2 = 9910;

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
        DatagramChannel dc = null;
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            runPutty(HOST, PORT);
            exec.execute(new InputServer(UDP_CHARSET, PORT, UDP_PORT));  // for putty

//            runTelnet(HOST, PORT);
//            exec.execute(new InputServer(TELNET_CHARSET, PORT, UDP_PORT);  // for telnet

            exec.shutdown();

// udp server
            InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, UDP_PORT);
            dc = DatagramChannel.open();
            dc.socket().bind(inetSocketAddress); // listening port
            dc.configureBlocking(false);

            ByteBuffer b = ByteBuffer.allocate(1024);

            while (true) {
                if (exec.isTerminated()) {
                    break;
                }
                b.clear();
                SocketAddress socketAddress = dc.receive(b);
                if (socketAddress != null) {
                    b.flip();
                    String s = "UDP:" + new String(b.array(), 0, b.limit(), UDP_CHARSET);

                    b.clear();
                    b.put(s.getBytes(UDP_CHARSET));
                    b.flip();
                    dc.send(b, socketAddress); // не постоянное соединение, поэтому всегда отвечаем адресату

                }
                Thread.sleep(100);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(dc);
        }

        System.out.printf("udp server closed...%n");
    }


//    private static class UDPServer implements Runnable {
//        private final boolean isTelnet;
//        private final int port;
//        private final int udpPort;
//
//
//        public UDPServer(boolean isTelnet, int serverPort) {
//            this.isTelnet = isTelnet;
//        }
//
//        @Override
//        public void run() {
//            System.out.printf("udp server started...%n");
//            DatagramChannel dc = null;
//
//            try {
//                ExecutorService exec = Executors.newCachedThreadPool();
//                if (isTelnet) {
//                    runTelnet(HOST, PORT);
//                    exec.execute(new InputServer(TELNET_CHARSET, PORT, UDP_PORT));  // for telnet
//                } else {
//                    runPutty(HOST, PORT);
//                    exec.execute(new InputServer(UDP_CHARSET, PORT, UDP_PORT));  // for putty
//                }
//
//                exec.shutdown();
//// udp server
//                InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, UDP_PORT);
//                dc = DatagramChannel.open();
//                dc.socket().bind(inetSocketAddress); // listening port
//                dc.configureBlocking(false);
//
//                ByteBuffer b = ByteBuffer.allocate(1024);
//
//                while (true) {
//                    if (exec.isTerminated()) {
//                        break;
//                    }
//                    b.clear();
//                    SocketAddress socketAddress = dc.receive(b);
//                    if (socketAddress != null) {
//                        b.flip();
//                        String s = "UDP:" + new String(b.array(), 0, b.limit(), UDP_CHARSET);
//
//                        b.clear();
//                        b.put(s.getBytes(UDP_CHARSET));
//                        b.flip();
//                        dc.send(b, socketAddress); // не постоянное соединение, поэтому всегда отвечаем адресату
//
//                    }
//                    Thread.sleep(100);
//                }
//
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                IOUtils.close(dc);
//            }
//
//            System.out.printf("udp server closed...%n");
//
//        }
//    }

    private static class InputServer implements Runnable {
        private BufferedReader br;
        private PrintWriter pw;
        private Charset consoleCharset;
        private int port;
        private int udpPort;

        public InputServer(Charset consoleCharset, int port, int udpPort) {
            this.consoleCharset = consoleCharset;
            this.port = port;
            this.udpPort = udpPort;
        }

        @Override
        public void run() {
// socket
            ServerSocket ssc = null;
            Socket sc = null;
            System.out.printf("socket server started...%n");
// udp
            DatagramChannel dc = null;
            ByteBuffer b = ByteBuffer.allocate(1024);
            InetSocketAddress udpAddress = new InetSocketAddress(HOST, udpPort);

            try {
                ssc = new ServerSocket();
                ssc.bind(new InetSocketAddress(HOST, port));

                sc = ssc.accept();
                br = new BufferedReader(new InputStreamReader(sc.getInputStream(), consoleCharset));
                pw = new PrintWriter(sc.getOutputStream(), true, consoleCharset); // flush by /r/n
                pw.println(Arrays.stream(PUTTY_WELCOME).collect(Collectors.joining(String.format("%n"))));

//udp
                dc = DatagramChannel.open();
                dc.connect(udpAddress);

                while (true) {
                    String s;
                    if ((s = br.readLine()) == null) break;  // client closed
// udp
                    b.clear();
                    b.put(s.getBytes(UDP_CHARSET));
                    b.flip();
                    dc.write(b);                        //  dc.send(b,udpAddress);


                    b.clear();
                    dc.receive(b);
                    b.flip();
                    s = new String(b.array(), 0, b.limit(), UDP_CHARSET);
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
