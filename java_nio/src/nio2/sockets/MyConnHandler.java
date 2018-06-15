package nio2.sockets;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MyConnHandler implements CompletionHandler<AsynchronousSocketChannel,AttachmentServer> {
    public  static final int BUFFER_SIZE = 400;
    public static final Charset CHARSET = Charset.forName("KOI8-R");//StandardCharsets.UTF_8;
    public static final String CLOSE_SERVER ="close";

    @Override
    public void completed(AsynchronousSocketChannel clientChannel, AttachmentServer attachment) {
        try {
            SocketAddress clientAddress = clientChannel.getRemoteAddress();
            System.out.printf("Accepted connection from:%s  local:%s%n",clientAddress,clientChannel.getLocalAddress());
// используется только поле serverChannel на самом деле
//            AttachmentServer serverAttachment = new AttachmentServer();
//            serverAttachment.serverChannel = attachment.serverChannel;
            attachment.serverChannel.accept(attachment,this);

            AttachmentServer myAttachment = new AttachmentServer();
            myAttachment.serverChannel = attachment.serverChannel;
            myAttachment.clientChannel = clientChannel;
            myAttachment.isReadMode = true;
            myAttachment.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            myAttachment.clientAddress = clientAddress;
            myAttachment.serverThread = attachment.serverThread;

            RWHandlerServer rwHandler = new RWHandlerServer();
// attachment предоставляет rwHandler доступ к собственно буферу
            clientChannel.read(myAttachment.buffer,myAttachment,rwHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, AttachmentServer attachment) {
        System.out.printf("Server failed to accept connection%n");
    }
}
