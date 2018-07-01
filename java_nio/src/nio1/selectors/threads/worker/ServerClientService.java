package nio1.selectors.threads.worker;

import util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ServerClientService implements Runnable {
    private final String MESSAHE_HEAD = String.format("HTTP:/1.1 200 OK%n");
    private final Socket sc;
    private final String message;
    private final String scName;

    public ServerClientService(Socket sc, String message) {
        this.sc = sc;
        this.message = message;
        this.scName = String.format("SC%d",sc.getPort());

    }

    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = sc.getInputStream();
            out = sc.getOutputStream();
            String time = String.format("%1$tT %1$tD", LocalDateTime.now());
            String s = String.format("%s %s client:%s at:%s%n", MESSAHE_HEAD, message, scName, time);
            out.write(s.getBytes(Charset.defaultCharset()));
            out.flush();
            out.close();
            in.close();
            System.out.printf("Request processed:%s at:%s",scName,time);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(sc, in, out);
        }
        System.out.printf(" closed...%n");
    }
}
