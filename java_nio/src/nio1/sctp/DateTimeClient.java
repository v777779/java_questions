package nio1.sctp;

import com.sun.nio.sctp.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 26-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class DateTimeClient {
    private static final int SERVER_PORT = 3456;
    private static final int US_STREAM = 0;
    private static final int FR_STREAM = 1;

    public static void main(String[] args) {

        InetSocketAddress serverAddr = null;

        try {

            serverAddr = new InetSocketAddress("localhost",
                    SERVER_PORT);
            ByteBuffer buf = ByteBuffer.allocateDirect(60);
            Charset charset = Charset.forName("ISO-8859-1");
            CharsetDecoder decoder = charset.newDecoder();

            SctpChannel sc = SctpChannel.open(serverAddr, 0, 0);

            /* handler to keep track of association setup and termination */
            AssociationHandler assocHandler = new AssociationHandler();

            /* expect two messages and two notifications */
            MessageInfo messageInfo = null;
            do {
                messageInfo = sc.receive(buf, System.out, assocHandler);
                buf.flip();

                if (buf.remaining() > 0 &&
                        messageInfo.streamNumber() == US_STREAM) {

                    System.out.println("(US) " + decoder.decode(buf).toString());
                } else if (buf.remaining() > 0 &&
                        messageInfo.streamNumber() == FR_STREAM) {

                    System.out.println("(FR) " + decoder.decode(buf).toString());
                }
                buf.clear();
            } while (messageInfo != null);

            sc.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class AssociationHandler extends AbstractNotificationHandler<PrintStream> {
        @Override
        public HandlerResult handleNotification(AssociationChangeNotification notification,
                                                PrintStream attachment) {
            if (notification.event().equals(AssociationChangeNotification.AssocChangeEvent.COMM_UP)) {
                int outbound = notification.association().maxOutboundStreams();
                int inbound = notification.association().maxInboundStreams();
                attachment.printf("New association setup with %d outbound streams" +
                        ", and %d inbound streams.\n", outbound, inbound);
            }

            return HandlerResult.CONTINUE;
        }

        @Override
        public HandlerResult handleNotification(ShutdownNotification notification, PrintStream attachment) {
            attachment.printf("The association has been shutdown.\n");
            return HandlerResult.RETURN;
        }
    }

}
