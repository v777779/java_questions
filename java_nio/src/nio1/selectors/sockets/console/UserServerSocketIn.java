package nio1.selectors.sockets.console;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Set;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 27-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UserServerSocketIn {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 1150000;

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
            String name = "nio1.selectors.sockets.console.UserClientSocketIn";
            Runtime.getRuntime().exec("cmd /c start  java -cp " + cp + " " + name);
            Runtime.getRuntime().exec("cmd /c start  java -cp " + cp + " " + name + " " + String.valueOf(port + 1));
            Runtime.getRuntime().exec("cmd /c start  java -cp " + cp + " " + name + " " + String.valueOf(port + 2));
        } catch (IOException e) {
            e.printStackTrace();
        }
// Server
        ServerSocketChannel ssc = null;
        ServerSocketChannel ssc2 = null;
        ServerSocket ss = null;
        ServerSocket ss2 = null;
        ByteBuffer b = ByteBuffer.allocate(2000);                     // message
        LocalDateTime sessionTime = LocalDateTime.now().plus(SESSION_LENGTH, ChronoUnit.MILLIS);
        String message = "";
        try {
            Selector selector = Selector.open();
// первый serverSocketChannel
            ssc = ServerSocketChannel.open();                       // channel
            ss = ssc.socket();                                      // socket
            ss.bind(new InetSocketAddress(port));
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT, "SSC");     // пока единственная операция

// второй serverSocketChannel
            ssc2 = ServerSocketChannel.open();                       // channel
            ss2 = ssc2.socket();                                      // socket
            ss2.bind(new InetSocketAddress(port + 1));
            ssc2.configureBlocking(false);
            ssc2.register(selector, SelectionKey.OP_ACCEPT, "SSC2");     // пока единственная операция


            System.out.printf("Server  started  local:%s%n", ssc.getLocalAddress());
            while (!LocalDateTime.now().isAfter(sessionTime)) {
                int n = selector.select(2500);
                if (n == 0) {
                    Set<SelectionKey> set = selector.keys();
                    if (set.size() > 0) {
                        SelectableChannel sc = set.iterator().next().channel();
                        if (sc instanceof ServerSocketChannel) {
                            System.out.print(".");
                        }
                    }
                    continue;
                }

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {                       // принять соединение
                        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
                        if (sc == null) continue;

                        System.out.printf("%naccepted local:%s remote:%s%n", sc.getLocalAddress(),
                                sc.getRemoteAddress());
                        key.cancel();
                        sc.configureBlocking(false);
                        if ((key.attachment()).equals("SSC")) {
                            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, "SC1");
                        } else {
                            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, "SC2");
                        }
//                        b.clear();
//                        String s = ("accepted");
//                        b.put(s.getBytes(Charset.forName("UTF-8")));
//                        b.flip();
//                        while (b.hasRemaining()) {
//                            sc.write(b);
//                        }
//                        sc.close();
                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        b.clear();
                        if (sc.read(b) > 0) {  // read all data into buffer
                            b.flip();
                            String s = new String(b.array(), 0, b.limit(), Charset.defaultCharset());
                            System.out.printf("%s", s);

                            if (s.replaceAll("\\s*", "").equals("cc")) {
                                key.cancel();
                                sc.close();
                                ssc.register(selector, SelectionKey.OP_ACCEPT);
                                System.out.printf("client closed%n");
                            }

                            message = s.replaceAll("\\s*", "") + key.attachment();
                        }
                    } else if (key.isWritable()) {
                        if (message.startsWith("aa") && message.endsWith((String) key.attachment())) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            b.clear();
                            String s = String.format("answer %s: at:%2$tT %2$tD%n", key.attachment(),LocalDateTime.now());
                            b.put(s.getBytes(Charset.defaultCharset()));
                            b.flip();
                            while (b.hasRemaining()) {
                                sc.write(b);
                            }
                            message = "";
                        }

                    }
                    it.remove();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(ssc, ss);
        }


    }
}
