package nio2.async.files;

import nio2.attributes.MainACL;
import util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FileCallback {
    private static final Object lock = new Object();
    private static boolean isDone;

    private static class Callback implements CompletionHandler<Integer, ByteBuffer> {
        private boolean isDone;
        private int result;
        private ByteArrayOutputStream bOut;

        synchronized public boolean isDone() {
            return isDone;
        }

        synchronized public int get() {
            return result;
        }

        synchronized public void set(int result) {
            this.result = result;
            this.isDone = true;
        }

        public Callback(ByteArrayOutputStream bOut) {
            this.isDone = false;
            this.result = -1;
            this.bOut = bOut;
        }

        @Override
        public void completed(Integer result, ByteBuffer b) {
            int len;
            if (result > 0) {
                b.flip();
                bOut.write(b.array(), 0, b.limit());
                set(result);
            } else set(-1);
        }

        @Override
        public void failed(Throwable exc, ByteBuffer b) {
            System.out.printf("Exception:%s%n", exc);
        }
    }

    private static class InternalInt {
        private int value = -1;
        private boolean isDone = false;

        private void set(int value) {
            synchronized (lock) {
                isDone = true;
                this.value = value;
            }
        }

        private int get() {
            synchronized (lock) {
                return value;
            }
        }

        private boolean isDone() {
            synchronized (lock) {
                return isDone;
            }
        }
    }
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
        System.out.printf(FORMAT, "Asynchronous Channel Future<T>:");
        Path path = Paths.get(".", "data", "nio2");
        Path pathC = Paths.get(path.toString(), "async");
        Path pathD = path.resolve("result_w.txt");
        Path pathE = path.resolve("result_k.txt");
        Path pathR;

        AsynchronousChannel ai = null;
        AsynchronousChannel ao = null;

        OpenOption[] options = new OpenOption[]{StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE};
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteBuffer b = ByteBuffer.allocate(50);
        ThreadFactory f = Executors.defaultThreadFactory();
        AsynchronousChannelGroup group = null;
        ThreadFactory factory = null;
        try {
// group of Executors or ThreadPool
            factory = new Factory(Executors.defaultThreadFactory());
            ExecutorService exec = Executors.newCachedThreadPool(factory);
            FileAttribute<List<AclEntry>> fileAttr = MainACL.attributes(path);
            ai = AsynchronousFileChannel.open(pathD, new HashSet<>(),exec);
            byteArrayOutputStream = new ByteArrayOutputStream(50); // init size
// read Future
            int pos = 0;
            int len;
            while (true) {
                Callback callback = new Callback(byteArrayOutputStream);
                b.clear();
                ((AsynchronousFileChannel) ai).read(b, pos, b, callback);  // вместо null можно любой канал
                while (!callback.isDone()) {  // synchronized method
                    Thread.sleep(100);
                    System.out.print(".");
                }
                len = callback.get();
                if (len == -1) break;
                pos += len;
            }
            System.out.println();
            String s = byteArrayOutputStream.toString(Charset.forName("WINDOWS-1251"));
            System.out.printf("%s%n", s);
// write future
            pathR = pathC.resolve(pathE.getFileName()); // result_w
            ao = AsynchronousFileChannel.open(pathR, options);
            ByteBuffer c = ByteBuffer.wrap(s.getBytes(Charset.forName("KOI8-R")));
// class for sync
            pos = 0;
            while (true) {
                b.clear();
                c.limit(c.capacity());
                if (c.remaining() == 0) break;
                len = c.remaining() > b.limit() ? b.limit() : c.remaining();
                c.limit(pos + len);
                b.put(c);
                b.flip();
                final InternalInt iInt = new InternalInt();
                ((AsynchronousFileChannel) ao).write(b, pos, null, new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                        iInt.set(result);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        System.out.printf("Exception:%s%n", exc);
                        iInt.set(-1);
                    }
                });
                while (!iInt.isDone()) {  // ожидать результата завершения IO у Future<T>
                    System.out.print(".");
                    Thread.sleep(10);
                }
                if (iInt.get() == -1) break; // synchronized method
                pos = c.position();
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ai, ao);
        }
    }
}
