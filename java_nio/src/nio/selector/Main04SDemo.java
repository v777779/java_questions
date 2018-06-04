package nio.selector;

import util.IOUtils;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04SDemo {
    public static void main(String[] args) {

// Selector
        System.out.printf(FORMAT, "Selectors:");
// pipe test
        UserPipe p = new UserPipe(100, 750);
        UserPipe p2 = new UserPipe(110, 250);
        UserPipe p3 = new UserPipe(60, 400);
        UserPipe p4 = new UserPipe(80, 650);

// demo working
//        p.runW();                           // PipeWrite 100 working    p.PR available
//        p2.runW();                          // PipeWrite 110 working    p2.PR available
//        p.receiverPipe.run();
//        p2.receiverPipe.run();

        Selector selector = null;
        try {
            selector = Selector.open();

            p.runW();                       // write in Thread, read in Selector
            p.getPR().configureBlocking(false);
            p.getPR().register(selector, SelectionKey.OP_READ, p);  // OP_ACCEPT, OP_WRITE not allowed

            p2.runW();
            p2.getPR().configureBlocking(false);
            p2.getPR().register(selector, SelectionKey.OP_READ, p);

            p3.runR();
            p3.getPW().configureBlocking(false);
            p3.getPW().register(selector, SelectionKey.OP_WRITE, p3);

            p4.runR();
            p4.getPW().configureBlocking(false);
            p4.getPW().register(selector, SelectionKey.OP_WRITE, p4
            );
            int counter = 10;
            while (true) {
                int numReadyChannels = selector.select(500);
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
                        System.out.printf("accepted:%s%n", key);
                    } else if (key.isReadable()) {
                        int len;
                        System.out.printf("read:%s%n", key.attachment());
                        Pipe.SourceChannel pR = (Pipe.SourceChannel) key.channel();
                        UserPipe pAttach = (UserPipe) key.attachment();
                        len = UserPipeSelector.readChannel(pR, pAttach);
                        if (len < 0 && pR.isOpen()) {       // len = -1 when Pipe is empty and Send is closed
//                            pR.register(selector,0);      // unregister all for channel ok
                            key.interestOps(0);             // unregister all for key ok
                            pR.close();                     // close channel ok
                        }
                    } else if (key.isWritable()) {
                        System.out.printf("write:%s%n", key.attachment());
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            IOUtils.closeStream(in, out);
            IOUtils.closeStream(selector);
        }


// MappedByteBuffer with CharBuffer
//        System.out.printf(FORMAT, "Channel:");
//        in = null;
//        out = null;
//        try {
//            in = Channels.newChannel(System.in);
//            out = Channels.newChannel(System.out);
//            ByteBuffer b = ByteBuffer.allocate(200);
//            while (in.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    out.write(b);
//                }
//                b.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in, out);
//        }


    }



}
