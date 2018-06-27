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
    private static final long TIMEOUT = 5000;

    public static void main(String[] args) {
        Selector selector = null;
        ServerSocketChannel ssc = null;
        UserPipeSource[] pipes = null;

        LocalDateTime finish = LocalDateTime.now().plus(TIMEOUT, ChronoUnit.MILLIS);

        Runnable mockRunnable = () -> {};
        try {
            pipes = new UserPipeSource[]{
                    new UserPipeSource(12, 250, mockRunnable),
                    new UserPipeSource(15),
                    new UserPipeSource(17, mockRunnable),
                    new UserPipeSource(21),
                    new UserPipeSource(25, 100, mockRunnable)
            };

            selector = Selector.open();
            for (UserPipeSource pipe : pipes) {
                Pipe.SourceChannel channel = pipe.getSource();
                channel.configureBlocking(false); // non blocking mode
                SelectionKey key = channel.register(selector, SelectionKey.OP_READ, pipe);
            }
            for (UserPipeSource pipe : pipes) {
                pipe.startPipe();
            }

            while (!LocalDateTime.now().isAfter(finish)) {  // 5 seconds long
                int channels = selector.select(250);
                if (channels == 0) {
                    System.out.print(".");
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    UserPipeSource.Info pipeInfo = ((UserPipeSource) key.attachment()).getInfo();

                    if (key.isAcceptable()) {
                        System.out.printf("Acceptable key %s : %03d", pipeInfo.name, pipeInfo.id);
                    } else if (key.isConnectable()) {
                        System.out.printf("Connectable key %s: %03d", pipeInfo.name, pipeInfo.id);
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
                        System.out.printf("Writable key %s   : %03d", pipeInfo.name, pipeInfo.id);
                    }
                    it.remove();  // remove key thus close channels
                }
            }
            System.out.printf("%n%nShutdown remained pipes...%n");
            for (UserPipeSource pipe : pipes) {
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
