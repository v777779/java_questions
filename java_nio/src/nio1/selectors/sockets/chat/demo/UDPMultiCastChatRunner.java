package nio1.selectors.sockets.chat.demo;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UDPMultiCastChatRunner {
    public static void main(String[] args) {
        try {
            UDPMultiCastChat.run(1,true);
            UDPMultiCastChat.run(2, true);
            UDPMultiCastChat.run(3, false);


        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
