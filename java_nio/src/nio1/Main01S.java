package nio1;

import nio.selector.UserPipeSelector;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 24-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01S {
    public static void main(String[] args) {

// Selector
        System.out.printf(FORMAT, "Selector:");
        UserPipeSelector.main(new String[] {});

        SelectableChannel sc;
        ReadableByteChannel rIn;



// Selector UserServerSocket
//        System.out.printf(FORMAT, "Selector:");
//        try {
//            rIn = Channels.newChannel(System.in);
//
//
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}
