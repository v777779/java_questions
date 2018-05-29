package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.*;

import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 29-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class NIOUtils {
    public static final String[] STRINGS = {
            "Roses are red",
            "Violets are blue",
            "Sugar is sweet",
            "And so are you."
    };


    public static CharBuffer getFilledCharBuffer(String s) {
        CharBuffer cb = CharBuffer.allocate(s.length());
        for (int i = 0; i < s.length(); i++) {
            cb.put(s.charAt(i));
        }
        return cb.flip();
    }

    public static void  readout(ByteBuffer b) {
        b.flip();
        while(b.hasRemaining()) {
            System.out.printf("%03d ", b.get());
        }
        System.out.printf("%n");
    }


    public static char[] readout(CharBuffer cb) {

        if (cb.capacity() == 0) return null;
        cb.position(0);
        char[] chars = new char[cb.limit()];
        for (int i = 0; cb.hasRemaining(); i++) {
            chars[i] = cb.get();
            System.out.printf("%s", chars[i]);
        }
        System.out.printf("%n");
        return chars;
    }

    public static void readout(String s) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(PATH + "result_out.txt"));
            while ((s = br.readLine()) != null) {
                System.out.printf("%s%n", s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            IOUtils.closeStream(br);
        }
    }

    public static void readout(IntBuffer b) {
        b.flip();
        while(b.hasRemaining()) {
            System.out.printf("%d ",b.get());
        }
        System.out.printf("%n");
    }
    public static void readout(LongBuffer b) {
        b.flip();
        while(b.hasRemaining()) {
            System.out.printf("%d ",b.get());
        }
        System.out.printf("%n");
    }

    public static void longToByte(ByteBuffer bByte, LongBuffer bLong) {
        bByte.clear();
        bLong.flip();
        while (bLong.hasRemaining()) {
            bByte.putLong(bLong.get());
        }
    }
    public static void byteToLong(ByteBuffer bByte, LongBuffer bLong) {
        bByte.flip();
        bLong.clear();
        while (bByte.hasRemaining()) {
            bLong.put(bByte.getLong());
        }
    }

    public static void checkMark(CharBuffer cb, boolean isException) {
        if (cb.capacity() == 0) return;
        try {
            int len = cb.capacity();
            cb.position(0);
            cb.position(len / 4).mark();

            if (isException) cb.position(0); // violate mark
            cb.limit(len / 2);  // 25..75%
            while (cb.hasRemaining()) {
                System.out.printf("%c", cb.get());
            }
            System.out.printf("%nreset:%n");
            cb.reset();
            while (cb.hasRemaining()) {
                System.out.printf("%c", cb.get());
            }
            try {
                System.out.printf("%nreset:%n");
                cb.reset();
                for (int i = len / 4; i < len - len / 4; i++) {  // 25%..100%
                    System.out.printf("%c", cb.get());
                }
            } catch (BufferUnderflowException e) {
                System.out.printf("%nException: %s%n", e);
            }

        } catch (InvalidMarkException e) {
            System.out.printf("Exception: %s%n", e);
        }
    }

}
