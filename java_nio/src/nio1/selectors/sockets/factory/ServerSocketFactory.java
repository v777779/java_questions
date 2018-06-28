package nio1.selectors.sockets.factory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 28-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ServerSocketFactory {
    private Selector selector;
    private ServerSocketChannel ssc;
    private ServerSocket ss;
    private SocketChannel sc;
    private final int port;
    private final String sscName;
    private final String scName;

    public ServerSocketFactory(Selector selector, int port) throws IOException {
        this.selector = selector;
        this.port = port;
        this.sscName = String.format("SSC%d", port);
        this.scName = String.format("SC%d", port);
        ssc = ServerSocketChannel.open();                       // channel
        ss = ssc.socket();                                      // socket
        ss.bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        registerSSC(SelectionKey.OP_ACCEPT);     // пока единственная операция
    }


    public static ServerSocketFactory[] newInstances(Selector selector, int startPort, int n) throws IOException {
        if (n <= 0) throw new IllegalArgumentException();
        ServerSocketFactory[] serverSockets = new ServerSocketFactory[n];
        for (int i = 0; i < serverSockets.length; i++) {
            serverSockets[i] = new ServerSocketFactory(selector, startPort + i);
        }

        return serverSockets;
    }


    public SelectionKey registerSSC(int ops) throws IOException {
        return ssc.register(selector, ops, this);

    }

    public SelectionKey registerSC(int ops) throws IOException {
        return sc.register(selector, ops, this);

    }

    public void accept(SelectionKey key) throws IOException {
        if (key.channel() != ssc) return;           // illegal channel
        this.sc = ssc.accept();
        if (sc == null) return;
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, this);
        System.out.printf("%naccepted local:%s remote:%s%n",sc.getLocalAddress(), sc.getRemoteAddress());

    }

    public void closeSSC() throws IOException {
        if (ssc != null) {
            ssc.close();
        }
    }

    public void closeSC() throws IOException {
        if (sc != null) {
            sc.close();
        }
    }

    public ServerSocketChannel getSSC() {
        return ssc;
    }

    public void setSc(SocketChannel sc) throws IOException {
        if (sc == null) return;
        sc.configureBlocking(false);
        this.sc = sc;
    }


    public SocketChannel getSC() {
        return sc;
    }

    public String getSSCName() {
        return sscName;
    }

    public String getSCName() {
        return scName;
    }
}
