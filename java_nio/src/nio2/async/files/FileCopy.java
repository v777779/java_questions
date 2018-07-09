package nio2.async.files;

import nio2.attributes.MainACL;
import util.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FileCopy {
    private static final Object lock = new Object();
    private static boolean isDone;

    private static class Callback implements CompletionHandler<Integer, ByteBuffer> {
        private final ReentrantLock lock;
        private boolean isDone;
        private int result;
        private AsynchronousFileChannel ao;
        private int pos;
        private boolean isLocked;

        public Callback(AsynchronousFileChannel ao, int pos) {
            this.isDone = false;
            this.result = -1;
            this.ao = ao;
            this.pos = pos;
            this.lock = new ReentrantLock();

        }

        @Override
        public void completed(Integer result, ByteBuffer b) {
            try {
                if (result > 0) {
                    b.flip();
                    isLocked = true;
                    ao.write(b, pos, null, new CompletionHandler<Integer, Void>() {
                        @Override
                        public void completed(Integer result, Void attachment) {
                            synchronized (lock) {
                                lock.notify();
                                isLocked = false;
                            }
                        }

                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            System.out.println("Exception:" + exc);
                        }
                    });
                    while (isLocked) {           // влететь в wait по любому
                        synchronized (lock) {
                            lock.wait();
                        }
                    }
                    set(result);
                } else {
                    set(-1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

        @Override
        public void failed(Throwable exc, ByteBuffer b) {
            System.out.printf("Exception:%s%n", exc);
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
        Path pathC = path.resolve("result_w.txt");
        Path pathD = Paths.get(path.toString(), "async", "result_w_out.txt");

        AsynchronousFileChannel ai = null;
        AsynchronousFileChannel ao = null;

        OpenOption[] options = new OpenOption[]{StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE};
        Set<OpenOption> set = Arrays.stream(options).collect(Collectors.toSet());


        ByteBuffer b = ByteBuffer.allocate(50);
        ThreadFactory f = Executors.defaultThreadFactory();
        AsynchronousChannelGroup group = null;
        ThreadFactory factory = null;
        ExecutorService exec = null;
        try {
// group of Executors or ThreadPool
            factory = new Factory(Executors.defaultThreadFactory());
            exec = Executors.newCachedThreadPool(factory);
            FileAttribute<List<AclEntry>> fileAttr = MainACL.attributes(path);
            ai = AsynchronousFileChannel.open(pathC, set, exec, fileAttr);
            ao = AsynchronousFileChannel.open(pathD, set, exec, fileAttr);

// read Future
            int pos = 0;
            int len;
            while (true) {
                Callback callback = new Callback(ao, pos);
                ai.read(b, pos, b, callback); // вместо null можно object
                while (!callback.isDone()) {                // synchronized method
                    Thread.sleep(100);
                    System.out.print(".");
                }
                b.clear();
                len = callback.get();
                if (len == -1) break;
                pos += len;
            }
            System.out.println();


            exec.shutdown();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ai, ao);
        }
        if (!exec.isTerminated()) {
            System.out.println("can't stop group ThreadPool...");
        }
        System.out.println("Finished...");
    }
}
