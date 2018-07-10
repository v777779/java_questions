package nio2.network;

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
            UDPChatMultiCastClient.run(1,true);
            UDPChatMultiCastClient.run(2, true);
            UDPChatMultiCastClient.run(3, false);


        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
