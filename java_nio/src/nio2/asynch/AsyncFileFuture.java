package nio2.asynch;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class AsyncFileFuture {
    public static void main(String[] args) {
        System.out.printf(FORMAT, "Asynchronous Channel Future<T>:");
        Path path = Paths.get(".", "data", "nio2");
        Path pathC = Paths.get(path.toString(), "async");
        Path pathD = path.resolve("result_k.txt");
        Path pathE = path.resolve("result_w.txt");
        Path pathR;


        BufferedReader br = null;
        FileInputStream in = null;
        AsynchronousChannel ai = null;
        AsynchronousChannel ao = null;

        OpenOption[] options = new OpenOption[]{StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE};
        ByteArrayOutputStream out = null;
        ByteBuffer b = ByteBuffer.allocate(50);
        try {
            ai = AsynchronousFileChannel.open(pathD, options);
            out = new ByteArrayOutputStream(50);
            byte[] bytes = new byte[50];
            int pos = 0;
            int len;
// read Future
            while (true) {
                Future<Integer> result = ((AsynchronousFileChannel) ai).read(b, pos);
                while (!result.isDone()) {
                    Thread.sleep(100);
                    System.out.print(".");
                }
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
// write future
            pathR = pathC.resolve(pathE.getFileName()); // result_w
            ao = AsynchronousFileChannel.open(pathR, options);
            bytes = s.getBytes(Charset.forName("WINDOWS-1251"));
            pos = 0;
            b.clear();

            while (true) {
                len = bytes.length - pos > b.limit() ? b.limit() : bytes.length - pos;
                if(len == 0) break;
                b.put(bytes, pos, len);
                b.flip();
                Future<Integer> result = ((AsynchronousFileChannel) ao).write(b, pos);
                while (!result.isDone()) {  // ожидать результата завершения IO у Future<T>
                    Thread.sleep(10);
                }

                if (result.get() == -1) break;
                pos += len;
                b.compact();
            }


        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ai, ao);
        }
    }
}
