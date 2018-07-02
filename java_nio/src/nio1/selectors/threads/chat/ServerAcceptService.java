package nio1.selectors.threads.chat;

import util.IOUtils;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerAcceptService implements Runnable {
    private static final int DEFAULT_SOCKET_TIMEOUT = 600000;

    private ServerSocket ssc;
    private boolean isStopped;
    private List<ServerClientService> listClients; // ВНИМАНИЕ. BufferedWriter synchronized

    public ServerAcceptService(String hostName, int port) throws IOException {
        this.ssc = new ServerSocket();
        this.ssc.bind(new InetSocketAddress(hostName, port));
//        this.ssc.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);   // timeout inactive
        isStopped = false;

//        this.listSockets = Collections.synchronizedList(new ArrayList<>());
//        this.listOutputs = Collections.synchronizedList(new ArrayList<>());

        this.listClients = new CopyOnWriteArrayList<>();  // добавляет один, читает много одновременно
    }


    synchronized public void closeServer() {
        isStopped = true;
        IOUtils.closeChannel(ssc);
    }

    public boolean isEmpty() {
        return listClients.isEmpty();
    }

    @Override
    public void run() {
        ExecutorService exec = Executors.newCachedThreadPool();
        try {
            System.out.printf("Server  started:%s%n", ssc.getInetAddress());

            try {
                while (!isStopped) {
                    Socket sc = ssc.accept();  // блокирующий метод
                    if (sc == null) continue;

                    System.out.printf("accepted local:%s remote:%s%n",
                            sc.getLocalSocketAddress(), sc.getRemoteSocketAddress());
                    ServerClientService scs = new ServerClientService(sc, listClients);  // synchronized add in constructor
                    exec.execute(scs);
                }
            } catch (SocketException | SocketTimeoutException e) {
                System.out.printf("Exception: %s%n", e);
            }
            for (ServerClientService scs : listClients) {
                scs.closeService();
            }

            exec.shutdown();
            if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                exec.shutdownNow();
                Thread.sleep(100);
            }

        } catch (InterruptedException e) {
            System.out.printf("server accept service interrupted:%s%n", e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(ssc);
        }

        if (!exec.isTerminated()) {
            System.out.printf("can't terminate accept service...%n");
        } else {
            System.out.printf("server accept service terminated...%n");
        }

    }


}