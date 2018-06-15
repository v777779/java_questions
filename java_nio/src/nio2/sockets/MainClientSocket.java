package nio2.sockets;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Executors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainClientSocket {
    private static final int PORT = 9090;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        AsynchronousSocketChannel clientChannel = null;
        AsynchronousChannelGroup group = null;
        try {
            group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
            clientChannel = AsynchronousSocketChannel.open();
            clientChannel.connect(new InetSocketAddress(HOST, PORT));
            System.out.printf("Client at %s connected%n", clientChannel.getLocalAddress());

            AttachmentClient attachment = new AttachmentClient();
            attachment.clientChannel = clientChannel;
            attachment.isReadMode = false;
            attachment.buffer = ByteBuffer.allocate(MyConnHandler.BUFFER_SIZE);
            attachment.clientThread = Thread.currentThread();

// записать первое сообщение серверу
            byte[] bytes = "Type сообщение(<close> to close Server, Client and exit...".getBytes(MyConnHandler.CHARSET);
            attachment.buffer.put(bytes); // все данные в буфер
            attachment.buffer.flip();
            clientChannel.write(attachment.buffer, attachment, new RWHandlerClient());

            System.out.printf("Client sent data to Server and waiting for answer...%n");
            System.out.printf("Type message(<Enter> to exit...%n");

// ожидаем работы с сервером через CompletionHandler<Integer,<? extends A> RWHandlerClient
//            Thread.currentThread().join();  // тоже самое
            attachment.clientThread.join(10000);

        } catch (IOException | IllegalStateException e) {
            System.out.printf("Unable to open AsynchronousSocketChannel:%s%n", e);
        } catch (InterruptedException e) {
            System.out.printf("Client interrupted:%s%n", e);
        } finally {
            IOUtils.close(clientChannel);
        }
        System.out.printf("Client closed%n");

        System.out.printf("Shutdown group...%n");
        if (group != null) {
            group.shutdown();
            while (!group.isShutdown()) {
            }
        }
        System.out.printf("group is shutdown%n");
    }
}
