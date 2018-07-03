package nio1.selectors.threads.encoding;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class EncodingOutput {
    private static final String BOM = "\ufeff";
    private static final String TEST_STRING
            = "ASCII     abcde xyz\n"
            + "German    äöü ÄÖÜ ß\n"
            + "Polish    ąęźżńł\n"
            + "Russian   абвгдеж эюя\n"
            + "CJK       你好\n";

    public static void main(String[] args) throws IOException {
        String[] encodings = new String[]{"UTF-8", "UTF-16LE", "UTF-16BE"};
//        String[] encodings = new String[] {
//                "UTF-8", "UTF-16LE", "UTF-16BE", "UTF-32LE", "UTF-32BE" };

        for (String encoding : encodings) {
            System.out.println("== " + encoding);

            for (boolean writeBom : new Boolean[]{false, true}) {
                System.out.println(writeBom ? "= bom" : "= no bom");
//to output
                String output = (writeBom ? BOM : "") + TEST_STRING;
                byte[] bytes = output.getBytes(encoding);
                System.out.write(bytes);
// to file
                String fileName = "uc-test-" + encoding + (writeBom ? "-bom.txt" : "-nobom.txt");
                FileOutputStream out = new FileOutputStream(fileName);
                out.write(bytes);
                out.close();

                if (encoding.equals("UTF-16BE")) {
                    FileInputStream fr = new FileInputStream(fileName);
                    bytes = new byte[2048];
                    int len = fr.read(bytes);
                    ByteBuffer b = ByteBuffer.allocate(len);
                    b.put(bytes, 0, len);
                    b.flip();
                    CharBuffer cb = b.asCharBuffer();
                    System.out.printf("%nCharBuffer:%n");
                    System.out.println(cb.toString());
                    fr.close();
                }

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(fileName), encoding));
                System.out.printf("BufferedReader Wrong:%n");
                String s;
                int n = 0;
                while ((s = br.readLine()) != null) {
                    System.out.printf("%d:%s%n", n++, s);
                }
                System.out.println();
                br.close();


                br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(fileName), encoding));
                System.out.printf("BufferedReader:%n");
                CharBuffer cb = CharBuffer.allocate(2048);
                br.read(cb);
                cb.flip();
                System.out.printf("%s%n", cb.toString());
                br.close();


                BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(fileName));
                bytes = new byte[2048];
                int len = bIn.read(bytes);
                System.out.printf("BufferedInputStream:%n");
                s = new String(bytes,0,len,encoding);
                System.out.printf("%s%n", s);
                System.out.println();
                br.close();


            }

        }
        System.out.println("================================");
        System.out.printf("UTF8:%n");
        String s = TEST_STRING;
        System.out.printf("%s",s);
        byte[] bytes = s.getBytes("UTF-8");
        s = new String(bytes, "UTF-8");
        System.out.printf("%s",s);

    }

}
