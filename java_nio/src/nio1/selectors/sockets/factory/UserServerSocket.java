package nio1.selectors.sockets.factory;

import util.IOUtils;

import java.io.IOException;
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
public class UserServerSocket {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 1150000;
    private static final int SOCKETS_NUMBER = 5;

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
            String name = "nio1.selectors.sockets.factory.UserClientSocket";
            for (int i = 0; i < SOCKETS_NUMBER; i++) {
                Runtime.getRuntime().exec("cmd /c start java -cp " +
                        cp + " " + name + " " + String.valueOf(port + i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
// Server
        ServerSocketFactory[] sscs = null;
        ByteBuffer b = ByteBuffer.allocate(2000);                     // message
        LocalDateTime sessionTime = LocalDateTime.now().plus(SESSION_LENGTH, ChronoUnit.MILLIS);
        String message = "";
        try {
            Selector selector = Selector.open();

// serverSocketChannels
            sscs = ServerSocketFactory.newInstances(selector, port, SOCKETS_NUMBER);
            for (ServerSocketFactory ssc : sscs) {
                System.out.printf("Server sockets started at:%s%n", ssc.getSSC().getLocalAddress());
            }

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
                    ServerSocketFactory ssf = (ServerSocketFactory) key.attachment();
                    if (key.isAcceptable()) {                       // принять соединение
                        ssf.accept(key);
                        key.cancel();

                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        b.clear();
                        if (sc.read(b) > 0) {  // read all data into buffer
                            b.flip();
                            String s = new String(b.array(), 0, b.limit(), Charset.defaultCharset());
                            System.out.printf("%s:%s", ssf.getSCName(), s);
                            if (s.replaceAll("\\s*", "").equals("cc")) {
                                key.cancel();
                                ssf.closeSC();
                                ssf.registerSSC(SelectionKey.OP_ACCEPT);
                                System.out.printf("client closed%n");
                            }
                            message = s.replaceAll("\\s*", "") + ssf.getSCName();
                        }
                    } else if (key.isWritable()) {
                        if (message.startsWith("aa") && message.endsWith((String) ssf.getSCName())) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            b.clear();
                            String s = String.format("answer %s: at:%2$tT %2$tD%n", ssf.getSCName(), LocalDateTime.now());
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
            if (sscs != null) {
                for (ServerSocketFactory ssf : sscs) {
                    IOUtils.closeChannel(ssf.getSSC(), ssf.getSC());
                }
            }

        }


    }
}
