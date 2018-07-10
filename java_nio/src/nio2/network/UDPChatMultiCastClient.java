package nio2.network;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 05-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UDPChatMultiCastClient {
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final Charset UTF_CHARSET = Charset.forName("UTF-8");

    private static final String HOST = "localhost";
    private static final int PORT = 1;
    private static final int SOCKET_MASK = 9990;
    private static final int GROUP_PORT = 9910;
    private static final int UDP_MASK = 9900;

    private static final String GROUP_MASK = "239.255.0.";
    private static final int GROUP_INDEX = -1;


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

    private static boolean parsePutty(String[] args, boolean isPutty) {
        if (args.length > 1) {
            return args[1].replaceAll("\\p{Punct}", "")
                    .toLowerCase()
                    .contains("putty");
        }
        return isPutty;
    }

    // runners
    public static void runTelnet(String host, int port) throws IOException {
        Runtime.getRuntime().exec("cmd /c start telnet " + host + " " + port);
    }

    private static void runPutty(String host, int port) throws IOException {
        Runtime.getRuntime().exec("./_lib/putty -raw " + host + " " + port);
    }

    public static void run(int port, boolean isPutty) throws IOException {
        String cp = "out/production/java_nio";
        String name = "nio2.network.UDPChatMultiCastClient";
        String putty = isPutty ? "putty" : "telnet";
        String cmd = String.format("cmd /c start java -cp %s %s %d %s", cp, name, port, putty);
        Runtime.getRuntime().exec(cmd);
    }

    private static void registerDatagramChannel(Selector selector, String host, int port) throws IOException {
        DatagramChannel dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(host, port)); // listening port
        dc.configureBlocking(false);
        dc.register(selector, SelectionKey.OP_READ);

    }


    public static void main(String[] args) {
//        args = new String[]{"" + PORT};

        System.out.printf("udp server started...%n");
        Selector selector = null;
        ByteBuffer b = ByteBuffer.allocate(1024);
        SocketAddress socketAddress = null;
        DatagramChannel dcGroup = null;
        DatagramChannel dcKey = null;
        NetworkInterface ni = null;


        int port = parsePort(args, -1);
        boolean isPutty = parsePutty(args, false);

        if (port < 0) {
            System.out.println("Usage: UDPClient 1-10");
            port = 9;
        }
        final int socketPort = SOCKET_MASK + port;
        final int udpPort = UDP_MASK + port;
        final String groupAddr = GROUP_MASK + port;
        final Charset consoleCharset = isPutty ? UTF_CHARSET : TELNET_CHARSET;
        final Charset groupCharset = UTF_CHARSET;

        try {
            ExecutorService exec = Executors.newCachedThreadPool();

            if (isPutty) runPutty(HOST, socketPort);
            else runTelnet(HOST, socketPort);

            InputServer inputServer = new InputServer(socketPort, udpPort, groupAddr);
            exec.execute(inputServer);  // for putty
            exec.shutdown();
// selector
            selector = Selector.open();
// udp server
            ni = NetworkInterface.getByInetAddress(InetAddress.getByName(HOST));
            dcGroup = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(GROUP_PORT))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            for (int i = 0; i < 10; i++) {
                String s = GROUP_MASK + i;
                if (s.equals(groupAddr)) continue;
                dcGroup.join(InetAddress.getByName(s), ni);  // группа ввода
            }
            dcGroup.configureBlocking(false)
                    .register(selector, SelectionKey.OP_READ, "group");

            dcKey = DatagramChannel.open(StandardProtocolFamily.INET)
                    .bind(new InetSocketAddress(udpPort));
            dcKey.configureBlocking(false)
                    .register(selector, SelectionKey.OP_READ, "udp");

            InetAddress group = InetAddress.getByName(groupAddr); // группа вывода


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

                        b.flip();
                        if (dc.equals(dcGroup)) {
                            String s = new String(b.array(), 0, b.limit(), groupCharset);
                            System.out.print(s);
                            b.clear().put(s.getBytes(consoleCharset)).flip();
                            dcKey.send(b, inputServer.getUdpSocketAddress());         // UDP: to socket
                        } else {
                            String s = new String(b.array(), 0, b.limit(),consoleCharset);
                            b.clear().put(s.getBytes(groupCharset)).flip();
                            dcGroup.send(b, new InetSocketAddress(group, GROUP_PORT)); // KEY: to group
                        }

                        b.clear();

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

        private int port;
        private int udpPort;
        private SocketAddress socketAddress;
        private byte[] header;

        public InputServer(int port, int udpPort, String groupAddr) throws IOException {

            this.port = port;
            this.udpPort = udpPort;
            this.socketAddress = null;
            this.header = String.format("%s:", groupAddr).getBytes(UTF_CHARSET);
        }

        synchronized public void setUDPSocketAddress(DatagramChannel dc) throws IOException {
            socketAddress = dc.getLocalAddress();
        }

        synchronized public SocketAddress getUdpSocketAddress() {
            return socketAddress;
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
            ByteBuffer bF = ByteBuffer.allocate(1024);


            try {
                selector = Selector.open();
                ssc = ServerSocketChannel.open();
                ssc.bind(new InetSocketAddress(HOST, port));
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, "socket");

                for (String s : PUTTY_WELCOME) {
                    bDC.put(String.format("%s%n", s).getBytes(UTF_CHARSET));
                }
//udp
                DatagramChannel dc = DatagramChannel.open();
                dc.connect(udpAddress);
                dc.configureBlocking(false);
                dc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, "datagram");
                setUDPSocketAddress(dc);

                while (true) {
                    int n = selector.select(100);
                    if (n == 0) break;

                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isReadable()) {
                            if (key.attachment().equals("socket")) {
                                sc = (SocketChannel) key.channel();
                                if (sc.read(bSC) == -1) {
                                    sc.close();
                                    dc.close();
                                    it.forEachRemaining(SelectionKey::cancel);
                                }
                            } else {
                                dc = (DatagramChannel) key.channel();
                                if (dc.read(bDC) == -1) {
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
                                bF.clear().put(header).put(bSC).flip();
                                while (bF.hasRemaining()) {
                                    SocketAddress socketAddress = dc.getRemoteAddress();
                                    dc.send(bF, socketAddress);
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
