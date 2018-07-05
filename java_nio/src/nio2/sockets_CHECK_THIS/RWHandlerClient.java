package nio2.sockets_CHECK_THIS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class RWHandlerClient implements CompletionHandler<Integer, AttachmentClient> {
    private InputStream in;
    private BufferedReader br;

    public RWHandlerClient() {
        this.in = System.in;
        this.br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset())); // клиент с клавиатурой
    }

    @Override
    public void completed(Integer result, AttachmentClient attachment) {
        try {
            br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset())); // клиент с клавиатурой
            if (attachment.isReadMode) {
// read
                attachment.buffer.flip();
                int limit = attachment.buffer.limit();
                byte[] bytes = new byte[limit];
                attachment.buffer.get(bytes); // буфер прямо в размер данных
                String s = new String(bytes, MyConnHandler.CHARSET);
// text demo
                System.out.printf("Server responded:%s%n", s);
// text demo
                if (s.matches(MyConnHandler.CLOSE_SERVER)|| s.isEmpty()) {
                    attachment.clientThread.interrupt();
                    return;
                }

// new message
                s = "";
                while (s.length() == 0) {
                    if (in.available() == 0) {
                        Thread.sleep(100);
                        continue;
                    }
                    s = br.readLine();
                }
                attachment.isReadMode = false;
                attachment.buffer.clear();
                bytes = s.getBytes(MyConnHandler.CHARSET);
                attachment.buffer.put(bytes);
                attachment.buffer.flip();
                attachment.clientChannel.write(attachment.buffer, attachment, this);
            } else {
// read
                attachment.isReadMode = true;
                attachment.buffer.clear();
                attachment.clientChannel.read(attachment.buffer, attachment, this); // this provides Attachment type
            }
        } catch (IOException e) {
            System.out.printf("Unable read from console:%s%n", e);
        } catch (InterruptedException e) {
            System.out.printf("Client interrupted:%s%n", e);
        }
    }

    @Override
    public void failed(Throwable exc, AttachmentClient attachment) {
        System.out.printf("Connection with client broken%n");
        attachment.clientThread.interrupt();
    }
}
