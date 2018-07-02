package nio1.selectors.threads.worker;

import util.IOUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 01-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ServerAcceptService implements Runnable {
    private static final int SERVER_SOCKET_TIMEOUT = 60000;
    private int port;
    private ServerSocket ssc;
    private boolean isStopped;
    private Thread currentThread;
    private List<Socket> list;


    public ServerAcceptService(String hostName,int port) throws IOException {
        this.port = port;
        this.ssc = javax.net.ServerSocketFactory.getDefault().createServerSocket();
        this.ssc.bind(new InetSocketAddress(hostName,port));
        this.ssc.setSoTimeout(SERVER_SOCKET_TIMEOUT);
        this.isStopped = false;
        this.list = new ArrayList<>();
//        currentThread = Thread.currentThread();
//        System.out.printf("currentThread:%s%n", currentThread.getName());
    }

    synchronized private boolean isStopped() {
        return isStopped;
    }

    synchronized public void stop() {   // close server Socket
        try {
            isStopped = true;
            ssc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ExecutorService exec = Executors.newCachedThreadPool();
        Socket sc = null;
        try {
            while (!isStopped && !Thread.interrupted()) {   // exit by isTopped or shutdownNow()
                sc = ssc.accept();
                if (sc == null) continue;
                list.add(sc);
                exec.execute(new ServerClientService(sc, "Multithreaded Server"));
            }
        } catch (SocketException | SocketTimeoutException e) {
            System.out.printf("server socket exception:%s%n", e);
        } catch (IOException  e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ssc);
            IOUtils.closeSocketList(list);
        }

        exec.shutdown();
        try {
            if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                System.out.printf("can't stop clients service...%n");
            } else {
                System.out.printf("clients service terminated...%n");
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(exec.isTerminated())
        System.out.printf("server accept service closed...%n");
    }
}
