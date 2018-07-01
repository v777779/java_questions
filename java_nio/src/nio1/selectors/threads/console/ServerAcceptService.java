package nio1.selectors.threads.console;

import nio1.selectors.threads.message.UserServerSocket;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerAcceptService implements Runnable {
    private ServerSocket ssc;
    private BufferedReader br;
    private BufferedWriter bw;
    private boolean isStopped;
    private List<Socket> list;


    public ServerAcceptService(String hostName, int port, int timeout) throws IOException {
        this.ssc = new ServerSocket();
        this.ssc.bind(new InetSocketAddress(hostName, port));
        this.ssc.setSoTimeout(timeout);
        isStopped = false;
        this.list = new ArrayList<>();
    }

    synchronized public boolean isStopped() {
        return isStopped;
    }

    synchronized public void setStopped() {
        isStopped = true;
    }

    synchronized public void closeServer() {
        isStopped = true;
        IOUtils.closeChannel(ssc);
    }

    synchronized private void addSocket(Socket sc) {
        if (sc != null) {
            list.add(sc);
        }
    }

    synchronized public int isClients() {
        int count  = 0;
        for (Socket socket : list) {
            if (!socket.isClosed()) count++;  // isConnected always return true DO NOT USE
        }
        return count;
    }

    @Override
    public void run() {
        ExecutorService exec = Executors.newFixedThreadPool(UserServerSocket.SOCKETS_NUMBER);
        try {
            System.out.printf("Server  started  local:%s%n", ssc.getInetAddress());

            try {
                while (!isStopped) {
                    Socket sc = ssc.accept();
                    if (sc == null) {
                        System.out.print(".");
                        Thread.sleep(500);
                        continue;
                    }

                    System.out.printf("%naccepted local:%s remote:%s%n",
                            sc.getLocalSocketAddress(), sc.getRemoteSocketAddress());
                    list.add(sc);
                    exec.execute(new ServerClientService(sc));
                }
            } catch (SocketException | SocketTimeoutException e) {
                System.out.printf("%nException: %s", e);
            }

            exec.shutdown();
            System.out.printf("%nServer waiting for clients service...%n");
            int count = 5;
            while (count > 0) {
                if (!exec.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    System.out.print("*");
                }
                count--;
            }
            while (!exec.isTerminated()) {
                System.out.printf("%ncan't terminate clients service...%n");
                System.out.printf("make shutdownNow()...%n");
                exec.shutdownNow();

                Thread.sleep(500);
            }
            if (exec.isTerminated()) {
                System.out.printf("%nclients service terminated normally...%n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(ssc);
        }

        System.out.printf("server accept service closed%n");

    }
}