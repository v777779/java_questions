package nio1.sctp;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 26-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class DateTimeServer {
    private static final int SERVER_PORT = 3456;
    private static final int US_STREAM = 0;
    private static final int FR_STREAM = 1;

    private static final SimpleDateFormat USformatter =
            new SimpleDateFormat("h:mm:ss a EEE d MMM yy, zzzz",Locale.US);
    private static final SimpleDateFormat FRformatter =
            new SimpleDateFormat("h:mm:ss a EEE d MMM yy, zzzz",Locale.FRENCH);


    public static void main(String[] args) {
        SctpServerChannel ssc = null;
        InetSocketAddress serverAddr = null;
        try {

            ssc = SctpServerChannel.open();
            serverAddr = new InetSocketAddress(SERVER_PORT);
            ssc.bind(serverAddr);

            ByteBuffer buf = ByteBuffer.allocateDirect(60);
            CharBuffer cbuf = CharBuffer.allocate(60);
            Charset charset = Charset.forName("ISO-8859-1");
            CharsetEncoder encoder = charset.newEncoder();

            while (true) {
                SctpChannel sc = ssc.accept();

                /* get the current date */
                Date today = new Date();
                cbuf.put(USformatter.format(today)).flip();
                encoder.encode(cbuf, buf, true);
                buf.flip();

                /* send the message on the US stream */
                MessageInfo messageInfo = MessageInfo.createOutgoing(null,
                        US_STREAM);
                sc.send(buf, messageInfo);

                /* update the buffer with French format */
                cbuf.clear();
                cbuf.put(FRformatter.format(today)).flip();
                buf.clear();
                encoder.encode(cbuf, buf, true);
                buf.flip();

                /* send the message on the French stream */
                messageInfo.streamNumber(FR_STREAM);
                sc.send(buf, messageInfo);

                cbuf.clear();
                buf.clear();

                sc.close();
            }

        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeChannel(ssc);
        }
    }


}
