package nio2.questions;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainCh12Runner {
    public static void main(String[] args) {
        try {
            MainCh12.run(",/nio2/result.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
