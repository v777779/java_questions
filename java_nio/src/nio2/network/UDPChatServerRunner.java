package nio2.network;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class UDPChatServerRunner {
    private static final String HOST = "localhost";
    private static final int PORT = 9990;

    public static void main(String[] args) {
        try {
            UDPChatServer.runPutty(HOST, PORT);
            UDPChatServer.runPutty(HOST, PORT+1);
            UDPChatServer.runTelnet(HOST, PORT + 2);

            UDPChatServer.main(new String[] {"clients"});
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
