package nio.selector;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class SelectorServerSocket {
    public static void main(String[] args) {
        int port = 9995;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
        }

        System.out.printf(FORMAT, "Server starting... listening port: " + port);

        ServerSocketChannel ssc = null;
        ByteBuffer bb;
        try {
            ssc = ServerSocketChannel.open();
            bb = ByteBuffer.allocateDirect(8);

            ServerSocket ss = ssc.socket();
            ss.bind(new InetSocketAddress(port));
            ssc.configureBlocking(false);
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT); // ожидает соединения

            while (true) {
                int n = selector.select(500); // blocking method
                if (n == 0) {
                    System.out.print(".");
                    Thread.sleep(500);
                    continue;
                }

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    if(key.isAcceptable()) {
                        SocketChannel sc = ((ServerSocketChannel)key.channel()).accept();
                        if(sc == null) continue;
                        System.out.printf("Receiving connection%n");
                        bb.clear();
                        bb.putLong(System.currentTimeMillis());
                        bb.flip();
                        while(bb.hasRemaining()){   // write ByteBuffer to SocketChannel of ServerSocketChannel
                            sc.write(bb);
                        }
                        sc.close();
                    }
                    it.remove();
                }
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(ssc);
        }

    }

}
