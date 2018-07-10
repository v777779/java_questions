package nio2.network;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
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
 * Date: 04-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ChatServer {
    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final long SESSION = 50000;
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final Charset PUTTY_CHARSET = Charset.forName("UTF-8");


    private static final ByteBuffer b = ByteBuffer.allocate(2048);
    private static int count = 0;

    public static void runTelnet(String host, int port) throws IOException {
        Runtime.getRuntime().exec("cmd /c start telnet " + host + " " + port);
    }

    public static void runPutty(String host, int port) throws IOException {
        Runtime.getRuntime().exec("./_lib/putty -raw " + host + " " + port);

    }


    public static void main(String[] args) {
        ServerSocketChannel ssc = null;
        SocketChannel sc = null;
        Selector selector = null;
        try {
            ssc = ServerSocketChannel.open();
//            ssc.socket().bind(new InetSocketAddress(HOST, PORT));     // вместо socket().bind()
            ssc.bind(new InetSocketAddress(HOST, PORT));                            // bind()
            LocalDateTime finishTime = LocalDateTime.now().plus(SESSION, ChronoUnit.MILLIS);
            ssc.configureBlocking(false);
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.printf("server started at:%s...%n", ssc.getLocalAddress());  // getLocalAddress()
            System.out.println("Supported Options:");
            for (SocketOption<?> option : ssc.supportedOptions()) {
                System.out.printf("%-10s:%s%n", option.name(), option.type().getSimpleName());
            }
// clients
            runTelnet(HOST, PORT);
            runTelnet(HOST, PORT);
            runPutty(HOST, PORT);

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
                        UserAttachment attachment = new UserAttachment(sc.socket().getPort());
                        sc.register(selector, SelectionKey.OP_READ, attachment);
                        sendMessage(sc, String.format("Welcome to NIO Chat%n"), attachment.charset());
                        System.out.printf("%s %s:entered to chat%n", sc.getRemoteAddress(), sc.getLocalAddress());
                        count++;

                    } else if (key.isReadable()) {  // data arrived from channel
                        sc = (SocketChannel) key.channel();
                        if (sc == null) continue;
// read
                        String s = readMessage(sc, key);
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

    private static String readMessage(SocketChannel sc, SelectionKey key) throws IOException {
        UserAttachment att = (UserAttachment) key.attachment();
        try {
            b.clear();
            int len;
            if ((len = sc.read(b)) > 0) {
                b.flip();
                String s = att.checkCharset(new String(b.array(), 0, b.limit(), att.charset()));
                return s;

            } else if (len == -1) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return "";
    }

    private static void sendMessage(SocketChannel sc, String s, Charset charset) throws IOException {
        b.clear();
//        String message = String.format("SC%d:%s", sc.socket().getPort(), s);
//        b.put(message.getBytes(TELNET_CHARSET));

        b.put(s.getBytes(charset));
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
                Charset charset = ((UserAttachment) key.attachment()).charset();
                sendMessage(sc, s, charset);
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

    private static class UserAttachment {
        private final String message;
        private boolean isChanged;
        private Charset charset;

        public UserAttachment(int port) {
            this.message = String.format("SC%d", port);
            this.isChanged = false;
            this.charset = TELNET_CHARSET;
        }

        private void changeCharset() {
            isChanged = true;
            charset = PUTTY_CHARSET;
        }

        public Charset charset() {
            return charset;
        }

        public boolean isChanged() {
            return isChanged;
        }

        public void setChanged() {
            isChanged = true;
        }

        public String checkCharset(String s) {
            if (!isChanged) {
                for (char c : s.toCharArray()) {
                    if (c > 0x2000) {
                        changeCharset();
                        return new String(b.array(), 0, b.limit(), charset);
                    }
                }
                for (char c : s.toCharArray()) {
                    if (c > 0x0400) {
                        setChanged();
                        return s;
                    }
                }
            }
            return s;
        }
    }

}
