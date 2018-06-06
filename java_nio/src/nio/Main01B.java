package nio;

import util.IOUtils;
import util.NIOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01B {

    public static void main(String[] args) {
        System.out.printf(FORMAT, "NIO Stream:");


// DecoratorReader
        System.out.printf(FORMAT, "DecoratorReader:");
        ByteBuffer b;
        b = ByteBuffer.allocate(50);
        System.out.println("capacity :" + b.capacity());
        System.out.println("limit    :" + b.limit());
        System.out.println("position :" + b.position());
        System.out.println("remaining:" + b.remaining());
        System.out.println("capacity:" + b.capacity());

        System.out.println("changing limit 5:");
        b.limit(5);
        System.out.println("limit    :" + b.limit());
        System.out.println("position :" + b.position());
        System.out.println("remaining:" + b.remaining());

        System.out.println("changing position 3:");
        b.position(3);
        System.out.println("limit    :" + b.limit());
        System.out.println("position :" + b.position());
        System.out.println("remaining:" + b.remaining());

        System.out.println(b);

        System.out.printf(FORMAT, "ByteBuffer duplicate():");
        b = ByteBuffer.wrap(IOUtils.STRING2.getBytes(), 10, 50);
        ByteBuffer c = b.duplicate();
        c.position(0);
        c.limit(25);
        System.out.println("limit    :" + b.limit() + " c:" + c.limit());
        System.out.println("position :" + b.position() + " c:" + c.position());
        System.out.println("remaining:" + b.remaining() + " c:" + c.remaining());

        while (b.hasRemaining()) {
            System.out.printf("%c", (char) b.get());
        }
        System.out.println();
        while (c.hasRemaining()) {
            System.out.printf("%c", (char) c.get());
        }
        System.out.println();

        System.out.println("b: " + new String(b.array(), Charset.forName("UTF-8")));
        System.out.println("c: " + new String(c.array(), Charset.forName("UTF-8")));


        System.out.printf(FORMAT, "ByteBuffer read() write():");
        b = ByteBuffer.allocate(10);
        System.out.println("b:" + Arrays.toString(b.array()));
        b.put((byte) 10);
        b.put((byte) 15);
        b.put(5, (byte) 20);
        b.position(7);
        b.put((byte) 30);
        System.out.println("b:" + Arrays.toString(b.array()));
        System.out.println("limit    :" + b.limit());
        System.out.println("position :" + b.position());
        System.out.println("remaining:" + b.remaining());

        System.out.printf(FORMAT, "ByteBuffer bulk:");
        c = ByteBuffer.allocate(10);
        System.out.println("b:" + Arrays.toString(b.array()));
        System.out.println("c:" + Arrays.toString(c.array()));
        System.out.printf("position : %02d %02d%n", b.position(), c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());

        System.out.println("c.put(b.array(),0,5):");
        c.put(b.array(), 0, 5);
        System.out.printf("position : %02d %02d%n", b.position(), c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());

        System.out.println("c.put(b.array(),0,5):");
        c.put(b.array(), 0, 5);
        System.out.printf("position : %02d %02d%n", b.position(), c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());

        System.out.println("c:" + Arrays.toString(c.array()));
        try {
            c.put(b);
        } catch (BufferOverflowException e) {
            System.out.println("Buffer Exception:" + e);
        }
        b.position(0);
        c.position(0);
        System.out.printf("position : %02d %02d%n", b.position(), c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());
        System.out.println("c.put(b):");
        c.put(b);
        System.out.printf("position : %02d %02d%n", b.position(), c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());

        c.put(b);
        System.out.println("b:" + Arrays.toString(b.array()));
        System.out.println("c:" + Arrays.toString(c.array()));

        System.out.printf(FORMAT, "ByteBuffer flip:");
        b = ByteBuffer.allocate(10);
        System.out.printf("position : %02d%n", b.position());
        System.out.printf("limit    : %02d%n", b.limit());
        b.put((byte) 10);
        b.put((byte) 20);
        b.put((byte) 30);
        System.out.println("b.flip()");
        b.flip();
        System.out.printf("position : %02d%n", b.position());
        System.out.printf("limit    : %02d%n", b.limit());
        while (b.hasRemaining()) {
            System.out.printf("%02d ", b.get());
        }

        System.out.println();

        System.out.printf(FORMAT, "flip() with strings:");

        CharBuffer buffer = CharBuffer.allocate(50);
        for (int i = 0; i < NIOUtils.STRINGS.length; i++) {
            for (int j = 0; j < NIOUtils.STRINGS[i].length(); j++) {
                buffer.put(NIOUtils.STRINGS[i].charAt(j));
            }
            buffer.flip();
            System.out.printf("buffer: %02d ", buffer.limit());
            while (buffer.hasRemaining()) {
                System.out.print(buffer.get());
            }
            buffer.clear();
            System.out.println(); // next line
        }

        System.out.printf(FORMAT, "mark() with strings:");
        String s = Arrays.stream(NIOUtils.STRINGS).reduce("", (s1, s2) -> s1 + s2);
        int len = s.length();
        CharBuffer cb = CharBuffer.wrap(s);

        System.out.printf(FORMAT, "mark() false:");
        NIOUtils.checkMark(cb, false);
        System.out.printf(FORMAT, "mark() true:");
        NIOUtils.checkMark(cb, true);

// compact
        System.out.printf(FORMAT, "ByteBuffer compact():");
        FileChannel in;
        FileChannel out;
        try {
            ByteBuffer bb = ByteBuffer.allocate(100);
            in = new FileInputStream(PATH + "result.txt").getChannel();
            out = new FileOutputStream(PATH + "result_out.txt").getChannel();
            while ((in.read(bb)) != -1) {
                bb.flip();
                System.out.println("flip    bb:" + bb.position() + " " + bb.limit());
                out.write(bb);
                bb.compact();
            }
            IOUtils.closeStream(in, out);  //  in.close() >> closes fIn  out.close() >> closes fout
            NIOUtils.readout(PATH + "result_out.txt");

            System.out.printf(FORMAT, "ByteBuffer compact() [position]:");
            in = new FileInputStream(PATH + "result.txt").getChannel();
            out = new FileOutputStream(PATH + "result_out.txt").getChannel();
            while ((in.read(bb)) != -1) {
                bb.flip();
                System.out.println("flip    bb:" + bb.position() + " " + bb.limit());
                out.write(bb);
                bb.position(60); //  [position .. limit] > 0 write is not finished and there are bytes to copy
                bb.compact();
            }
            IOUtils.closeStream(in, out);  //  in.close() >> closes fIn  out.close() >> closes fout
            NIOUtils.readout(PATH + "result_out.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

// byteOrder
        System.out.printf(FORMAT, "Byteint byteOrder():");
        int size = 5;
        IntBuffer bInt = IntBuffer.allocate(size);
        LongBuffer bLong = LongBuffer.allocate(size);


        for (int i = 0; i < size; i++) {
            bInt.put(i * 1200);
            bLong.put(i * 10200);
        }

        NIOUtils.readout(bInt);
        NIOUtils.readout(bLong);

        System.out.printf("int order: %s long order:  %s%n", bInt.order(), bLong.order());

// byte order
        System.out.printf(FORMAT,"Byte Order:");

        ByteBuffer bByte = ByteBuffer.allocate(size*8);
        System.out.printf("orders  byte:%s int:%s long:%s float:%s double:%s%n",
                ByteBuffer.allocate(1).order(), IntBuffer.allocate(1).order(),
                LongBuffer.allocate(1).order(), FloatBuffer.allocate(1).order(),
                DoubleBuffer.allocate(1).order());

        NIOUtils.longToByte(bByte,bLong);

        bByte.order(ByteOrder.LITTLE_ENDIAN);      // read byte BE     reversed
        NIOUtils.byteToLong(bByte,bLong);
        NIOUtils.readout(bLong);

// asLongBuffer
        bByte.order(ByteOrder.BIG_ENDIAN);       // read byte LE      normal
        LongBuffer byteLong = bByte.flip().asLongBuffer();
        bLong.clear();
        while(byteLong.hasRemaining())
            bLong.put(byteLong.get());
        NIOUtils.readout(bLong);


// read only
        LongBuffer bLongReadOnly = bLong.asReadOnlyBuffer();
        bLongReadOnly.flip();
        NIOUtils.readout(bLongReadOnly);
        try {
            bLongReadOnly.put(1, 1200);
        } catch (ReadOnlyBufferException e) {
            System.out.printf("Buffer Exception:%s%n", e);
        }



// ByteBuffer
        System.out.printf(FORMAT, "ByteBuffer Read:");

        Charset charset = Charset.forName("UTF-8");
        s = "façade touché Пример кодировки";
        System.out.printf("string: %s%n",s);

        ByteBuffer bb = charset.encode(s);
        System.out.printf("array: %s%n", output(bb));

        CharBuffer cbc = CharBuffer.wrap(s);
        ByteBuffer bbc = charset.encode(cbc);
        System.out.printf("array: %s%n", output(bbc));

        System.out.printf(FORMAT, "CharrBuffer to String:");
        bb.rewind();
        CharBuffer cbb = charset.decode(bb);
        System.out.printf("array: %s%n", output(cbb));
        cbb.rewind();
        s = cbb.toString();

        System.out.printf("array: %s%n", cbb);

        System.out.printf(FORMAT, "ByteBuffer asCharBuffer to String:");

        b = ByteBuffer.allocate(s.length() * 2);
        b.asCharBuffer().put(s);

        cbb = Charset.forName("UTF-8").decode(b);
        System.out.printf("UTF8 : %s%n", cbb);

        b.rewind();
        cbb = Charset.forName("UTF-16").decode(b);
        System.out.printf("UTF16: %s%n", cbb);

        b.rewind();
        System.out.printf("UTF16 asCharBuffer: %s%n", b.asCharBuffer().toString());

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

}
