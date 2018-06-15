package nio2.sockets;

import util.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserPipe implements Closeable {
    static final int PIPE_LIMIT = 2;
    static final int PIPE_SIZE = 50;
    static final int RANGE_ID = 128;
    private static final int PIPE_DELAY = 500;
    private static final Random rnd = new Random();

    private Pipe mPipe;
    private int mId;
    private int mDelay;
    private int mLimit;
    private Pipe.SinkChannel mPW;
    private Pipe.SourceChannel mPR;
    private ExecutorService exec;

    public UserPipe(int id, int delay) {
        if (id < 0 || id > RANGE_ID) throw new IllegalArgumentException();
        try {
            this.mPipe = Pipe.open();
            this.mId = id;
            this.mPW = mPipe.sink();
            this.mPR = mPipe.source();
            this.mDelay = delay;
            this.mLimit = PIPE_LIMIT;
            exec = Executors.newFixedThreadPool(2);

        } catch (IOException e) {
            e.printStackTrace();
            this.mPipe = null;
            this.mPW = null;
            this.mPR = null;
            this.mId = -1;
            this.mDelay = 0;
        }
    }

    public int getId() {
        return mId;
    }

    public int getDelay() {
        return mDelay;
    }

    public Pipe.SinkChannel getPW() {
        return mPW;
    }

    public Pipe.SourceChannel getPR() {
        return mPR;
    }

    public Pipe.SourceChannel runW() {
        exec.execute(senderPipe);
        return mPR;
    }

    public Pipe.SinkChannel runR() {
        exec.execute(receiverPipe);
        return mPW;
    }


    private final Runnable senderPipe = () -> {
        InputStream in = null;
        BufferedReader br = null;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            in = System.in;
            br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
            while (true) {
                if (in.available() > 0) {
                    int len = in.available();
                    String s = br.readLine();
                    b.clear();
                    b.put(s.getBytes(Charset.defaultCharset()));
                    b.flip();
                    while (mPW.write(b) > 0) ;
                }
                Thread.sleep(mDelay);                 // 1ms
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(mPW, br);
        }
    };

    private final Runnable receiverPipe = () -> {
        int len = -1;
        try {
            ByteBuffer b = ByteBuffer.allocate(UserPipe.PIPE_SIZE);
            while ((len = mPR.read(b)) > 0) {
                b.flip();
                while (b.hasRemaining()) {
                    System.out.printf("rt%03d:%d ", mId, (byte) (b.get() & 255));
                }
                b.clear();
                System.out.printf("%n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            len = -1;
        } finally {
            if (len < 0) {
//                IOUtils.close(pR);
            }
        }
        System.out.printf("rt%03d:receiver pipe closed%n", mId);
    };

    @Override
    public String toString() {
        return "[" + mId + "]";
    }

    @Override
    public void close() throws IOException {
        if (mPW != null && mPW.isOpen()) mPW.close();
        if (mPR != null && mPW.isOpen()) mPR.close();
    }

    public static void main(String[] args) {
        UserPipe pipe = new UserPipe(1, 100);

        pipe.runW();
        pipe.runR();


    }
}