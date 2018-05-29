package nio;

import util.IOUtils;
import util.NIOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 29-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02C {

    private static void copy(ReadableByteChannel in, WritableByteChannel out) throws IOException {
        ByteBuffer b = ByteBuffer.allocateDirect(2048);
        ByteBuffer b2 = b.asReadOnlyBuffer();
        byte[] bytes = new byte[4];

        while (in.read(b) != -1) {
            b.flip();               // отсекаем
            out.write(b);           // пишем в выходной поток что есть
// check data
            if (b2.hasRemaining() && b2.limit() >= bytes.length) {
                b2.get(bytes);
                if (new String(bytes).equals("exit")) {
                    return;
                }
                b2.flip();
            }
            b.compact();            // если прервано сохраняем

        }
        b.flip();                   // дозапись если есть
        while (b.hasRemaining()) {  // выкачиваем остатки полностью
            out.write(b);
        }
    }

    private static void copyAlt(ReadableByteChannel in, WritableByteChannel out) throws IOException {
        ByteBuffer b = ByteBuffer.allocateDirect(2048);
        byte[] bytes = new byte[4];
        while (in.read(b) != -1) {          // получили данные в буфер
            b.flip();                       // отсекаем
            if (b.limit() >= bytes.length) {
                b.get(bytes);
                if (new String(bytes).equals("exit")) {
                    return;
                }
                b.position(0);
            }

            while (b.hasRemaining()) {      // пишем в выходной поток
                out.write(b);
            }
            b.clear();
        }
    }


    public static void main(String[] args) {

// channel

        System.out.printf(FORMAT, "Channel IO:");

        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.printf(FORMAT, "Channel copy:");
                ReadableByteChannel in = null;
                WritableByteChannel out = null;
//              UserReadableChannel  in = new UserReadableChannel(Channels.newChannel(System.in));
                try {
                    in = NIOUtils.newInstance(System.in);    // четко отрабатывает свой класс
                    out = NIOUtils.newInstance(System.out);
                    System.out.println("copy: type than <Enter>('exit' to exit:");
// waiting
                    int count = 0;
                    while (System.in.available() == 0 && count++ < 10) {
                        Thread.sleep(100);
                        System.out.print(".");
                    }
                    if (count > 10) return;
// scanner
                    copy(in, out);
                    System.out.println("copyAlt: type than <Enter>('exit' to exit:");
                    copyAlt(in, out);
                    in.close();
                    in.isOpen();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeStream(in, out);

                }
            }
        };
        ExecutorService exec = Executors.newFixedThreadPool(1);
        exec.execute(r);
        exec.shutdown();
        while (!exec.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
// scattering
        System.out.printf(FORMAT, "Scattering Channel:");
        ScatteringByteChannel sin = null;
        GatheringByteChannel gout = null;
        FileInputStream fs = null;
        FileOutputStream fo = null;
        try {
            fs = new FileInputStream(PATH + "result_i.txt");
            fo = new FileOutputStream(PATH + "result_o.txt");
            sin = (ScatteringByteChannel) Channels.newChannel(fs);
            gout = (GatheringByteChannel)Channels.newChannel(fo);

            ByteBuffer b5 = ByteBuffer.allocate(5);
            ByteBuffer b3 = ByteBuffer.allocate(3);
            ByteBuffer[] bbs = {b5, b3};
            sin.read(bbs);
            NIOUtils.readout(b5);
            NIOUtils.readout(b3);
            b5.rewind();
            b3.rewind();
            bbs[0] = b3;
            bbs[1] = b5;
            gout.write(bbs);


//            ByteBuffer b = ByteBuffer.allocate(200);
//            while (in.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    out.write(b);
//                }
//                b.clear();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(sin, gout);
        }

//        System.out.printf(FORMAT, "Channel:");
//        in = null;
//        out = null;
//        try {
//            in = Channels.newChannel(System.in);
//            out = Channels.newChannel(System.out);
//            ByteBuffer b = ByteBuffer.allocate(200);
//            while (in.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    out.write(b);
//                }
//                b.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in, out);
//        }

//        System.out.printf(FORMAT, "Channel:");
//        in = null;
//        out = null;
//        try {
//            in = Channels.newChannel(System.in);
//            out = Channels.newChannel(System.out);
//            ByteBuffer b = ByteBuffer.allocate(200);
//            while (in.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    out.write(b);
//                }
//                b.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in, out);
//        }

//        System.out.printf(FORMAT, "Channel:");
//        in = null;
//        out = null;
//        try {
//            in = Channels.newChannel(System.in);
//            out = Channels.newChannel(System.out);
//            ByteBuffer b = ByteBuffer.allocate(200);
//            while (in.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    out.write(b);
//                }
//                b.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(in, out);
//        }
    }


    final Runnable inKey = new Runnable() {
        final FileInputStream fs = null;
        final FileOutputStream fo = null;
        BufferedReader br = null;

        @Override
        public void run() {
            try {
                PrintStream sOld = System.out;
                InputStream sIn = System.in;
                FileInputStream fs = new FileInputStream(PATH + "inkey.txt");
                PrintStream fo = new PrintStream(new FileOutputStream(PATH + "outkey.txt"));
                System.setIn(fs);
                System.setOut(fo);
                br = new BufferedReader(new FileReader(PATH + "outkey.txt"));
                Thread.sleep(5000);
                String s;
                while ((s = br.readLine()) == null) {
                    Thread.sleep(100);
                }
                while ((s = br.readLine()) != null) {
                    if (s.equals("exit")) return;
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    // реально не выполняет ничего, просто пробрасывает
    private static class UserReadableChannel implements ReadableByteChannel {
        private ReadableByteChannel ch;

        public UserReadableChannel(ReadableByteChannel ch) {
            this.ch = ch;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return ch.read(dst);
        }

        @Override
        public boolean isOpen() {
            return ch.isOpen();
        }

        @Override
        public void close() throws IOException {  // закрывает канал System.in
            ch.close();                             // если отключить, просто оставит открытым
        }
    }


}
