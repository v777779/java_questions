package nio2.async.files;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantLock;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FileCallbackLock {
    private static final ReentrantLock lock = new ReentrantLock();
    private static boolean conditionForWait = false;

    synchronized private static void setCondition(boolean isValue) {
        conditionForWait = isValue;
    }

    synchronized private static boolean getCondition() {
        return conditionForWait;
    }

    public static void main(String[] args) {
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
    }
}
