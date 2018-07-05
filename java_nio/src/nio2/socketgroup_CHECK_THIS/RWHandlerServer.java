package nio2.socketgroup_CHECK_THIS;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CompletionHandler;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class RWHandlerServer implements CompletionHandler<Integer, AttachmentServer> {

    @Override
    public void completed(Integer result, AttachmentServer attachment) {
        if (result == -1) {  // accept not provided
            try {
                SocketAddress address = attachment.clientChannel.getRemoteAddress();
                attachment.clientChannel.close();
                System.out.printf("Stopped listening to client from:%s%n", address);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (attachment.isReadMode) {
// read
            attachment.buffer.flip();
            int limit = attachment.buffer.limit();
            byte[] bytes = new byte[limit];
            attachment.buffer.get(bytes); // буфер прямо в размер данных
            String s = new String(bytes, MyConnHandler.CHARSET);
// text demo
            System.out.printf("Client sent:%s%n", s);
// text demo
            attachment.buffer.clear();
            attachment.buffer.put(s.getBytes(MyConnHandler.CHARSET));
            attachment.buffer.flip();
// write back to client
            attachment.isReadMode = false;
            attachment.buffer.rewind();
            attachment.clientChannel.write(attachment.buffer, attachment, this);
// close server by command
            if (s.matches(MyConnHandler.CLOSE_SERVER)) {
                attachment.serverThread.interrupt();
            }

        } else {
// write
            attachment.isReadMode = true;
            attachment.buffer.clear();
            attachment.clientChannel.read(attachment.buffer, attachment, this); // this provides Attachment type
        }
    }

    @Override
    public void failed(Throwable exc, AttachmentServer attachment) {
        System.out.printf("Connection with client broken%n");
    }
}
