package nio1.selectors.sockets.factory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

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
    private List<SocketChannel> listSC;


    public ServerSocketFactory(Selector selector, int port) throws IOException {
        this.selector = selector;
        this.listSC = new ArrayList<>();

        ssc = ServerSocketChannel.open();                       // channel
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT, this);
    }


    public static ServerSocketFactory[] newInstances(Selector selector, int startPort, int n) throws IOException {
        if (n <= 0) throw new IllegalArgumentException();
        ServerSocketFactory[] serverSockets = new ServerSocketFactory[n];
        for (int i = 0; i < serverSockets.length; i++) {
            serverSockets[i] = new ServerSocketFactory(selector, startPort + i);
        }
        return serverSockets;
    }


    public SocketChannel accept(SelectionKey key) throws IOException {
        if (key.channel() != ssc) return null;           // illegal channel
        SocketChannel sc = ssc.accept();
        if (sc == null) return null;
        sc.configureBlocking(false);
        listSC.add(sc);
        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, this);
        System.out.printf("%naccepted local:%s remote:%s%n", sc.getLocalAddress(), sc.getRemoteAddress());
        return sc;
    }

    public void closeSSC() throws IOException {
        if (ssc != null) {
            ssc.close();
        }
    }

    public boolean closeSC(SocketChannel sc) throws IOException {
        if (sc == null) return false;
        for (SocketChannel socketChannel : listSC) {
            if (socketChannel.socket().getPort() == sc.socket().getPort()) {
                socketChannel.close();
                return true;
            }
        }
        return false;
    }

    public ServerSocketChannel getSSC() {
        return ssc;
    }


    public List<SocketChannel> getSC() {
        return listSC;
    }

    public String getSSCName() {
        if (ssc == null) return null;
        return String.format("SSC%d", ssc.socket().getLocalPort());
    }

    public String getSCName(SocketChannel sc) {
        if (sc == null) return null;

        return String.format("SC%d", sc.socket().getPort());
    }
}
