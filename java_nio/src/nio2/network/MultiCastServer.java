package nio2.network;

import util.IOUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MultiCastServer {
    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final String GROUP_LOCAL = "239.255.0.1";
    private static final String GROUP_MASK = "239.255.0.";
    private static final Charset UTF_CHARSET = Charset.forName("UTF-8");


    public static void main(String[] args) {
        NetworkInterface ni = null;
        DatagramChannel dc = null;
        ByteBuffer b = ByteBuffer.allocate(50);
        try {

            ni = NetworkInterface.getByInetAddress(InetAddress.getByName(HOST));
            dc = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(PORT))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            dc.configureBlocking(false);

            for (int i = 0; i < 10; i++) {
                String s = GROUP_MASK + i;
                if(s.equals(GROUP_LOCAL)) continue;
                dc.join(InetAddress.getByName(s), ni);  // группа ввода
            }

            InetAddress group = InetAddress.getByName(GROUP_LOCAL); // группа на вывод

            System.out.printf("server started at:%s group:%s%n", dc.getLocalAddress(), group.getHostAddress());
            int i = 0;
            while (true) {
                b.clear();
                b.put(("line" + i).getBytes(Charset.forName("UTF-8")));
                b.flip();
                dc.send(b, new InetSocketAddress(group, PORT));
                i++;
                if (i == 1050) break;
// check
                b.clear();
                SocketAddress sa = dc.receive(b);
                if(b.position() > 0) {
                    b.flip();
                    System.out.printf("%n%s:%s%n",sa,new String(b.array(),0,b.limit(),UTF_CHARSET));
                }
                Thread.sleep(400);
                System.out.print(".");
            }
            System.out.println();
// send exit
            b = ByteBuffer.wrap((GROUP_MASK + ":exit").getBytes(UTF_CHARSET));
            dc.send(b, new InetSocketAddress(group, PORT)); // exit from the group

            b.clear();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(dc);
        }
        System.out.println("server stopped...");

    }
}
