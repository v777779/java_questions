package nio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Map;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main06C {

    private static void encode(String message, Charset charset) {
        System.out.printf("Charset: %s%n", charset);
        System.out.printf("Message: %s%n", message);
        ByteBuffer bb = charset.encode(message);  // encoding to bytes

        for (int i = 0; bb.hasRemaining(); i++) {
            int b = bb.get() & 0xFF;
            char c = (char) b;
            if (Character.isWhitespace(c) || Character.isISOControl(c)) {
                c = '\u0000';
            }
            System.out.printf("%2d: 0x%02X (%c)  ", i + 1, b, c);
            if ((i + 1) % 4 == 0) System.out.printf("%n");

        }
        System.out.printf("%n");
    }

    private static String output(CharBuffer cb) {
        StringBuilder sb = new StringBuilder();
        char[] chars = new char[50];
        int len;

        while ((len = cb.remaining()) > 0) {
            if (len > chars.length) len = chars.length;
            cb.get(chars, 0, len);
            for (int i = 0; i < len; i++) {
                sb.append(String.format("%c ", chars[i]));
            }
        }
        return sb.toString();
    }

    private static String output(ByteBuffer b) {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[50];
        int len;

        while ((len = b.remaining()) > 0) {
            if (len > bytes.length) len = bytes.length;
            b.get(bytes, 0, len);
            for (int i = 0; i < len; i++) {
                sb.append(String.format("0x%02X ", bytes[i]));
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) {

// charsets
        System.out.printf(FORMAT, "Charsets:");
        String[] charsets = {"US-ASCII", "ISO-8859-1", "UTF-8",
                "UTF-16BE", "UTF-16LE", "UTF-16"
        };
        String s = "façade touché";
        for (String charset : charsets) {
            encode(s, Charset.forName(charset));
        }

        System.out.printf(FORMAT, "Checking UTF-8:");
        Charset charset = Charset.forName("UTF-8");

        System.out.printf("C3A7:%c C3A9:%c %n", '\uC3A7', '\uC3A9');
        System.out.printf("C3A7:%s C3A9:%s %n", new String(new byte[]{(byte) 0xC3, (byte) 0xA7}, charset),
                new String(new byte[]{(byte) 0xC3, (byte) 0xA9}, charset));

        System.out.printf("E7:%c E9:%c %n", (char) 0xE7, (char) 0xE9);

        charset = Charset.forName("UTF-16");
        System.out.printf("C3A7:%s C3A9:%s %n", new String(new byte[]{(byte) 0xC3, (byte) 0xA7}, charset),
                new String(new byte[]{(byte) 0xC3, (byte) 0xA9}, charset));

        System.out.printf(FORMAT, "Charset and ByteBuffer, CharBuffer:");
        charset = Charset.forName("UTF-8");

        ByteBuffer bb = charset.encode(s);
        System.out.printf("array: %s%n", output(bb));

        CharBuffer cb = CharBuffer.wrap(s);
        ByteBuffer bbc = charset.encode(cb);
        System.out.printf("array: %s%n", output(bbc));

        System.out.printf(FORMAT, "CharrBuffer to String:");
        bb.rewind();
        CharBuffer cbb = charset.decode(bb);
        System.out.printf("array: %s%n", output(cbb));
        cbb.rewind();
        s = cbb.toString();

        System.out.printf("array: %s%n", cbb);

        System.out.printf(FORMAT, "ByteBuffer asCharBuffer to String:");

        bb = ByteBuffer.allocate(s.length() * 2);
        bb.asCharBuffer().put(s);

        cbb = Charset.forName("UTF-8").decode(bb);
        System.out.printf("UTF8 : %s%n", cbb);

        bb.rewind();
        cbb = Charset.forName("UTF-16").decode(bb);
        System.out.printf("UTF16: %s%n", cbb);

        bb.rewind();
        System.out.printf("UTF16 asCharBuffer: %s%n", bb.asCharBuffer().toString());

        bb.rewind();
        cbb = Charset.defaultCharset().decode(bb);
        System.out.printf("default: %s  charset: %s%n", cbb, Charset.defaultCharset().name());

        System.out.printf(FORMAT, "Charsets Map supported by JVM:");
        Map<String, Charset> map = Charset.availableCharsets();
        int count = 0;
        for (String charsetName : map.keySet()) {
            System.out.printf("%-20s ", charsetName);
            if (((count + 1) % 5 == 0)) System.out.printf("%n");
            count++;
        }

    }

}
