package java01.inout;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainStd {
    public static void main(String[] args) {
        InputStreamReader in = null;

        try {
            in = new InputStreamReader(System.in);
            System.out.println("Введите символы, символ 'q' для выхода.");
            char a;
            do {
                a = (char) in.read();
                if(a == 'e') System.err.println("Error stream activated");
                else System.out.print(a);

            } while(a != 'q');

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
