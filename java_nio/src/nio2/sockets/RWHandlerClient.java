package nio2.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.CompletionHandler;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class RWHandlerClient implements CompletionHandler<Integer, AttachmentClient> {
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // клиент с клавиатурой

    @Override
    public void completed(Integer result, AttachmentClient attachment) {
        if (attachment.isReadMode) {
// read
            attachment.buffer.flip();
            int limit = attachment.buffer.limit();
            byte[] bytes = new byte[limit];
            attachment.buffer.get(bytes); // буфер прямо в размер данных
            String s =new String(bytes, MyConnHandler.CHARSET);
// text demo
            System.out.printf("Server responded:%s%n", s);
// text demo
            if(s.matches(MyConnHandler.CLOSE_SERVER)) {
                attachment.clientThread.interrupt();
                return;
            }
// new message
            try {
                s = "";
                while (s.length() == 0) {
                    s = br.readLine();
                    if (s.isEmpty()) {
                        attachment.clientThread.interrupt(); // interrupt Client and exit from handler
                        return;
                    }
                }
                attachment.isReadMode = false;
                attachment.buffer.clear();
                bytes = s.getBytes(MyConnHandler.CHARSET);
                attachment.buffer.put(bytes);
                attachment.buffer.flip();
                attachment.clientChannel.write(attachment.buffer, attachment, this);
            } catch (IOException e) {
                System.out.printf("Unable read from console:%s%n", e);
            }
        } else {
// read
            attachment.isReadMode = true;
            attachment.buffer.clear();
            attachment.clientChannel.read(attachment.buffer, attachment, this); // this provides Attachment type
        }
    }

    @Override
    public void failed(Throwable exc, AttachmentClient attachment) {
        System.out.printf("Connection with client broken%n");
        attachment.clientThread.interrupt();
    }
}
