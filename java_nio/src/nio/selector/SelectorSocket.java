package nio.selector;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class SelectorSocket {
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
        System.out.printf(FORMAT, "Client starting... connecting port: " + port);

        SocketChannel sc = null;
        ByteBuffer bb;
        try {
            sc = SocketChannel.open();
            bb = ByteBuffer.allocateDirect(8);

            sc = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("localhost",port);
            sc.connect(address);
            long time = 0;
            while(sc.read(bb) != -1) {
                bb.flip();
                while(bb.hasRemaining()) {
//                    time <<= 8;
//                    time |= bb.get()&255;
                    time = bb.getLong();
                }
                bb.clear();
            }
            Instant instant = Instant.ofEpochMilli(time);
            LocalDateTime localTime = LocalDateTime.ofInstant(instant,ZoneId.systemDefault());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
            System.out.printf("date: %s",localTime.format(dateTimeFormatter));

            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(sc);

        }

    }
}
