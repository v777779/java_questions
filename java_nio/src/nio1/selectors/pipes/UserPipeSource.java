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

public class UserPipeSource implements Closeable {
    public static final int DEFAULT_DELAY = 500;

    private static final int PIPE_SIZE = 3;
    private static final int RANGE_ID = 128;
    private static final int DEFAULT_TERMINATE = 500;
    private static final int DEFAULT_ID = 1;

    private Pipe mPipe;
    private final int mId;
    private final int mDelay;
    private ExecutorService exec;
    private boolean isActive;


    public UserPipeSource(int id, int delay, Runnable senderPipe) {
        this.mId = id;
        this.mDelay = delay;

        if (id < 0 || id > RANGE_ID) throw new IllegalArgumentException("id shuld be in range 1..128 ");
        try {
            this.mPipe = Pipe.open();
            this.mPipe.sink().configureBlocking(false);
            this.mPipe.source().configureBlocking(false);
            if (senderPipe != null) {
                this.senderPipe = this.senderPipeLimitedEof;   // = -1 after limit
            } else {
                this.senderPipe = this.senderPipeLimited;   // = 0 after limit  подвисает
            }
            this.exec = Executors.newFixedThreadPool(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserPipeSource(int id) {
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

    public UserPipeSource(int id, Runnable senderPipe) {
        this(id, DEFAULT_DELAY, senderPipe);
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
    public Pipe.SinkChannel startPipe() {
        if (senderPipe == null) throw new IllegalArgumentException();

        if (((ThreadPoolExecutor) exec).getActiveCount() == 0) {
            exec.execute(senderPipe);
            setActive(true);
        }
        return mPipe.sink();
    }

    /**
     * Stop Pipe
     * set isActive = false
     * shutdown() then wait 500ms for termination
     *
     * @return termination status
     */
    public boolean stopPipe() {
        if (!isActive() && exec.isTerminated()) {
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

    private Runnable senderPipeHide = () -> {
        int counter = 0;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            Pipe.SinkChannel channel = mPipe.sink();
            while (isActive()) {
                b.clear();
                for (int j = 0; j < PIPE_SIZE; j++) {
                    byte c = (byte) (counter++);
                    b.put(c);
                }
                b.flip();
                while (channel.write(b) > 0) ;                 // пока буфер не будет записан
                Thread.sleep(getDelay());                           // 500ms
            }

        } catch (AsynchronousCloseException e) {
            System.out.print("Asynchronously ");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    };

    private Runnable senderPipe = () -> {
        int counter = 0;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            Pipe.SinkChannel channel = mPipe.sink();
            while (isActive()) {
                b.clear();
                System.out.printf("send id:%03d ", getId());
                for (int j = 0; j < PIPE_SIZE; j++) {
                    byte c = (byte) (counter++);
                    System.out.printf("%03d ", c);
                    b.put(c);
                }
                System.out.printf("%n");
                b.flip();
                while (channel.write(b) > 0) ;                 // пока буфер не будет записан
                Thread.sleep(getDelay());                           // 500ms
            }

        } catch (AsynchronousCloseException e) {
            System.out.print("Asynchronously ");
        } catch (IOException e) {
            System.err.printf("Pipe:%03d unlimited IOException %s%n", getId(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    private Runnable senderPipeLimited = () -> {
        int counter = 1;
        int count = 3;
        Pipe.SinkChannel channel = null;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            channel = mPipe.sink();
            while (isActive() && count > 0) {
                b.clear();
                System.out.printf("send id:%03d ", getId());
                for (int j = 0; j < PIPE_SIZE; j++) {
                    byte c = (byte) (counter++);
                    System.out.printf("%03d ", c);
                    b.put(c);
                }
                System.out.printf("%n");
                b.flip();
                while (channel.write(b) > 0) ;                 // пока буфер не будет записан
                Thread.sleep(getDelay());                           // 500ms
                count--;
            }
        } catch (AsynchronousCloseException e) {
            System.out.print("Asynchronously ");
        } catch (IOException e) {
            System.err.printf("Pipe:%03d limited IOException %s%n", getId(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    private Runnable senderPipeLimitedEof = () -> {
        int counter = 1;
        int count = 3;  // 30 to Exception
        Pipe.SinkChannel channel = null;
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            channel = mPipe.sink();
            while (isActive() && count > 0) {
                b.clear();
                System.out.printf("send id:%03d ", getId());
                for (int j = 0; j < PIPE_SIZE; j++) {
                    byte c = (byte) (counter++);
                    System.out.printf("%03d ", c);
                    b.put(c);
                }
                System.out.printf("%n");
                b.flip();
                while (channel.write(b) > 0) ;                 // пока буфер не будет записан
                Thread.sleep(getDelay());                           // 500ms
                count--;
            }
        } catch (AsynchronousCloseException e) {
            System.out.print("Asynchronously ");
        } catch (IOException e) {
            System.err.printf("Pipe:%03d limited eof IOException %s%n", getId(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeChannel(channel);                  // ATTENTION  channel.close >> readChannel = -1
        }
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

    public static int readPipeChannel(Pipe.SourceChannel channel, int id) {
        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            int len;
            while ((len = channel.read(b)) > 0) {  // non blocking mode
                b.flip();
                System.out.printf("read id:%03d ", id);
                for (int j = 0; j < len; j++) {
                    byte c = b.get();
                    System.out.printf("%03d ", c);
                }
                System.out.printf("%n");
                b.compact();
            }
            b.clear();
            return len;   // -1 if sender is closed
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // channel failed
    }


    private static void readPipeChannel(UserPipeSource pipe, int delay) {
        int count = 1;
        if (delay > DEFAULT_DELAY) count = delay / DEFAULT_DELAY;
        if (pipe == null || pipe.getSource() == null) throw new IllegalArgumentException();

        try {
            ByteBuffer b = ByteBuffer.allocate(PIPE_SIZE);
            Pipe.SourceChannel channel = pipe.getSource();
            int len;
            while (count > 0) {
                if ((len = channel.read(b)) == 0) continue;  // non blocking mode

                b.flip();
                System.out.printf("read id:%03d ", pipe.getId());
                for (int j = 0; j < len; j++) {
                    byte c = b.get();
                    System.out.printf("%03d ", c);
                }
                System.out.printf("%n");
                b.compact();
                count--;
            }
            b.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int id = DEFAULT_ID;
        if (args != null && args.length > 0) {
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.exit(0);
            }
        }

        UserPipeSource pipe = new UserPipeSource(id);
        pipe.startPipe();

        readPipeChannel(pipe, 5000);
        pipe.stopPipe();

    }
}