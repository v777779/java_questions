package nio1.selectors.sockets.arc.chatm;

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
    private static final String MARKER_DISCONNECT = "disconnect\\s*";

    private static final Queue<UserTask> q = new LinkedList<>();
    private static final Map<String, StringBuilder> map = new HashMap<>();
    private static final ByteBuffer b = ByteBuffer.allocate(2048);

    public static void main(String[] args) {
        ServerSocketChannel ssc = null;
        SocketChannel sc = null;
        Selector selector;
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(HOST, PORT));
            LocalDateTime finishTime = LocalDateTime.now().plus(SESSION, ChronoUnit.MILLIS);
            ssc.configureBlocking(false);
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.printf("server started...%n");
// clients
            ClientService.runTelnet(HOST,PORT);
            ClientService.runTelnet(HOST,PORT);
            ClientService.run(HOST,PORT);
            ClientService.runPutty(HOST,PORT);

            while (!LocalDateTime.now().isAfter(finishTime)) {
                int n = selector.select(100);
                if (n == 0) {
                    if(map.isEmpty() || selector.keys().size() > 1)
                    continue;
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
                        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, String.format("SC%d", sc.socket().getPort()));
                        putMessage(sc, String.format("Welcome to NIO Chat%n"));
                        System.out.printf("%s:entered to chatcmd%n",sc.getRemoteAddress());

                    } else if (key.isReadable()) {  // data arrived from channel
                        sc = (SocketChannel) key.channel();
                        if (sc == null) continue;
                        String name = (String) key.attachment();
                        if (map.get(name) == null) map.put(name, new StringBuilder());
                        StringBuilder sb = map.get(name);
// read
                        b.clear();
                        int len;
                        if ((len = sc.read(b)) > 0) {
                            b.flip();
                            String s = new String(b.array(), 0, b.limit(), TELNET_CHARSET);
                            sb.append(s);
                            if (sb.toString().matches(MARKER_DISCONNECT)) {
                                System.out.printf("%s:left chatcmd%n",sc.getRemoteAddress());
                                sc.close();     // closes all keys connected with channel
                            }
                            if (s.matches("(?s:.*\n.*)")) {  // last character of s
                                broadcast(selector, name);
                            }
                        } else if (len == -1) {
                            System.out.printf("%s:left chatcmd%n",sc.getRemoteAddress());
                            sc.close();         // closes all keys connected with channel
                        }
                    } else if (key.isWritable()) {
                        Iterator<UserTask> itTask = q.iterator();
                        while (itTask.hasNext()) {
                            UserTask task = itTask.next();
                            if (task.sc == key.channel()) {
// write
                                sc = (SocketChannel) key.channel();
                                b.clear();
                                b.put(task.message.getBytes(TELNET_CHARSET));
                                b.flip();
                                while (b.hasRemaining()) {
                                    sc.write(b);
                                }

                                itTask.remove();
                                break;
                            }
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("server closed...%n");
    }


    private static String getName(SocketChannel sc) {
        return String.format("SC%d", sc.socket().getPort());
    }

    private static void putMessage(SocketChannel sc, String message) {
        q.add(new UserTask(sc, message));
    }

    private static void broadcast(Selector selector, String name) {
        Set<SelectionKey> set = selector.keys();

        String s = map.get(name).toString();
        map.get(name).setLength(0);

        String message = String.format("%s:%s", name, s);
        for (SelectionKey key : set) {
            if (key.isValid() && (key.interestOps() & SelectionKey.OP_WRITE) > 0) {
                putMessage((SocketChannel) key.channel(), message);
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
