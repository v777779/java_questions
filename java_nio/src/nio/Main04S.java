package nio;

import nio.selector.SelectorSocket;
import nio.selector.UserPipeSelector;

import java.io.IOException;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04S {
    public static void main(String[] args) {

// Selector
        System.out.printf(FORMAT, "Selector UserPipe2:");
        UserPipeSelector.main(new String[] {});



// Selector ServerSocket
        System.out.printf(FORMAT, "Selector ServerSocket:");
        try {
            Runtime.getRuntime().exec("cmd /c start java -ea -cp " +
                    "out/production/java_nio nio.selector.SelectorServerSocket 9994");

//            Runtime.getRuntime().exec("cmd /c start call java -ea -cp " +
//                    "out/production/java_nio nio.selector.SelectorSocket 9994");

            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                SelectorSocket.main(new String[]{"9994"});
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
//            IOUtils.close(in, out);
//        }


    }


}
