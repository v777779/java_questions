package nio;

import util.IOUtils;
import util.NIOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Locale;
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
        final boolean isShort = true;
// channel

        System.out.printf(FORMAT, "Channel IO:");
        System.out.println("Attention: set isShort=false to run all items");

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
                        if (!isShort)
                            Thread.sleep(500);
                        else
                            Thread.sleep(10);
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
                if (!isShort)
                    Thread.sleep(1000);
                else
                    Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();

// scattering
        System.out.printf(FORMAT, "Scattering Channel:");
        ScatteringByteChannel sin = null;
        GatheringByteChannel gout = null;
        try {
//            sin = (ScatteringByteChannel) Channels.newChannel(new FileInputStream(PATH + "result_i.txt"));
//            gout = (GatheringByteChannel) Channels.newChannel(new FileOutputStream(PATH + "result_o.txt"));
            sin = new FileInputStream(PATH + "result_i.txt").getChannel();
            gout = new FileOutputStream(PATH + "result_o.txt").getChannel();
            ByteBuffer b5 = ByteBuffer.allocate(5);
            ByteBuffer b3 = ByteBuffer.allocate(3);
            ByteBuffer[] bbs = {b5, b3};
            sin.read(bbs);                          // прочитать в два буфера из файла
            NIOUtils.readout(b5);
            NIOUtils.readout(b3);
            b5.rewind();
            b3.rewind();
            bbs[0] = b3;
            bbs[1] = b5;
            gout.write(bbs);                        // записать из двух буферов в файл
//            ((FileChannel) gout).force(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(sin, gout);
        }


        System.out.printf(FORMAT, "RandomAccessFile:");
        FileChannel fra = null;

        try {
            fra = new RandomAccessFile(PATH + "result_ra_test.txt", "rw").getChannel();
            long pos;
            System.out.printf("position: %d%n", (pos = fra.position()));
            System.out.printf("size    : %d%n", fra.size());
            ByteBuffer b = ByteBuffer.allocate(IOUtils.STRING_ENC.length() * 2);
            b.asCharBuffer().put(IOUtils.STRING_ENC); // работает четко по символам Unicode
            fra.write(b);                   // записать
            fra.force(true);        // точно записать
            System.out.printf("position: %d%n", fra.position());
            System.out.printf("size    : %d%n", fra.size());
            b.clear();
            fra.position(pos);              // start position
            fra.read(b);
            b.flip();
            while (b.hasRemaining()) {
                System.out.printf(Locale.ENGLISH, "%c", b.getChar());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fra); // closes FileInputStream, FileOutputStream, RandomAccessFile
        }

        System.out.printf(FORMAT, "FileChannel:");
        FileChannel fr = null;
        FileChannel fw = null;
        FileChannel frw = null;
        try {
            fr = new FileInputStream(PATH + "result.txt").getChannel();
            fw = new FileOutputStream(PATH + "result_w.txt").getChannel();
            frw = new RandomAccessFile(PATH + "result_ra.txt", "rw").getChannel();

            ByteBuffer b = ByteBuffer.allocate(200);
            while (fr.read(b) != -1) {          // получили данные в буфер
                b.flip();                       // отсекаем
                while (b.hasRemaining()) {      // пишем в выходной поток
                    fw.write(b);
                    b.flip();
                    frw.write(b);
                }
                b.compact();
                b.clear();
            }
            frw.force(true);  // force store changes to file
            frw.position(0);
            while (frw.read(b) != -1) {          // получили данные в буфер
                b.flip();                       // отсекаем
                System.out.printf("%s", new String(b.array(), Charset.defaultCharset()));
                b.clear();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fr, fw, frw); // closes FileInputStream
        }

        System.out.printf(FORMAT, "FileLock:");
        FileChannel in = null;
        FileChannel out = null;
//        FileLock lock = null;

        try {
            if (!isShort) {
                Runtime.getRuntime().exec("cmd /c start call java -cp ./out/production/java_nio nio.fileLock.MainLock w");
                Runtime.getRuntime().exec("cmd /c start call java -cp ./out/production/java_nio nio.fileLock.MainLock");
            } else {
                Runtime.getRuntime().exec("cmd /c start java -cp ./out/production/java_nio nio.fileLock.MainLock w");
                Runtime.getRuntime().exec("cmd /c start java -cp ./out/production/java_nio nio.fileLock.MainLock");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
//        IOUtils.process("java -cp out/production/java_nio util.IOUtils");

// MappedByteBuffer
        System.out.printf(FORMAT, "MappedByteBuffer:");

// ATTENTION This does NOT WORK because of splitting between ascii and unicode
//            byte[] bytes = new byte[17];
//            int len;
//            while ((len = mb.remaining()) > 0) {
//                if (len > bytes.length) len = bytes.length;
//                mb.get(bytes, 0, len);
//                String s = new String(bytes, 0, len, Charset.forName("UTF-8"));
//                System.out.printf("%s", s);
//            }

        NIOUtils.checkMappedBuffer("READ_WRITE");
        NIOUtils.checkMappedBuffer("PRIVATE");


        System.out.printf(FORMAT, "Channel transferFrom() transferTo():");
        in = null;
        WritableByteChannel wout = null;
        try {
            in = new FileInputStream(PATH + "result.txt").getChannel();
            wout = NIOUtils.newInstance(System.out);  // Channels.newChannel()
            in.transferTo(0, in.size(), wout); // из файла в канал WritableByteChannel

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in, wout);
        }


// MappedByteBuffer with CharBuffer
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
