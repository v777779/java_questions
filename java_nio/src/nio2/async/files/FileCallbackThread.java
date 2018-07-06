package nio2.async.files;

import util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class FileCallbackThread {
    public static void main(String[] args) {
        System.out.printf(FORMAT, "Asynchronous Channel CompletionHandler<> Thread.interrupt:");
        Path path = Paths.get(".", "data", "nio2");
        Path pathC = Paths.get(path.toString(), "async");
        Path pathD = path.resolve("result_k.txt");

        AsynchronousFileChannel ai = null;
        AsynchronousFileChannel ao = null;
        ByteArrayOutputStream out = null;
// class
        final class MyInt {
            private int value = 0;
            private Thread thread = Thread.currentThread();
        }

        CompletionHandler<Integer, MyInt> cph = new CompletionHandler<>() {
            @Override
            public void completed(Integer result, MyInt myInt) {
                myInt.value = result;
                myInt.thread.interrupt();
            }

            @Override
            public void failed(Throwable exc, MyInt myInt) {
                System.out.printf("Exception: %s%n", exc);
                myInt.value = -1;
                myInt.thread.interrupt();
            }
        };

        try {
            ai = AsynchronousFileChannel.open(pathD, StandardOpenOption.READ);
            out = new ByteArrayOutputStream(50);
            ByteBuffer b = ByteBuffer.allocate(50);

            byte[] bytes = new byte[50];
            int pos = 0;
            int len;
            MyInt attachment = new MyInt();
            while (true) {
                try {
                    ((AsynchronousFileChannel) ai).read(b, pos, attachment, cph);
                    Thread.currentThread().join(); // waiting of interrupt
//                    while (conditionForWait) {
//                        synchronized (lock) {
//                            lock.wait();
//                        }
//                    }
                } catch (InterruptedException e) {
                }

                if (attachment.value == -1) break;
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
    }
}
