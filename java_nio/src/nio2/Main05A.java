package nio2;

import util.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 14-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main05A {
    private static final ReentrantLock lock = new ReentrantLock();
    private static boolean conditionForWait = false;

    synchronized private static void setCondition(boolean isValue) {
        conditionForWait = isValue;
    }

    synchronized private static boolean getCondition() {
        return conditionForWait;
    }

    public static void main(String[] args) {

// future
        System.out.printf(FORMAT, "Asynchronous Channel Future<T>:");
        Path path = Paths.get(".", "data", "nio2");
        Path pathC = Paths.get(path.toString(), "async");
        Path pathD = path.resolve("result_k.txt");
        Path pathE = pathC.resolve("result_k.txt");

        BufferedReader br = null;
        FileInputStream in = null;
        AsynchronousChannel ai = null;
        AsynchronousChannel ao = null;
        OpenOption[] options = new OpenOption[]{StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE};
        ByteArrayOutputStream out = null;
        try {
            ai = AsynchronousFileChannel.open(pathD, StandardOpenOption.READ);
            out = new ByteArrayOutputStream(50);
            ByteBuffer b = ByteBuffer.allocate(50);

            byte[] bytes = new byte[50];
            int pos = 0;
            int len;

            while (true) {
                Future<Integer> result = ((AsynchronousFileChannel) ai).read(b, pos);
                while (!result.isDone()) ;
                if (result.get() == -1) break;
                b.flip();
                while ((len = b.remaining()) > 0) {
                    b.get(bytes, 0, len);
                    pos += len;
                    out.write(bytes, 0, len);
                }
                b.compact();
            }
            String s = out.toString(Charset.forName("KOI8-R"));
            System.out.printf("%s%n", s);

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ai, ao);
        }

// callback
        System.out.printf(FORMAT, "Asynchronous Channel CompletionHandler<> Thread.interrupt:");
        path = Paths.get(".", "data", "nio2");
        pathC = Paths.get(path.toString(), "async");
        pathD = path.resolve("result_k.txt");
        pathE = pathC.resolve("result_k.txt");

        ai = null;
        ao = null;
        options = new OpenOption[]{StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE};
        out = null;
// class
        final class MyInt {
            private int value = 0;
        }
        final MyInt myInt = new MyInt();
        final Thread cThread = Thread.currentThread();

        CompletionHandler<Integer, Void> cph = new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                myInt.value = result;
                cThread.interrupt();
//                synchronized (lock) {
//                    lock.notifyAll();
//                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.printf("Exception: %s%n", exc);
                myInt.value = -1;
                cThread.interrupt();
//                synchronized (lock) {
//                    lock.notifyAll();
//                }
            }
        };

        try {
            ai = AsynchronousFileChannel.open(pathD, StandardOpenOption.READ);
            out = new ByteArrayOutputStream(50);
            ByteBuffer b = ByteBuffer.allocate(50);


            byte[] bytes = new byte[50];
            int pos = 0;
            int len;

            while (true) {
                try {
                    ((AsynchronousFileChannel) ai).read(b, pos, null, cph);
                    Thread.currentThread().join(); // waiting of interrupt
//                    while (conditionForWait) {
//                        synchronized (lock) {
//                            lock.wait();
//                        }
//                    }
                } catch (InterruptedException e) {
                }

                if (myInt.value == -1) break;
                b.flip();
                while ((len = b.remaining()) > 0) {
                    b.get(bytes, 0, len);
                    pos += len;
                    out.write(bytes, 0, len);
                }
                b.compact();
            }
            String s = out.toString(Charset.forName("KOI8-R"));
            System.out.printf("%s%n", s);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ai, ao);
        }


// callback wait
        System.out.printf(FORMAT, "Asynchronous Channel CompletionHandler<> ReentrantLock:");
        path = Paths.get(".", "data", "nio2");
        pathC = Paths.get(path.toString(), "async");
        pathD = path.resolve("result_k.txt");
        pathE = pathC.resolve("result_k.txt");

        ai = null;
        ao = null;
        options = new OpenOption[]{StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE};
        out = null;
// class
        final class MutableInt {
            private int value = 0;
        }
        final MutableInt mutableInt = new MutableInt();
        final Thread currentThread = Thread.currentThread();

        CompletionHandler<Integer, Void> completionHandler = new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                mutableInt.value = result;
                synchronized (lock) {
                    setCondition(false);
                    lock.notifyAll();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.printf("Exception: %s%n", exc);
                mutableInt.value = -1;
                synchronized (lock) {
                    setCondition(false);
                    lock.notifyAll();
                }
            }
        };

        try {
            ai = AsynchronousFileChannel.open(pathD, StandardOpenOption.READ);
            out = new ByteArrayOutputStream(50);
            ByteBuffer b = ByteBuffer.allocate(50);

            byte[] bytes = new byte[50];
            int pos = 0;
            int len;
            while (true) {
//                setCondition(true);
                ((AsynchronousFileChannel) ai).read(b, pos, null, completionHandler);
//                while (getCondition()) {
                synchronized (lock) {
                    lock.wait();
//                    }
                }

                if (mutableInt.value == -1) break;
                b.flip();
                while ((len = b.remaining()) > 0) {
                    b.get(bytes, 0, len);
                    pos += len;
                    out.write(bytes, 0, len);
                }
                b.compact();
            }
            String s = out.toString(Charset.forName("KOI8-R"));
            System.out.printf("%s%n", s);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ai, ao);
        }

        System.out.printf(FORMAT, "AsynchronousServerSocketChannel:");
        try {
            System.out.printf("Type message in Client Window within 10 sec%n");
            Runtime.getRuntime()
                    .exec("cmd /c start java -cp out/production/java_nio nio2.sockets_CHECK_THIS.MainServerSocket");
            Thread.sleep(500);
            Runtime.getRuntime()
                    .exec("cmd /c start java -cp out/production/java_nio nio2.sockets_CHECK_THIS.MainClientSocket");
//            MainServerSocket.main(args);
//            MainClientSocket.main(args);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf(FORMAT, "Asynchronous Channel Group:");
        path = Paths.get(".", "data", "nio2", "result.txt");
        br = null;
        in = null;
        AsynchronousChannelGroup group = null;
        try {
//            group  = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(),1);
//            AsynchronousChannelGroup.withFixedThreadPool(20,Executors.defaultThreadFactory());
//            AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());


            in = new FileInputStream(path.toString());
            br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));



        } catch (IOException  e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br, in);
        }

//        System.out.printf(FORMAT, "Asynchronous Channel:");
//        Path path = Paths.get(".", "data", "nio2", "result.txt");
//        BufferedReader br = null;
//        FileInputStream in = null;
//        try {
//            in = new FileInputStream(path.toString());
//            br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(br, in);
//        }

    }
}
