package nio1.selectors.sockets.chat.demo;

import nio1.selectors.sockets.chat.chatm.ClientService;
import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 04-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ChatServer {
    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final long SESSION = 50000;
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");

    private static final ByteBuffer b = ByteBuffer.allocate(2048);
    private static int count = 0;

    public static void main(String[] args) {
        ServerSocketChannel ssc = null;
        SocketChannel sc = null;
        Selector selector = null;
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(HOST, PORT));
            LocalDateTime finishTime = LocalDateTime.now().plus(SESSION, ChronoUnit.MILLIS);
            ssc.configureBlocking(false);
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.printf("server started...%n");
// clients
            ClientService.runTelnet(HOST, PORT);
            ClientService.runTelnet(HOST, PORT);
            ClientService.run(HOST, PORT);
            ClientService.runPutty(HOST, PORT);

            while (!LocalDateTime.now().isAfter(finishTime)) {
                int n = selector.select(100);
                if (n == 0) {
                    if (selector.keys().size() > 1 || count == 0) continue;
                    else break;
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        sc = ((ServerSocketChannel) key.channel()).accept();
                        if (sc == null) continue;
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ, String.format("SC%d", sc.socket().getPort()));
                        sendMessage(sc, String.format("Welcome to NIO Chat%n"));
                        System.out.printf("%s:entered to chat%n", sc.getRemoteAddress());
                        count++;

                    } else if (key.isReadable()) {  // data arrived from channel
                        sc = (SocketChannel) key.channel();
                        if (sc == null) continue;
// read
                        String s = readMessage(sc);
                        if (s == null) {
                            System.out.printf("%s:left chat%n", sc.getRemoteAddress());
                            sc.close();         // closes all keys connected with channel
                        } else if (s.length() > 0) {
                            broadcast(selector, key, s);
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.printf("Exception:%s%n", e);
        } finally {
            IOUtils.close(ssc, selector);
        }
        System.out.printf("server closed...%n");
    }

    private static String readMessage(SocketChannel sc) throws IOException {
        try {
            b.clear();
            int len;
            if ((len = sc.read(b)) > 0) {
                b.flip();
                return new String(b.array(), 0, b.limit(), TELNET_CHARSET);

            } else if (len == -1) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return "";
    }

    private static void sendMessage(SocketChannel sc, String s) throws IOException {
        b.clear();
//        String message = String.format("SC%d:%s", sc.socket().getPort(), s);
//        b.put(message.getBytes(TELNET_CHARSET));
        b.put(s.getBytes(TELNET_CHARSET));
        b.flip();
        while (b.hasRemaining()) {
            sc.write(b);
        }
    }

    private static void broadcast(Selector selector, SelectionKey src, String s) throws IOException {
        Set<SelectionKey> set = selector.keys();

        for (SelectionKey key : set) {
            if (key == src) continue;
            if (key.isValid() && (key.interestOps() & SelectionKey.OP_READ) > 0) {
                SocketChannel sc = (SocketChannel) key.channel();
                sendMessage(sc, s);
            }
        }
    }

    private static class UserTask {
        private final SocketChannel sc;
        private final String message;

        public UserTask(SocketChannel sc, String message) {
            this.sc = sc;
            this.message = message;
        }
    }
}
