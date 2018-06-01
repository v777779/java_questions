package nio.socket;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class DatagramServer {
    private static int parsePort(String[] args, int port) throws NumberFormatException {
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0].replaceAll("\\p{Punct}", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return port;
    }

    private static boolean parseBlock(String[] args, boolean isBlocking) {
        if (args.length > 1) {
            return !args[1].replaceAll("\\p{Punct}", "")
                    .toLowerCase()
                    .contains("n");
        }
        return isBlocking;
    }

    public static void main(String[] args) {

        boolean isBlocking = parseBlock(args, true);
        int port = parsePort(args, 9997);

        System.out.printf(FORMAT, "DatagramChannel Server:");
        System.out.printf("parameters: 9996 [/b,/n]%n");
        DatagramChannel dc = null;
        try {
            System.out.printf("Server listening port:%d  blocking:%s%n", port, isBlocking ? "on" : "off");
            dc = DatagramChannel.open();
            dc.socket().bind(new InetSocketAddress(port));
// config
            if (!isBlocking) {
                dc.configureBlocking(false);  // nonblocking mode can be commented
            }

            ByteBuffer bSymbol = ByteBuffer.allocate(4);
            ByteBuffer bPayload = ByteBuffer.allocate(16);

            while (true) {
                bSymbol.clear();
                bPayload.clear();
                SocketAddress socketAddress = dc.receive(bSymbol);
// config
                if (socketAddress != null) {
                    String s = new String(bSymbol.array(), 0, 4, Charset.forName("UTF-8"));
                    System.out.printf("%nReceiver request from: %s symbol:%s%n", socketAddress, s);

                    if (s.toUpperCase().equals("MSFT")) {
                        bPayload.putFloat(0, 37.40f); // отсылаем на запрос
                        bPayload.putFloat(4, 57.22f);
                        bPayload.putFloat(8, 72.48f);
                        bPayload.putFloat(12, 91.41f);
                    } else {
                        bPayload.putFloat(0, 0f); // отсылаем на запрос
                        bPayload.putFloat(4, 0f);
                        bPayload.putFloat(8, 0f);
                        bPayload.putFloat(12, 0f);
                    }
                    dc.send(bPayload, socketAddress);
                } else {
                    if (!isBlocking) {
                        Thread.sleep(500);
                        System.out.print(".");
                    } else {
                        throw new IOException("SocketAddress=null");
                    }
                }

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(dc);
        }
    }
}
