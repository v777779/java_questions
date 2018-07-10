package nio2.network;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
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
public class UDPChatServerMultiCast {
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final Charset UTF_CHARSET = Charset.forName("UTF-8");

    private static final String HOST = "localhost";
    private static final int SOCKET_PORT = 9990;
    private static final int GROUP_PORT = 9910;
    private static final int UDP_PORT = 9901;

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

    private static void registerDatagramChannel(Selector selector, String host, int port) throws IOException {
        DatagramChannel dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(host, port)); // listening port
        dc.configureBlocking(false);
        dc.register(selector, SelectionKey.OP_READ);

    }

    public static void main(String[] args) {
        args = new String[]{"4"};

        System.out.printf("udp server started...%n");
        Selector selector = null;
        ByteBuffer b = ByteBuffer.allocate(1024);
        SocketAddress socketAddress = null;
        DatagramChannel dc = null;
        NetworkInterface ni = null;

        int gIndex = parsePort(args, -1);
        if (gIndex == -1) {
            System.out.println("Usage: UDPClient 1-10");
            return;
        }

        String groupAddr = GROUP_MASK + gIndex;
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            runPutty(HOST, SOCKET_PORT);
            InputServer inputServer = new InputServer(UTF_CHARSET, SOCKET_PORT, UDP_PORT);
            exec.execute(inputServer);  // for putty
            exec.shutdown();
// selector
            selector = Selector.open();
// udp server
            ni = NetworkInterface.getByInetAddress(InetAddress.getByName(HOST));
            dc = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(GROUP_PORT))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            dc.configureBlocking(false);
            for (int i = 0; i < 10; i++) {
                String s = GROUP_MASK + i;
                if (s.equals(groupAddr)) continue;
                dc.join(InetAddress.getByName(s), ni);  // группа ввода
            }
            dc.register(selector, SelectionKey.OP_READ, "group");

            dc = DatagramChannel.open(StandardProtocolFamily.INET);
            dc.bind(new InetSocketAddress(UDP_PORT));
            dc.configureBlocking(false);
            dc.register(selector, SelectionKey.OP_READ, "udp");

            InetAddress group = InetAddress.getByName(groupAddr); // группа вывода
            Map<SocketAddress, DatagramChannel> map = new HashMap<>();

            while (true) {
                if (exec.isTerminated()) {
                    break;
                }
                int n = selector.select(100);
                if (n == 0) continue;
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                SocketAddress scKey = null;
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isReadable()) {
                        DatagramChannel dcKey = (DatagramChannel) key.channel();
                        b.clear();
                        socketAddress = dcKey.receive(b); // accumulate in buffer
                        if (socketAddress == null) continue;

                        b.flip();
//                        byte[] header = "UDP:".getBytes(UTF_CHARSET);
//                        byte[] bytes = new byte[b.remaining()];
//                        b.get(bytes).compact().put(header).put(bytes).flip();
//                        dcKey.send(b,socketAddress); // back
//                        b.rewind(); // groupLocal
//                        dc.send(b,new InetSocketAddress(group,UDP_PORT));
                            System.out.print(new String(b.array(), 0, b.limit(), UTF_CHARSET));
//                        if (new String(b.array(), 0, 4, UTF_CHARSET).equals("UDP:")) {
//                            socketAddress = inputServer.getUdpSocketAddress();
//                            dcKey.send(b, socketAddress); // UDP: to socket
//                        } else {
//                            dcKey.send(b, new InetSocketAddress(group, UDP_PORT)); // KEY: to group
//                            b.rewind();
//                            dcKey.send(b, socketAddress);
//                        }

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
        private Charset consoleCharset;
        private int port;
        private int udpPort;
        private SocketAddress socketAddress;

        public InputServer(Charset consoleCharset, int port, int udpPort) throws IOException {
            this.consoleCharset = consoleCharset;
            this.port = port;
            this.udpPort = udpPort;
            this.socketAddress = null;
        }

        synchronized public void setUdpSocketAddress(DatagramChannel dc) throws IOException {
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
            ByteBuffer bH = ByteBuffer.wrap("KEY:".getBytes(UTF_CHARSET));
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
                setUdpSocketAddress(dc);
//// welcome
//                bF.put(String.format("KEY:welcome%n").getBytes(UTF_CHARSET));
//                bF.flip();
//                dc.send(bF, dc.getRemoteAddress());


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
                                bF.clear();
                                bF.put(ByteBuffer.wrap("KEY:".getBytes(UTF_CHARSET)));
                                bSC.flip();
                                bF.put(bSC);
                                bF.flip();
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
