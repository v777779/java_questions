package nio2.links;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainLinksRun {
    public static void main(String[] args) {
        try {
            MainLinks.run2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
