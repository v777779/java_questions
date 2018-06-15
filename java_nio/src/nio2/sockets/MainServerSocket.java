package nio2.sockets;

import util.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.Executors;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainServerSocket {
    private static final int PORT = 9090;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        AsynchronousServerSocketChannel serverChannel = null;
        AsynchronousChannelGroup group = null;
        try {

            group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 2);

            serverChannel = AsynchronousServerSocketChannel.open(group);
            serverChannel.bind(new InetSocketAddress(HOST, PORT));
            System.out.printf("Server listening at:%s%n", serverChannel.getLocalAddress());


            AttachmentServer attachment = new AttachmentServer();
            attachment.serverChannel = serverChannel;
            attachment.serverThread = Thread.currentThread();

            serverChannel.accept(attachment, new MyConnHandler());
            System.out.printf("Waiting 10 sec for connections...%n");
            System.out.printf("*********************************%n");
            Thread.currentThread().join(10000);
            Thread.currentThread().join(1000);  // for client

        } catch (IOException e) {
            System.out.printf("Unable to open or bind AsynchronousServerSocketChannel:%s%n", e);
        } catch (InterruptedException e) {
            System.out.printf("Server terminating:%s%n", e);
        } finally {
            IOUtils.close(serverChannel);
        }

        System.out.printf("Server closed%n");

        System.out.printf("Shutdown group...%n");
        if (group != null) {
            group.shutdown();
            while (!group.isShutdown()) {
            }
        }
        System.out.printf("group is shutdown%n");


    }
}
