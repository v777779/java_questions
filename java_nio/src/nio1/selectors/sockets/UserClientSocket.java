package nio1.selectors.sockets;

import util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
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
public class UserClientSocket {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 50000;
    private static final String[] MESSAGES = {
            "Message from client",
            "Required new books in library",
            "Please go to the room entrance",
            "Wait for minute in the shop",
            "Second moment client say \"close connection\"!",
    };

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
        InputStream in = null;
        try {
            sc = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("localhost", port);
            sc.connect(address);
            sc.configureBlocking(false);
            in = System.in;
            b.clear();
            System.out.printf("Please type message and <Enter>(\"aa\" get time, \"cc\" to exit):%n");
            while (true) {
                if (in.available() > 0) {
                    b.clear();
                    byte[] bytes = new byte[in.available()];
                    int len = in.read(bytes);
                    b.put(bytes, 0, len);
                    b.flip();
                    while (b.hasRemaining()) {
                        sc.write(b);
                    }
                    b.rewind();
                    String s = new String(b.array(), 0, b.limit(), StandardCharsets.UTF_8);
                    if (s.replaceAll("\\s*","").equals("cc")) break;
                }
                // check response 1 sec
                b.clear();

                if (sc.read(b) > 0) {
                    b.flip();
                    String s = new String(b.array(), 0, b.limit(), Charset.defaultCharset());
                    System.out.printf("%s", s);
                }
                Thread.sleep(100);
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

    public static void main2(String[] args) {
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
                if (s.contains("close connection")) break;

// waiting response 1 sec
                b.clear();
                int count = 10;
                while (count > 0) {
                    sc.read(b);
                    Thread.sleep(100);
                    count--;
                }
                b.flip();
                if (b.hasRemaining()) {
                    s = new String(b.array(), 0, b.limit(), Charset.defaultCharset());
                    System.out.printf("%s", s);
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
    }


}
