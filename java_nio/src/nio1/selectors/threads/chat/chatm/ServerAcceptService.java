package nio1.selectors.threads.chat.chatm;

import util.IOUtils;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerAcceptService implements Runnable {
//    private static final int DEFAULT_SOCKET_TIMEOUT = 600000;

    private ServerSocket ssc;
    private List<ServerClientService> listClients; // ВНИМАНИЕ. BufferedWriter synchronized

    public ServerAcceptService(String hostName, int port) throws IOException {
        this.ssc = new ServerSocket();
        this.ssc.bind(new InetSocketAddress(hostName, port));
//        this.ssc.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);   // timeout inactive
        this.listClients = new CopyOnWriteArrayList<>();  // добавляет один, читает много одновременно
    }

    public boolean isEmpty() {
        return listClients.isEmpty();
    }

    synchronized public void closeServer() {
        IOUtils.closeChannel(ssc);
    }

    @Override
    public void run() {
        ExecutorService exec = Executors.newCachedThreadPool();
        try {
            System.out.printf("Server  started:%s%n", ssc.getInetAddress());

            try {
                while (true) {
                    Socket sc = ssc.accept();  // блокирующий метод  нет смысла организовывать флаги
                    if (sc == null) continue;
                    System.out.printf("accepted local:%s remote:%s%n",
                            sc.getLocalSocketAddress(), sc.getRemoteSocketAddress());
                    ServerClientService scs = new ServerClientService(sc, listClients);  // synchronized add in constructor
                    exec.execute(scs);
                }
            } catch (SocketException | SocketTimeoutException e) {
                System.out.printf("Exception: %s%n", e);
            }

            exec.shutdown();
            if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                System.out.printf("can't terminate accept service...%n");
            }

        } catch (IOException |InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(ssc);
        }

        if (exec.isTerminated()) {
            System.out.printf("server accept service terminated...%n");
        }

    }


}