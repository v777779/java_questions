package nio1.selectors.pipes;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Set;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 26-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class SelectorPipes {
    private static final long TIMEOUT =10000;

    public static void main(String[] args) {
        Selector selector = null;
        ServerSocketChannel ssc = null;
        UserPipeSource[] pipes = null;
        UserPipeSink[] pipes2 = null;

        LocalDateTime finish = LocalDateTime.now().plus(TIMEOUT, ChronoUnit.MILLIS);

        Runnable mockRunnable = () -> {};
        try {
            pipes = new UserPipeSource[]{
                    new UserPipeSource(12, 250, mockRunnable),  // limited eof
                    new UserPipeSource(15,null),            // limited
                    new UserPipeSource(21),                           // unlimited
                                                                         // blocking
                    new UserPipeSource(32, 250, mockRunnable),
                    new UserPipeSource(45,null),
                    new UserPipeSource(51),

            };

            pipes2 = new UserPipeSink[]{
                    new UserPipeSink(42, 150, mockRunnable),   // limited eof
                    new UserPipeSink(45, 100, null), // limited
                    new UserPipeSink(55),                            // unlimited
                                                                        // blocking
                    new UserPipeSink(62, 150, mockRunnable),
                    new UserPipeSink(65, 100, null),
                    new UserPipeSink(75)
            };

            selector = Selector.open();
            for (UserPipeSource pipe : pipes) {
                if(pipe.getId() > 30) {
                    pipe.getSink().configureBlocking(true);     // blocking other end
                }
                Pipe.SourceChannel channel = pipe.getSource();
                channel.configureBlocking(false);               // non blocking mode
                SelectionKey key = channel.register(selector, SelectionKey.OP_READ, pipe);
            }
            for (UserPipeSource pipe : pipes) {
                pipe.startPipe();
            }

            for (UserPipeSink pipe : pipes2) {
                if(pipe.getId() > 60) {                         // blocking mode other end
                    pipe.getSource().configureBlocking(true);
                }

                Pipe.SinkChannel channel = pipe.getSink();
                channel.configureBlocking(false);                           // non blocking mode
                channel.register(selector, SelectionKey.OP_WRITE, pipe);


            }
            for (UserPipeSink pipe : pipes2) {
                pipe.startPipe();
            }

            while (!LocalDateTime.now().isAfter(finish)) {  // 5 seconds long
                int channels = selector.select(100);
                if (channels == 0) {
                    System.out.print(".");
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    UserPipeInfo pipeInfo;
                    if(key.attachment() instanceof UserPipeSource)
                        pipeInfo = ((UserPipeSource) key.attachment()).getInfo();
                    else{
                        pipeInfo = ((UserPipeSink) key.attachment()).getInfo();
                    }

                    if (key.isAcceptable()) {
                        System.out.printf("Acceptable key %s : %03d%n", pipeInfo.name, pipeInfo.id);
                    } else if (key.isConnectable()) {
                        System.out.printf("Connectable key %s: %03d%n", pipeInfo.name, pipeInfo.id);
                    } else if (key.isReadable()) {
                        int len = UserPipeSource.readPipeChannel((Pipe.SourceChannel) key.channel(), pipeInfo.id);
// закрыть канал и удалить ключи совсем если передающая сторона закрыта
                        if (len < 0) {
                            key.cancel();
                            ((UserPipeSource)key.attachment()).stopPipe(); // close channels
//                            key.interestOps(0);      // remove operation for this channel
//                            key.channel().close();   // close channel
                        }

                    } else if (key.isWritable()) {
                        int len = UserPipeSink.writePipeChannel((Pipe.SinkChannel) key.channel(),
                                pipeInfo.id, pipeInfo.delay);
                        if(len < 0) {
                            key.cancel();
                            ((UserPipeSink)key.attachment()).stopPipe(); // close channels
                        }
                    }
                    it.remove();  // remove key thus close channels
                }
            }
            System.out.printf("%n%nShutdown remained pipes...%n");
            for (UserPipeSource pipe : pipes) {
                pipe.stopPipe();
            }
            for (UserPipeSink pipe : pipes2) {
                pipe.stopPipe();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


//            String classPath = "out/production/java_nio";
//            Runtime.getRuntime()
//                    .exec("cmd /c start call java -cp "+classPath+" nio1.selectors.pipes.UserPipeSource 10");


    }
}
