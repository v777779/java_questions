package nio.socket;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 31-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainServerSocket {
    public static void main(String[] args) {
        int port = 9999;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
        }

        System.out.printf(FORMAT, "SocketChannel( assert: java -ea):");
        ServerSocketChannel ssc = null;
        SocketChannel sc = null;

        try {
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(port));     // port:9999
            ssc.configureBlocking(false);                           // nonblocking mode
            String s = String.format("Local address: %s%n", ssc.socket().getLocalSocketAddress());
            System.out.printf("Server started at %s", s);
            ByteBuffer b = ByteBuffer.wrap(s.getBytes(Charset.forName("UTF-8")));
            while (true) {
                sc = ssc.accept();
                if (sc != null) {
                    System.out.printf("%nReceived connection from: %s%n",
                            sc.socket().getRemoteSocketAddress());
                    b.rewind();
                    sc.write(b);            // запись из буфера в SocketChannel
                    sc.close();             // закрыть сразу
                }else {
                    System.out.print(".");
                    Thread.sleep(500);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(ssc, sc);
        }
        System.exit(0);
    }
}
