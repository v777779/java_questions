package nio.selector;

import util.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UserPipeSelector {

    public static void main(String[] args) {
        UserPipe[] pipes = {
                new UserPipe(100, 750),
                new UserPipe(110, 250),
                new UserPipe(60, 400),
                new UserPipe(80, 650)};

        Selector selector = null;
        try {
            selector = Selector.open();
            UserPipeSelector.registerReadChannels(selector, pipes, 0, 2);
            UserPipeSelector.registerWriteChannels(selector, pipes, 2, 4);

            int counter = 5;
            while (true) {
                int numReadyChannels = selector.select(250);
                if (numReadyChannels == 0) {
                    System.out.print(".");
                    if (counter-- > 0) {
                        continue;
                    } else {
                        break;
                    }
                }
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> it = set.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        System.out.printf("accepted:%s%n", key.attachment());
                    } else if (key.isReadable()) {
                        int len;
//                        System.out.printf("read:%s%n", key.attachment());
                        Pipe.SourceChannel pR = (Pipe.SourceChannel) key.channel();
                        UserPipe pAttach = (UserPipe) key.attachment();
                        len = UserPipeSelector.readChannel(pR, pAttach);
                        if (len < 0 && pR.isOpen()) {       // len = -1 when Pipe is empty and Send is closed
//                            pR.register(selector,0);      // unregister all for channel ok
                            key.interestOps(0);             // unregister all for key ok
                            pR.close();                     // close channel ok
                        }
                    } else if (key.isWritable()) {
//                        System.out.printf("write:%s%n", key.attachment());
                        Pipe.SinkChannel pW = (Pipe.SinkChannel) key.channel();
                        UserPipe pAttach = (UserPipe) key.attachment();
                        int len = UserPipeSelector.writeChannel(pW, pAttach);
                        if (len < 0) {
//                            pR.register(selector,0);
                            key.interestOps(0);
                            pW.close();
                        }
                    }
                    it.remove(); // remove key
                }
            }
            System.out.printf("%nSelector finished%n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(selector);
            IOUtils.close(pipes);
        }

    }

    public static void registerReadChannel(Selector selector, UserPipe p) throws IOException {
        p.runW();                       // write in Thread, read in Selector
        p.getPR().configureBlocking(false);
        p.getPR().register(selector, SelectionKey.OP_READ, p);  // OP_ACCEPT, OP_WRITE not allowed
    }

    public static void registerReadChannels(Selector selector, UserPipe[] pipes,
                                            int start, int end) throws IOException {
        if (selector == null || pipes == null ||
                pipes.length < end ) throw new IllegalArgumentException();
        for (int i = start; i < end; i++) {
            registerReadChannel(selector, pipes[i]);
        }
    }

    public static void registerWriteChannel(Selector selector, UserPipe p) throws IOException {
        p.runR();                       // write in Thread, read in Selector
        p.getPW().configureBlocking(false);
        p.getPW().register(selector, SelectionKey.OP_WRITE, p);  // OP_ACCEPT, OP_READ not allowed
    }

    public static void registerWriteChannels(Selector selector, UserPipe[] pipes,
                                             int start, int end) throws IOException {
        if (selector == null || pipes == null ||
                pipes.length < end) throw new IllegalArgumentException();
        for (int i = start; i < end; i++) {
            registerWriteChannel(selector, pipes[i]);
        }
    }

    public static int writeChannel(Pipe.SinkChannel pW, UserPipe p) {
        final Random rnd = new Random();
        int len = -1;
        try {
            ByteBuffer b = ByteBuffer.allocate(UserPipe.PIPE_SIZE);
            if ((len = p.decLimit()) < 0) return len;
            b.clear();
            for (int j = 0; j < UserPipe.PIPE_SIZE; j++) {
                byte c = (byte) (rnd.nextInt(UserPipe.RANGE_ID) + p.getId());
                b.put(c);
                System.out.printf("ws%03d:%d ", p.getId(), c);
            }
            System.out.printf("%n");
            b.flip();
            while (pW.write(b) > 0) ;               // пока буфер не будет записан
            Thread.sleep(p.getDelay());                 // 500ms

            return len;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            len = -1;
            return len;
        } finally {
            if (len < 0) {
//                IOUtils.close(pW);
            }
        }
    }

    public static int readChannel(Pipe.SourceChannel pR, UserPipe p) {
        int len = -1;
        try {
            ByteBuffer b = ByteBuffer.allocate(UserPipe.PIPE_SIZE);
            Thread.sleep(100);
            while ((len = pR.read(b)) > 0) {
                b.flip();
                while (b.hasRemaining()) {
                    System.out.printf("rs%03d:%d ", p.getId(), (byte) (b.get() & 255));
                }
                b.clear();  //clear buffer
                System.out.printf("%n");
            }
            return len;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            len = -1;
            return len;
        } finally {
            if (len < 0) {
//                IOUtils.close(pR);
            }
        }
    }

}
