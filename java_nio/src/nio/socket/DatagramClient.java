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
public class DatagramClient {

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

    private static String parseSym(String[] args, String s) {
        if (args.length > 1) {
            return args[1].replaceAll("\\p{Punct}", "")
                    .substring(0, 4)
                    .toUpperCase();
        }
        return s;
    }

    private static boolean parseConn(String[] args, boolean isConnectRequired) {
        if (args.length > 2) {
            return args[2].replaceAll("\\p{Punct}", "")
                    .toLowerCase()
                    .contains("w");
        }
        return isConnectRequired;
    }

    private static boolean parseBlock(String[] args, boolean isBlocking) {
        if (args.length > 3) {
            return !args[3].replaceAll("\\p{Punct}", "")
                    .toLowerCase()
                    .contains("n");
        }
        return isBlocking;
    }


    public static void main(String[] args) {
// config
        boolean isConnectRequired = parseConn(args, false);  // !/w
        boolean isBlocking = parseBlock(args, true);               // !/n

        int port = parsePort(args, 9997);                              //9997
        String symbol = parseSym(args, "MSFT");                          // MSFT


        System.out.printf(FORMAT, "DatagramChannel Client:");
        System.out.printf("parameters: 9996 [MSFT,MSBT]  [/s,/w] [/b,/n]%n");
        DatagramChannel dc = null;
        try {
            System.out.printf("Client request port:%d  symbol:%s  conn:%s  blocking:%s%n", port, symbol,
                    isConnectRequired ? "true write()" : "false send()", isBlocking ? "on" : "off");
            ByteBuffer bSymbol = ByteBuffer.wrap(symbol.getBytes(Charset.forName("UTF-8")));
            ByteBuffer bPayload = ByteBuffer.allocate(16);
            InetSocketAddress socketAddress = new InetSocketAddress("localhost", port);
            dc = DatagramChannel.open();
// config
            if (!isBlocking) {
                dc.configureBlocking(false);
            }
            if (!isConnectRequired) {                           // no connection
                dc.send(bSymbol, socketAddress);
                System.out.printf("Client connected: %b%n", dc.isConnected());
            } else {
                dc.connect(socketAddress);
                System.out.printf("Client connected: %b%n", dc.isConnected());
                dc.write(bSymbol);
            }
// config
            SocketAddress responseAddress = null;
            while (responseAddress == null) {
                responseAddress = dc.receive(bPayload);
                System.out.print(".");
                Thread.sleep(10);
            }

            System.out.printf("%nReceiving datagram frome: %s  pos: %d%n", responseAddress, bPayload.position());
            System.out.printf("open price : %.2f%n", bPayload.getFloat(0));
            System.out.printf("low price  : %.2f%n", bPayload.getFloat(4));
            System.out.printf("high price : %.2f%n", bPayload.getFloat(8));
            System.out.printf("close price: %.2f%n", bPayload.getFloat(12));


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(dc);
        }
        System.out.printf("Datagram client closed%n");
    }
}
