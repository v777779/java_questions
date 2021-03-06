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
public class MultiCastClientCheck {
    private static final String HOST = "localhost";
    private static final int UDP_PORT = 9910;
    private static final String GROUP_LOCAL = "239.255.0.2";
    private static final String GROUP_MASK = "239.255.0.";
    private static final Charset UTF_CHARSET = Charset.forName("UTF-8");


    public static void main(String[] args) {

        NetworkInterface ni = null;
        DatagramChannel dc = null;
        MembershipKey key = null;
        ByteBuffer b = ByteBuffer.allocate(100);
        int count = 0;
        try {
            ni = NetworkInterface.getByInetAddress(InetAddress.getByName(HOST));
            dc = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(UDP_PORT))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);


            for (int i = 0; i < 10; i++) {
                String s = GROUP_MASK + i;
                if (s.equals(GROUP_LOCAL)) continue;
                dc.join(InetAddress.getByName(s), ni);  // группа ввода
            }

            InetAddress group = InetAddress.getByName(GROUP_LOCAL); // группа вывода

            dc.configureBlocking(false);
            while (true) {
                dc.receive(b);
                if (b.position() > 0) {

                    b.flip();
                    String s = new String(b.array(), 0, b.limit(), UTF_CHARSET);

                    if (s.equals(GROUP_MASK + ":exit")) {
                        break;
                    }
                    System.out.print(s);
                    b.clear();
                }

                ByteBuffer c = ByteBuffer.allocate(100);
                c.put(String.format("UDP:%s ping %d%n", GROUP_LOCAL, count++).getBytes(UTF_CHARSET));
                c.flip();
                System.out.print(new String(c.array(),0,c.limit(),UTF_CHARSET));
                dc.send(c, new InetSocketAddress(group, UDP_PORT));  // отправлять в свою группу
                Thread.sleep(5000);


            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(dc);
        }
        System.out.println("client closed...");
    }
}
