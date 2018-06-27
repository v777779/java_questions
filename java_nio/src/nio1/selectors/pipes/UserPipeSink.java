package nio1.selectors.pipes;

import util.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Pipe;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserPipeSink implements Closeable {
    private static final int PIPE_SIZE = 3;
    private static final int RANGE_ID = 128;
    private static final int DEFAULT_DELAY = 500;
    private static final int DEFAULT_TERMINATE = 500;
    private static final int DEFAULT_ID = 1;
    private static int counter;

    private Pipe mPipe;
    private final int mId;
    private final int mDelay;
    private ExecutorService exec;
    private boolean isActive;

    public UserPipeSink(int id, int delay, Runnable receiverPipe) {
        this.mId = id;
        this.mDelay = delay;
        if (id < 0 || id > RANGE_ID) throw new IllegalArgumentException("id shuld be in range 1..128 ");
        try {
            this.mPipe = Pipe.open();
            this.mPipe.sink().configureBlocking(false);
            this.mPipe.source().configureBlocking(false);

            if (receiverPipe == null) {
                this.receiverPipe = this.receiverPipeLimited;
            } else {
                this.receiverPipe = this.receiverPipeLimitedEof;
            }

            this.exec = Executors.newFixedThreadPool(1);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public UserPipeSink(int id) {
        this.mId = id;
        this.mDelay = DEFAULT_DELAY;

        if (id < 0 || id > RANGE_ID) throw new IllegalArgumentException("id shuld be in range 1..128 ");
        try {
            this.mPipe = Pipe.open();
            this.mPipe.sink().configureBlocking(false);
            this.mPipe.source().configureBlocking(false);
            this.exec = Executors.newFixedThreadPool(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pipe.SinkChannel getSink() {
        return mPipe.sink();
    }

    public Pipe.SourceChannel getSource() {
        return mPipe.source();
    }

    public int getId() {
        return mId;
    }

    public int getDelay() {
        return mDelay;
    }

    public UserPipeInfo getInfo() {
        return new UserPipeInfo(this);
    }

    /**
     * Start pipe
     *
     * @return
     */
    public Pipe.SourceChannel startPipe() {
        if (receiverPipe == null) throw new IllegalArgumentException();

        if (((ThreadPoolExecutor) exec).getActiveCount() == 0) {
            exec.execute(receiverPipe);
            setActive(true);
        }
        return mPipe.source();
    }

    /**
     * Stop Pipe
     * set isActive = false
     * shutdown() then wait 500ms for termination
     *
     * @return termination status
     */
    public boolean stopPipe() {
        if (!isActive() && exec.isTerminated()){
            System.out.printf("Pipe:%03d already shutdown%n", mId);
            return true;
        }
        setActive(false);
        exec.shutdown();

        IOUtils.closeChannel(mPipe.source(), mPipe.sink());
        try {
            exec.awaitTermination(DEFAULT_TERMINATE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!mPipe.source().isOpen() && !mPipe.sink().isOpen()) {
            System.out.printf("Closed channels pipe  :%03d%n", mId);
        }
        if (!exec.isTerminated()) {
            System.out.printf("Can't shutdown pipe   :%03d%n", mId);
        } else {
            System.out.printf("Pipe shutdown normally:%03d%n", mId);
        }
        return exec.isTerminated();
    }

    private Runnable receiverPipeHide = () -> {
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            Pipe.SourceChannel channel = mPipe.source();
            int len;
            while (isActive()) {
                if ((len = channel.read(b)) == 0) continue;  // non blocking mode

                b.flip();
                for (int j = 0; j < len; j++) {
                    byte c = b.get();
                }
                b.compact();
            }

        } catch (AsynchronousCloseException e) {
            System.out.print("Asynchronously ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public Runnable receiverPipe = () -> {
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            Pipe.SourceChannel channel = mPipe.source();
            int len;
            while (isActive()) {
                if ((len = channel.read(b)) == 0) continue;  // non blocking mode

                b.flip();
                System.out.printf("read id:%03d ", this.getId());
                for (int j = 0; j < len; j++) {
                    byte c = b.get();
                    System.out.printf("%03d ", c);
                }
                System.out.printf("%n");
                b.compact();
            }

        } catch (AsynchronousCloseException e) {
            System.out.print("Asynchronously ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public Runnable receiverPipeLimited = () -> {
        int count = 3;
        Pipe.SourceChannel channel = null;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            channel = mPipe.source();
            int len;
            while (isActive() && count > 0) {
                if ((len = channel.read(b)) == 0) continue;  // non blocking mode

                b.flip();
                System.out.printf("read id:%03d ", this.getId());
                for (int j = 0; j < len; j++) {
                    byte c = b.get();
                    System.out.printf("%03d ", c);
                }
                System.out.printf("%n");
                b.compact();
                count--;
            }

        } catch (AsynchronousCloseException e) {
            System.out.print("Asynchronously ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Pipe:%03d limited finished%n", getId());
    };

    public Runnable receiverPipeLimitedEof = () -> {
        int count = 3;
        Pipe.SourceChannel channel = null;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            channel = mPipe.source();
            int len;
            while (isActive() && count > 0) {
                if ((len = channel.read(b)) == 0) continue;  // non blocking mode

                b.flip();
                System.out.printf("read id:%03d ", this.getId());
                for (int j = 0; j < len; j++) {
                    byte c = b.get();
                    System.out.printf("%03d ", c);
                }
                System.out.printf("%n");
                b.compact();
                count--;
            }

        } catch (AsynchronousCloseException e) {
            System.out.print(" asynchronously ...");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(channel);
        }
        System.out.printf("Pipe:%03d limited eof finished%n", getId());
    };


    synchronized private void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    synchronized public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "[" + mId + "]";
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeChannel(mPipe.sink(), mPipe.source());
    }

    private static void writePipeChannel(UserPipeSink pipe, int delay) {
        int count = 1;
        if (delay > DEFAULT_DELAY) count = delay / DEFAULT_DELAY;
        if (pipe == null || pipe.getSink() == null) throw new IllegalArgumentException();

        int counter = 0;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            Pipe.SinkChannel channel = pipe.getSink();
            while (count > 0) {
                b.clear();
                System.out.printf("send id:%03d ", pipe.getId());
                for (int j = 0; j < PIPE_SIZE; j++) {
                    byte c = (byte) (counter++);
                    b.put(c);
                    System.out.printf("%03d ", c);
                }
                System.out.printf("%n");
                b.flip();
                while (channel.write(b) > 0) ;                          // пока буфер не будет записан
                Thread.sleep(pipe.getDelay());                          // 500ms
                count--;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int writePipeChannel(Pipe.SinkChannel channel, int id, int delay) {
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            b.clear();
            System.out.printf("send id:%03d ", id);
            for (int j = 0; j < PIPE_SIZE; j++) {
                byte c = (byte) (counter++);
                b.put(c);
                System.out.printf("%03d ", c);
            }
            System.out.printf("%n");
            b.flip();
            int len;
            while ((len = channel.write(b)) > 0) ;                          // пока буфер не будет записан
            Thread.sleep(delay);                          // 500ms

            return len;
        } catch (IOException e) {
            System.err.printf("Pipe:%03d Exception %s%n", id, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) throws InterruptedException {
        int id = DEFAULT_ID;
        if (args != null && args.length > 0) {
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.exit(0);
            }
        }

        UserPipeSink pipe = new UserPipeSink(id);
        pipe.startPipe();

        writePipeChannel(pipe, 5000);
        pipe.stopPipe();


    }
}