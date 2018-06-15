package nio.socket;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 31-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainSocket {
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
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(false);                           // nonblocking mode
            InetSocketAddress inetAddr = new InetSocketAddress("localhost", port);
            sc.connect(inetAddr);
//            SocketChannel sc2=SocketChannel.open(new InetSocketAddress("localhost",9999)).
            String s = "Remote clientAddress: " + sc.getRemoteAddress();
            System.out.printf("Waiting finish connection at %s%n", s);
            while (!sc.finishConnect()) {
                System.out.print(".");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ByteBuffer b = ByteBuffer.allocate(200);

            int len;
            while ((len = sc.read(b)) >= 0) {  // data read from socket
//                if (len == 0) continue;
                b.flip();
// string
//                byte[] bytes =  new byte[len];
//                b.get(bytes);
//                System.out.printf("%s",new String(bytes,Charset.forName("KOI8-R")));
// char buffer
//                CharBuffer c = Charset.forName("KOI8-R").decode(b);
//                while (c.hasRemaining()) {
//                    System.out.printf("%c", c.get());
//                }
// no encoding
                while (b.hasRemaining()) {
                    System.out.printf("%c", (char)b.get());
                }
                b.clear();
            }
            sc.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(sc);
        }

        System.out.println("Socket finished...");
    }
}
