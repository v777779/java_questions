package nio.fileLock;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 30-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainLock {
    private static final int MAX_QUERIES = 1000;
    private static final int MAX_UPDATES = 1000;
    private static final int RECORD_LEN = 16;


    private FileChannel fc;
    private RandomAccessFile raf;
    private ByteBuffer bByte;
    private IntBuffer bInt;
    private int counter;

    public MainLock() {
        this.bByte = ByteBuffer.allocate(RECORD_LEN);
        this.bInt = bByte.asIntBuffer();
        this.counter = 1;
    }

    public static void main(String[] args) {
        new MainLock().run(args.length > 0);
        System.exit(0);
    }

    private void query(FileChannel fc) throws IOException, InterruptedException {
        for (int i = 0; i < MAX_QUERIES; i++) {
            FileLock lock = fc.lock(0, RECORD_LEN, true);
            try {
                bByte.clear();
                fc.read(bByte, 0);
                int a = bInt.get(0);
                int b = bInt.get(1);
                int c = bInt.get(2);
                int d = bInt.get(3);

                System.out.printf("Reading: %d %d %d %d%n", a, b, c, d);
                if (a * 2 != b || a * 3 != c || a * 4 != d) {
                    System.out.printf("Read Error%n");
                    return;
                }
            } finally {
                lock.release();
//                Thread.sleep(0,20); // for 10 apps
            }
        }
    }


    private void update(FileChannel fc) throws IOException {
        for (int i = 0; i < MAX_UPDATES; i++) {

            FileLock lock = fc.lock(0, RECORD_LEN, false);
            try {
                bInt.clear();
                int a = counter;
                int b = counter * 2;
                int c = counter * 3;
                int d = counter * 4;
                System.out.printf("writing: %d %d %d %d%n", a, b, c, d);
                bInt.put(a);
                bInt.put(b);
                bInt.put(c);
                bInt.put(d);
                counter++;
                bByte.clear();              // actually position=0 limit=capacitance()
                fc.write(bByte, 0); // write all the buffer
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.release();
            }
        }
    }


    private void run(boolean isWrite) {
        raf = null;
        fc = null;
        try {
            raf = new RandomAccessFile(PATH + "result_lock.txt", isWrite ? "rw" : "r");
            fc = raf.getChannel();

            if (isWrite) update(fc);
            else query(fc);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fc != null) fc.close();
                if (raf != null) raf.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
