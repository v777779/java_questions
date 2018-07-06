package java01.inout;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainStream {
    public static void main(String[] args) {
        FileInputStream in = null;          // bytes
        FileOutputStream out = null;

        InputStreamReader inR = null;       // chars
        OutputStreamWriter outW = null;

        try {
// no charset

            in = new FileInputStream("src/java01/inout/files.txt");
            out = new FileOutputStream("src/java01/inout/file_out.txt");
            int a;
            while ((a = in.read()) != -1) {
                out.write(a);
            }
// charset
            inR = new InputStreamReader(
                    new FileInputStream("src/java01/inout/files.txt"),
                    Charset.forName("koi8-r")
            );
            outW = new OutputStreamWriter(
                    new FileOutputStream("src/java01/inout/file_out_c.txt"),
                    Charset.forName("utf8")
            );

            while ((a = inR.read()) != -1) {
                outW.write(a);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();

                if (inR != null) inR.close();
                if (outW != null) outW.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
