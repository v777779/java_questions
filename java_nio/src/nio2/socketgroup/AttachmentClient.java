package nio2.socketgroup;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class AttachmentClient {
    public AsynchronousSocketChannel clientChannel;
    public boolean isReadMode;
    public ByteBuffer buffer;
    public Thread clientThread;
}
