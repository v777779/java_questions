package nio2.network.multicast;

import util.IOUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.charset.Charset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MultiCastClient3 {
    private static final String HOST = "localhost";
    private static final int PORT = 9990;
    private static final String GROUP_LOCAL = "239.255.0.3";
    private static final String GROUP_MASK = "239.255.0.";
    private static final Charset UTF_CHARSET = Charset.forName("UTF-8");


    public static void main(String[] args) {

        NetworkInterface ni = null;
        DatagramChannel dc = null;
        MembershipKey key = null;
        ByteBuffer b = ByteBuffer.allocate(100);
        try {
            ni = NetworkInterface.getByInetAddress(InetAddress.getByName(HOST));
            dc = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(PORT))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);


            for (int i = 0; i < 10; i++) {
                String s = GROUP_MASK + i;
                if(s.equals(GROUP_LOCAL)) continue;
                dc.join(InetAddress.getByName(s), ni);  // группа ввода
            }

            InetAddress group = InetAddress.getByName(GROUP_LOCAL); // группа вывода

            dc.configureBlocking(false);
            while (true) {
                dc.receive(b);

                if (b.position() == 0) continue;

                b.flip();
                String s = new String(b.array(), 0, b.limit(), UTF_CHARSET);

                if (s.equals(GROUP_MASK + ":exit")) {
                    break;
                }
                System.out.println(s);
                b.clear();

                ByteBuffer c = ByteBuffer.allocate(100);
                c.put(String.format("from client :%s ping",GROUP_LOCAL).getBytes(UTF_CHARSET));
                c.flip();
                dc.send(c,new InetSocketAddress(group, PORT));  // отправлять в свою группу
                Thread.sleep(500);

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(dc);
        }
        System.out.println("client closed...");
    }
}
