package nio1.selectors.sockets.server;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 28-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ServerChat implements Runnable {
    private static final String WELCOME_MESSAGE = String.format("Welcome to NIO Chat%n");

    private final int port;
    private ServerSocketChannel ssc;
    private Selector selector;
    private ByteBuffer b;
    private boolean isClosed;

    public ServerChat(int port) throws IOException {
        if (port <= 0) throw new IllegalArgumentException();
        this.port = port;
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        this.b = ByteBuffer.allocate(2000);
        this.isClosed = false;
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
        sc.configureBlocking(false);

        String attachment = sc.socket().getInetAddress().toString() + ":" + sc.socket().getPort();
        sc.register(selector, SelectionKey.OP_READ, attachment);
        b.clear();
        b.put(WELCOME_MESSAGE.getBytes(Charset.defaultCharset()));
        b.flip();
        while (b.hasRemaining()) {
            sc.write(b);
        }
        System.out.printf("%naccepted connection from:%s%n", attachment);
    }

    private void handleRead(SelectionKey key) {

    }

    @Override
    public void run() {
        System.out.printf("Server started on port:%d%n", port);
        try {
            Iterator<SelectionKey> it = null;
            SelectionKey key = null;

            while (ssc.isOpen() && !isClosed) {
                int n = selector.select(500); // nonblocking operation
                if (n == 0) {
                    System.out.print(".");
                    continue;
                }
                it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(ssc, selector);
        }

        System.out.printf("server finished...%n");
    }


    synchronized public boolean isClosed() {
        return isClosed;
    }

    synchronized public void close() {
        isClosed = true;
    }

    public static void main(String[] args) {
        try {
            ServerChat serverChat = new ServerChat(9990);
            ExecutorService exec = Executors.newFixedThreadPool(1);
            exec.execute(serverChat);

            Thread.sleep(60000);


            serverChat.close();
            exec.shutdown();
            if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                System.out.printf("Can't terminate thread...%n");
            } else {
                System.out.printf("Thread terminated normally...%n");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
