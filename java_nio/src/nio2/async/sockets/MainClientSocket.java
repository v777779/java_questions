package nio2.async.sockets;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainClientSocket {
    private static final int PORT = 9090;
    private static final String HOST = "localhost";

    private static class Factory implements ThreadFactory {
        private ThreadFactory factory;
        private List<Thread> list;

        public Factory(ThreadFactory factory) {
            this.factory = factory;
            this.list = new ArrayList<>();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = factory.newThread(r);
            list.add(thread);
            return thread;
        }

        private void interruptAll() {
            for (Thread t : list) {
                if (t != null && t.isAlive()) t.interrupt();
            }
        }
    }

    public static void main(String[] args) {
        AsynchronousSocketChannel clientChannel = null;
        AsynchronousChannelGroup group = null;
        Factory factory = null;
        try {
            factory = new Factory(Executors.defaultThreadFactory());
            group = AsynchronousChannelGroup.withFixedThreadPool(1, factory);
//            group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 1);

            clientChannel = AsynchronousSocketChannel.open(group);
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
            attachment.clientThread.join(10000);

        } catch (IOException | IllegalStateException e) {
            System.out.printf("Unable to open AsynchronousSocketChannel:%s%n", e);
        } catch (InterruptedException e) {
            System.out.printf("Client interrupted:%s%n", e);
        } finally {
            IOUtils.close(clientChannel);
        }
        System.out.printf("Client closed%n");
        try {
            System.out.printf("Shutdown group...%n");
            if (group != null) {
                factory.interruptAll();
                group.shutdown();
                if (!group.isTerminated()) {
                    group.shutdownNow();
                    group.awaitTermination(2, TimeUnit.SECONDS);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(group != null) {
            if (group.isTerminated()) {
                System.out.printf("group is shutdown%n");
            } else {
                System.out.printf("group is NOT shutdown%n");
            }
        }
    }
}
