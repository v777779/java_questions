package nio1.selectors.sockets.message;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 27-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UserClientSocket {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 50000;
    private static final String[] MESSAGES = {
            "Message from client",
            "Required new books in library",
            "aa",
            "Please go to the room entrance",
            "Wait for minute in the shop",
            "aa",
            "Second moment client say \"close connection\"!",
            "cc"
    };

    public static void main(String[] args) throws InterruptedException {
        int port = DEFAULT_PORT;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.printf("Use default port:%d", port);
            }
        }

// Client
        SocketChannel sc = null;
        ByteBuffer b = ByteBuffer.allocate(50);
        try {
            sc = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("localhost", port);
            sc.connect(address);
            sc.configureBlocking(false);

            b.clear();
            for (int i = 0; i < MESSAGES.length; i++) {
                b.clear();
                String s = String.format("%s%n", MESSAGES[i]);
                byte[] bytes = s.getBytes(Charset.defaultCharset());
                b.put(bytes);
                b.flip();
                while (b.hasRemaining()) {
                    sc.write(b);
                }
                b.rewind();
                s = new String(b.array(), 0, b.limit(), Charset.defaultCharset());
                System.out.printf("%s", s);
                if (s.contains("cc")) break;

// waiting response 1 sec
                b.clear();
                int count = 5;
                while (count > 0) {
                    if (sc.read(b) > 0) {
                        b.flip();
                        s = new String(b.array(), 0, b.limit(), Charset.defaultCharset());
                        System.out.printf("%s", s);
                    }
                    count--;
                    Thread.sleep(100);
                }


            }

            Thread.sleep(100);
            while (!sc.finishConnect()) {
                System.out.print(".");
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(sc);
        }

        System.out.printf("client closed...%n");
        Thread.sleep(10000);
    }


}
