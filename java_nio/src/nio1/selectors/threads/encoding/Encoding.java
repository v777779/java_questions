package nio1.selectors.threads.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Encoding {


    public static void main(String[] args) throws IOException {
        int n;
        byte[] bytes = new byte[1024];
        System.out.printf("Введите строку (консоль по умолчанию Charset.forName(\"CP866\"): %n");
        InputStream in = System.in;
        int len = in.read(bytes);
        bytes = Arrays.copyOfRange(bytes, 0, len);
        System.out.print("bytes:[");
        for (byte b : bytes) {
            System.out.printf("%03d(0x%02X) ", (int) b & 0xFF, b);
        }
        System.out.println("]");
        String s = new String(bytes, 0, len, Charset.forName("CP866"));
        System.out.printf("%s%n", s);
        s = new String(bytes, 0, len, Charset.forName("WINDOWS-1251"));
        System.out.printf("%s%n", s);
        s = new String(bytes, 0, len, Charset.forName("UTF-8"));
        System.out.printf("%s%n", s);

        System.out.printf("Введите строку Scanner(ADAPTIVE CHARSET)): %n");   // подстраивается под консоль
        String encoding = System.getProperty("console.encoding","cp866");
        if(System.getProperty("file.encoding").equals("UTF-8")) {
            encoding = System.getProperty("console.encoding","utf8");
        }
        System.out.printf("console:%s file:%s%n",encoding,System.getProperty("file.encoding"));

//        ps = new PrintStream(System.out, false, encoding);
        Scanner sc = new Scanner(System.in, encoding);
        System.out.printf("%s%n", sc.nextLine());
        sc.close();



    }

}
