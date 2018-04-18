package java01.inout;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainFile {
    public static void main(String[] args) {
        FileReader in = null;           // extends InputStreamReader
        FileWriter out = null;          // extends OutputStreamWriter

        try {
            in = new FileReader("src/java01/inout/file2.txt");
            out = new FileWriter("src/java01/inout/file2_out.txt");

            int a;
            while ((a = in.read()) != -1) {
                out.write(a);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) in.close();
                if(out != null) out.close();

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
