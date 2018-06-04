package nio;

import nio.socket.DatagramClient;
import nio.socket.MainSocket;
import util.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 31-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03S {

    private static void communicateWithSocket(SocketChannel sc) {

    }

    public static void main(String[] args) {

//SocketChannel

        System.out.printf(FORMAT, "SocketChannel and ServerSocketChannel:");

// independent call
        try {
            Runtime.getRuntime().exec("cmd /c start call java -ea -cp " +
                    "out/production/java_nio nio.socket.MainServerSocket 9998");
//            Runtime.getRuntime().exec("cmd /c start call java -ea -cp " +
//                    "out/production/java_nio nio.socket.MainSocket 9998");
            Thread.sleep(500);
            MainSocket.main(new String[]{"9998"});
            MainSocket.main(new String[]{"9998"});

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        }

// DatagramChannel
        System.out.printf(FORMAT, "Datagram Channel:");
        try {
            Runtime.getRuntime().exec("cmd /c start call java -ea -cp " +
                    "out/production/java_nio nio.socket.DatagramServer 9996 /b");

//            Runtime.getRuntime().exec("cmd /c start call java -ea -cp " +
//                    "out/production/java_nio nio.socket.DatagramClient 9996 MSFT /s /b");
//
            Thread.sleep(500);
            DatagramClient.main(new String[]{"9996", "MSFT", "/s", "/b"});
            DatagramClient.main(new String[]{"9996", "MSBT", "/s", "/b"});

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

// Pipe
        System.out.printf(FORMAT, "Channel:");
        final int PIPE_SIZE = 10;
        final int PIPE_LIMIT = 3;
        final Pipe pipe;
        final Random rnd = new Random();

        try {
            pipe = Pipe.open();
            Runnable senderPipe = () -> {
                try {
                    WritableByteChannel pSinkChannel = pipe.sink();
                    ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
                    for (int i = 0; i < PIPE_LIMIT; i++) {      // 3 раза по 10 значений
                        b.clear();
                        for (int j = 0; j < PIPE_SIZE; j++) {
                            b.put((byte) rnd.nextInt(256));
                        }
                        b.flip();
                        while (pSinkChannel.write(b) > 0) ; // пока буфер не будет записан
                    }
                    pSinkChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.printf("Send pipe closed%n");
            };
            Runnable receiverPipe = () -> {
                try {
                    ReadableByteChannel pSourceChannel = pipe.source();
                    ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
                    Thread.sleep(100);
                    while (pSourceChannel.read(b) >= 0) {
                        b.flip();
                        while (b.hasRemaining()) {
                            System.out.printf("%d ", (byte) (b.get() & 255));
                        }
                        b.clear();  //clear buffer
                    }
                    pSourceChannel.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%nReceiver pipe closed%n");
            };
            ExecutorService exec = Executors.newFixedThreadPool(2);
            exec.execute(senderPipe);
            exec.execute(receiverPipe);

            exec.shutdown();                                            // normal completion
            if (exec.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                System.out.printf("Termination: true%n");
            } else {
                System.out.printf("Termination: false%n");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf(FORMAT, "Channel Question:");
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(PATH+"result.txt").getChannel();
            out = new FileOutputStream(PATH+"result_out.txt").getChannel();
            ByteBuffer b = ByteBuffer.allocate(200);
            while (in.read(b) != -1) {          // получили данные в буфер
                b.flip();                       // отсекаем
                while (b.hasRemaining()) {      // пишем в выходной поток
                    out.write(b);
                }
                b.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in, out);
        }

//        System.out.printf(FORMAT, "Channel:");
//        in = null;
//        out = null;
//        try {
//            in = Channels.newChannel(System.in);
//            out = Channels.newChannel(System.out);
//            ByteBuffer b = ByteBuffer.allocate(200);
//            while (in.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    out.write(b);
//                }
//                b.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in, out);
//        }

    }
}
