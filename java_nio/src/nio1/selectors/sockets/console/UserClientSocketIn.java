package nio1.selectors.sockets.console;

import util.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 27-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UserClientSocketIn {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 50000;


    public static void main(String[] args) {
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
        ByteBuffer b = ByteBuffer.allocate(2000);
        BufferedInputStream bIn = null;

        try {
            sc = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("localhost", port);
            sc.connect(address);
            sc.configureBlocking(false);
            bIn = new BufferedInputStream(System.in);

            b.clear();
            System.out.printf("Please type message and <Enter>(\"aa\" get time, \"cc\" to exit):%n");
            String s;
            while (true) {
                byte[] bytes = new byte[2000];
                int len;
                if ((len = bIn.read(bytes)) > 0) {
                    b.clear();
                    b.put(bytes, 0, len);

                    b.flip();
                    while (b.hasRemaining()) {
                        sc.write(b);
                    }
                    b.rewind();
                    s = new String(b.array(), 0, b.limit(), StandardCharsets.UTF_8);
                    System.out.printf("%s", s);
                    if (s.replaceAll("\\s*", "").equals("cc")) break;
                }
                // check response 0.5 sec
                b.clear();
                for (int i = 0; i < 5; i++) {
                    if (sc.read(b) > 0) {
                        b.flip();
                        s = new String(b.array(), 0, b.limit(), Charset.defaultCharset());
                        System.out.printf("%s", s);
                    }
                    Thread.sleep(100);
                }
            }


            while (!sc.finishConnect()) {
                System.out.print(".");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(sc);
        }
        System.out.printf("client closed%n");
    }


}
