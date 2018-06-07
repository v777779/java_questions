package nio.selector;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class UserPipe implements Closeable {
    static final int PIPE_LIMIT = 2;
    static final int PIPE_SIZE = 3;
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
            exec = Executors.newFixedThreadPool(1);

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


    public int decLimit() {
        if (mLimit == 0) return -1;
        mLimit--;
        return mLimit;
    }

    public Pipe.SourceChannel runW() {
        if (((ThreadPoolExecutor) exec).getActiveCount() > 0) return null;
        exec.execute(senderPipe);
        exec.shutdown();
        return mPR;
    }

    public Pipe.SinkChannel runR() {
        if (((ThreadPoolExecutor) exec).getActiveCount() > 0) return null;
        exec.execute(receiverPipe);
        exec.shutdown();
        return mPW;
    }


    private final Runnable senderPipe = () -> {
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            for (int i = 0; i < PIPE_LIMIT; i++) {      // 10 раз по 3 значения
                b.clear();
                for (int j = 0; j < PIPE_SIZE; j++) {
                    byte c = (byte) (rnd.nextInt(RANGE_ID) + mId);
                    b.put(c);
                    System.out.printf("wt%03d:%d ", mId, c);
                }
                System.out.printf("%n");
                b.flip();
                while (mPW.write(b) > 0) ;               // пока буфер не будет записан
                Thread.sleep(mDelay);                 // 500ms
            }
            mPW.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("wt%03d:send pipe closed%n", mId);
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
}