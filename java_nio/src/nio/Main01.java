package nio;

import util.IOUtils;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {

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
        System.out.println("limit    :" + b.limit()+" c:"+c.limit());
        System.out.println("position :" + b.position()+" c:"+c.position());
        System.out.println("remaining:" + b.remaining()+" c:"+c.remaining());

        while(b.hasRemaining()) {
            System.out.printf("%c",(char)b.get());
        }
        System.out.println();
        while(c.hasRemaining()) {
            System.out.printf("%c",(char)c.get());
        }
        System.out.println();

        System.out.println("b: "+new String(b.array(),Charset.forName("UTF-8")));
        System.out.println("c: "+new String(c.array(),Charset.forName("UTF-8")));


        System.out.printf(FORMAT, "ByteBuffer read() write():");
        b = ByteBuffer.allocate(10);
        System.out.println("b:"+ Arrays.toString(b.array()));
        b.put((byte)10);
        b.put((byte)15);
        b.put(5,(byte)20);
        b.position(7);
        b.put((byte)30);
        System.out.println("b:"+ Arrays.toString(b.array()));
        System.out.println("limit    :" + b.limit());
        System.out.println("position :" + b.position());
        System.out.println("remaining:" + b.remaining());

        System.out.printf(FORMAT, "ByteBuffer bulk:");
        c = ByteBuffer.allocate(10);
        System.out.println("b:"+ Arrays.toString(b.array()));
        System.out.println("c:"+ Arrays.toString(c.array()));
        System.out.printf("position : %02d %02d%n", b.position(),c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());

        System.out.println("c.put(b.array(),0,5):");
        c.put(b.array(),0,5);
        System.out.printf("position : %02d %02d%n", b.position(),c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());

        System.out.println("c.put(b.array(),0,5):");
        c.put(b.array(),0,5);
        System.out.printf("position : %02d %02d%n", b.position(),c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());

        System.out.println("c:"+ Arrays.toString(c.array()));
        try {
            c.put(b);
        }catch (BufferOverflowException e) {
            System.out.println("Buffer Exception:"+e);
        }
        b.position(0);
        c.position(0);
        System.out.printf("position : %02d %02d%n", b.position(),c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());
        System.out.println("c.put(b):");
        c.put(b);
        System.out.printf("position : %02d %02d%n", b.position(),c.position());
        System.out.printf("limit    : %02d %02d%n", b.limit(), c.limit());


        c.put(b);
        System.out.println("b:"+ Arrays.toString(b.array()));
        System.out.println("c:"+ Arrays.toString(c.array()));



//// DecoratorReader
//        System.out.printf(FORMAT, "DecoratorReader:");
//        br = null;
//        try {
//            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
//            util.IOUtils.readout(br);
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        } finally {
//            util.IOUtils.closeStream(br);
//        }
//

//// DecoratorReader
//        System.out.printf(FORMAT, "DecoratorReader:");
//        br = null;
//        try {
//            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
//            util.IOUtils.readout(br);
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        } finally {
//            util.IOUtils.closeStream(br);
//        }
//

//// DecoratorReader
//        System.out.printf(FORMAT, "DecoratorReader:");
//        br = null;
//        try {
//            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
//            util.IOUtils.readout(br);
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        } finally {
//            util.IOUtils.closeStream(br);
//        }
//


//// DecoratorReader
//        System.out.printf(FORMAT, "DecoratorReader:");
//        br = null;
//        try {
//            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
//            util.IOUtils.readout(br);
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        } finally {
//            util.IOUtils.closeStream(br);
//        }
//


    }
}
