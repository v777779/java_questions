package nio1.selectors.threads.chat;

import util.IOUtils;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 27-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ClientService {
    private static final int DEFAULT_PORT = 9990;
    private static final long SESSION_LENGTH = 500000;
    private static final long RESPONSE_LENGTH = 60000;

    private static final String MARKER_CONNECT = "connect";
    private static final String MARKER_DISCONNECT = "disconnect";
    private static final String MARKER_CHAT = "chat";
    private static final int INDEX_MODE = 0;
    private static final int INDEX_USER = 1;
    private static final int INDEX_MESSAGE = 2;

    private static final Charset TELNET_CHARSET = Charset.forName("CP866");
    private static final Charset INPUT_CHARSET = Charset.forName("UTF-8");

    private static final String[] USER_NAMES = {
            "Mike", "Steve", "John", "Joshua", "Jim",
            "Bob", "Dan", "Duke", "Clive", "Tim", "Tom",
            "Willey"
    };
    private static final Random rnd = new Random();

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.printf("Use default port:%d", port);
            }
        }

// Client
        Socket sc = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        ClientInput ci = null;

        String userName = USER_NAMES[rnd.nextInt(USER_NAMES.length)];
        StringBuilder sb = new StringBuilder();

        ExecutorService exec = Executors.newCachedThreadPool();
        boolean isStopped = false;

        try {
            sc = new Socket();
            InetSocketAddress address = new InetSocketAddress("localhost", port);
            sc.connect(address);
            br = new BufferedReader(new InputStreamReader(sc.getInputStream(), TELNET_CHARSET));
            bw = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream(), TELNET_CHARSET));

            Charset inputCharset = TELNET_CHARSET;
            if(System.getProperty("file.encoding").equals("UTF-8")) {  // running from IDEA
                inputCharset = INPUT_CHARSET;
            }
            ci = new ClientInput(inputCharset);

            exec.execute(ci);

// send connect
            String s = String.format("%s:%s::%n", MARKER_CONNECT, userName);
            bw.write(s);
            bw.flush();
            sb.setLength(0);

// wait 60sec for response w/o selectors
            long count = RESPONSE_LENGTH / 100;
            while (!br.ready()) {
                if (count <= 0) {
                    System.out.printf("server does not respond, exiting...%n");
                    return;
                }
                count--;
                Thread.sleep(100);
            }
// main loop
            while (!isStopped) {
                sb.setLength(0);
                while (br.ready() && (s = br.readLine()) != null) {
                    sb.append(s).append(String.format("%n"));
                }
                if (sb.toString().length() > 0) {
                    System.out.printf("%s", sb.toString());
                }

                if (!(s = ci.getMessage()).isEmpty()) {
                    String message = String.format("%s:%s:%s:%n", MARKER_CHAT, userName, s);
                    if (s.equals(MARKER_DISCONNECT)) {
                        message = String.format("%s:%s::%n", MARKER_DISCONNECT, userName);
                        isStopped = true;
                    }
                    bw.write(message);
                    bw.flush();
                }
                Thread.sleep(10);
            }
        } catch (ConnectException e) {
            System.out.printf("can't connect to server:%s%n", e);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(sc);
        }

// shutdown
        exec.shutdown();            // ordered shutdown after thread exit
        try {
            if (!exec.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                System.out.printf("can't stop client service...%n");
                exec.shutdownNow();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if (exec.isTerminated()) {
            System.out.printf("client service terminated%n");
        }

    }


    private static class ClientInput implements Runnable {
        private BufferedReader br;
        private String message;


        public ClientInput(Charset charset) {
            this.br = new BufferedReader(new InputStreamReader(System.in, charset));
            this.message = "";
        }

        public String getMessage() {  // read one time only
            String s = message;
            message = "";
            return s;
        }

        @Override
        public void run() {
            String s;
            try {
                while (!Thread.interrupted()) {
                    if ((s = br.readLine()) != null) {
                        message = s;
                        if (s.equals(MARKER_DISCONNECT)) {
                            break;
                        }
                    }
                }
// waiting for transfer to client service
                while (!Thread.interrupted() && !message.isEmpty()) {
                    Thread.sleep(100);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("client input thread closed...%n");
        }

    }

//            ClientPipeOut cpOut = new ClientPipeOut();
//            ClientPipeIn cpIn = new ClientPipeIn(cpOut.getPipeOut());
//            exec.execute(cpOut);
//            exec.execute(cpIn);

//    private static class ClientPipeIn implements Runnable {
//        private PipedInputStream pipeIn;
//        private BufferedReader br;
//
//        public ClientPipeIn(PipedOutputStream pipeOut) throws IOException {
//            pipeIn = new PipedInputStream(pipeOut);
//            br = new BufferedReader(new InputStreamReader(pipeIn, Charset.defaultCharset()));
//        }
//
//        @Override
//        public void run() {
//            try {
//                String s;
//                while (!Thread.interrupted()) {
//                    if ((s = br.readLine()) != null) {
//                        System.out.printf("%s%n", s);
//                    }
//                    Thread.sleep(1);
//                }
//            } catch (IOException | InterruptedException e) {
//                System.out.printf("Exception:%s%n", e);
//            }
//            System.out.printf("PipeIn closed...%n");
//        }
//    }
//
//    private static class ClientPipeOut implements Runnable {
//        private PipedOutputStream pipeOut;
//
//        public ClientPipeOut() throws IOException {
//            pipeOut = new PipedOutputStream();
//        }
//
//        public PipedOutputStream getPipeOut() {
//            return pipeOut;
//        }
//
//        @Override
//        public void run() {
//            try {
//                while (!Thread.interrupted()) {
//                    pipeOut.write(System.in.read());
//                    Thread.sleep(1);
//                }
//            } catch (IOException | InterruptedException e) {
//                System.out.printf("Exception:%s%n", e);
//            }
//            System.out.printf("PipeOut closed...%n");
//        }
//    }
}
