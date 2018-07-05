package nio2.socketgroup_CHECK_THIS;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class AttachmentServer {
    public AsynchronousServerSocketChannel serverChannel;
    public AsynchronousSocketChannel clientChannel;
    public boolean isReadMode;
    public ByteBuffer buffer;
    public SocketAddress clientAddress;
    public Thread serverThread;
}
