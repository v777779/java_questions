package nio1.selectors.threads.udp;

import util.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 05-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class SimpleEcho {
    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final String HOST = "localhost";
    private static final int PORT = 9990;

    public static void runTelnet(String host, int port) throws IOException {
        Runtime.getRuntime().exec("cmd /c start telnet " + host + " " + port);
    }

    public static void runPutty(String host, int port) throws IOException {
        Runtime.getRuntime().exec("./_lib/putty -raw " + host + " " + port);

    }

    public static void main(String[] args) {
        ServerSocket ssc = null;
        Socket sc = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        try {

            InetAddress inetAddress = InetAddress.getByName(HOST);
            System.out.printf("inet:%s  n:%s a:%s%n", inetAddress,
                    inetAddress.getHostName(), inetAddress.getHostAddress());

            runTelnet(HOST,PORT);

            ssc = new ServerSocket();
            ssc.bind(new InetSocketAddress(HOST, PORT));

            System.out.printf("inet:%s  socket%s %n", ssc.getInetAddress(), ssc.getLocalSocketAddress());

            System.out.printf("server started...%n");
            sc = ssc.accept();
            br = new BufferedReader(new InputStreamReader(sc.getInputStream(), TELNET_CHARSET));
            pw = new PrintWriter(sc.getOutputStream(), true, TELNET_CHARSET);

            while (true) {
                String s;
                if ((s = br.readLine()) == null) break;
                pw.println("udp:" + s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ssc, sc, br, pw);
        }

        System.out.printf("server closed...%n");
    }
}
