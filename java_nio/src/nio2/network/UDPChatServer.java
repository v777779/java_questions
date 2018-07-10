package nio2.network;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 05-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UDPChatServer {
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final Charset UDP_CHARSET = Charset.forName("UTF-8");

    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final int UDP_PORT = 9910;
    private static final int UDP_PORT2 = 9910;

    private static final String[] PUTTY_WELCOME = {
            "Welcome to udp server!",
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

    public static void runPutty(String host, int port) throws IOException {
        Runtime.getRuntime().exec("./_lib/putty -raw " + host + " " + port);
    }

    private static void registerDatagramChannel(Selector selector, String host, int port) throws IOException {
        DatagramChannel dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(host, port)); // listening port
        dc.configureBlocking(false);
        dc.register(selector, SelectionKey.OP_READ);

    }

    public static void main(String[] args) {
        System.out.printf("udp server started...%n");
        Selector selector = null;
        ByteBuffer b = ByteBuffer.allocate(1024);
        SocketAddress socketAddress = null;

        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new InputServer(UDP_CHARSET, PORT, UDP_PORT));  // for putty
            exec.execute(new InputServer(UDP_CHARSET, PORT + 1, UDP_PORT + 1));  // for telnet
            exec.execute(new InputServer(TELNET_CHARSET, PORT + 2, UDP_PORT + 2));  // for telnet
// running alone
            if(args.length == 0) {
                runPutty(HOST, PORT);
                runPutty(HOST, PORT+1);
                runTelnet(HOST, PORT + 2);
            }

            exec.shutdown();
// selector
            selector = Selector.open();
// udp server
            registerDatagramChannel(selector, HOST, UDP_PORT);
            registerDatagramChannel(selector, HOST, UDP_PORT + 1);
            registerDatagramChannel(selector, HOST, UDP_PORT + 2);
            Map<SocketAddress,DatagramChannel> map = new HashMap<>();

            while (true) {
                if (exec.isTerminated()) {
                    break;
                }
                int n = selector.select(100);
                if (n == 0) continue;
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isReadable()) {
                        DatagramChannel dc = (DatagramChannel) key.channel();
                        b.clear();
                        socketAddress = dc.receive(b); // accumulate in buffer
                        if (socketAddress == null) continue;
                        map.put(socketAddress,dc);

                        b.flip();
                        byte[] header = "UDP:".getBytes(UDP_CHARSET);
                        byte[] bytes = new byte[b.remaining()];
                        b.get(bytes).compact().put(header).put(bytes).flip();
                        for (SocketAddress address : map.keySet()) {
                            dc = map.get(address);
                            dc.send(b,address);
                            b.rewind();
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(selector);
        }

        System.out.printf("udp server closed...%n");
    }

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
            ServerSocketChannel ssc = null;
            Selector selector = null;
            System.out.printf("socket server at s:%d u:%d started...%n", port, udpPort);
// udp
            ByteBuffer bSC = ByteBuffer.allocate(1024);
            ByteBuffer bDC = ByteBuffer.allocate(1024);
            InetSocketAddress udpAddress = new InetSocketAddress(HOST, udpPort);

            try {
                selector = Selector.open();
                ssc = ServerSocketChannel.open();
                ssc.bind(new InetSocketAddress(HOST, port));
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, "socket");

                for (String s : PUTTY_WELCOME) {
                    bDC.put(String.format("%s%n", s).getBytes(UDP_CHARSET));
                }
                bSC.put(String.format("SC%d added to chat%n",sc.socket().getPort()).getBytes(UDP_CHARSET));  // регистрация socketAddress в map
//udp
                DatagramChannel dc = DatagramChannel.open();
                dc.connect(udpAddress);
                dc.configureBlocking(false);
                dc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, "datagram");

                while (true) {
                    int n = selector.select(100);
                    if (n == 0) break;

                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        int len;
                        if (key.isReadable()) {
                            if (key.attachment().equals("socket")) {
                                sc = (SocketChannel) key.channel();
                                if ((len = sc.read(bSC)) == -1) {
                                    sc.close();
                                    dc.close();
                                    it.forEachRemaining(SelectionKey::cancel);
                                }
                            } else {
                                dc = (DatagramChannel) key.channel();
                                if ((len = dc.read(bDC)) == -1) {
                                    dc.close();
                                }
                            }
                        } else if (key.isWritable()) {
                            if (key.attachment().equals("socket")) {
                                sc = (SocketChannel) key.channel();
                                bDC.flip();
                                while (bDC.hasRemaining()) {
                                    sc.write(bDC);
                                }
                                bDC.clear();
                            } else {
                                dc = (DatagramChannel) key.channel();
                                byte[] b = bSC.array();
                                boolean isFound = false;
                                for (int i = 0; i < bSC.position(); i++) {
                                    if (b[i] == '\r' || b[i] == '\n') {
                                        isFound = true;
                                        break;
                                    }
                                }
                                if (!isFound) continue;
                                bSC.flip();
                                while (bSC.hasRemaining()) {
                                    dc.write(bSC);
                                }
                                bSC.clear();
                            }
                        }
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(ssc, selector);
            }
            System.out.printf("socket server at s:%d u:%d closed...%n", port, udpPort);
        }
    }
}
