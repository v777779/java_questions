package nio;

import util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;

import static util.IOUtils.FORMAT;

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
        ReadableByteChannel in = null;
        WritableByteChannel out = null;

        try {

            in = newInstance(System.in);
            out = newInstance(System.out);
            System.out.println("copy: type than <Enter>('exit' to exit:");
            copy(in, out);
            System.out.println("copyAlt: type than <Enter>('exit' to exit:");
            copyAlt(in, out);
            in.close();
            in.isOpen();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in, out);
        }

// scattering
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

    private static ReadableByteChannel newInstance(InputStream in) {
        return new ReadableByteChannelImpl(in);
    }
    private static WritableByteChannel newInstance(OutputStream out) {
        return new WritableByteChannelImpl(out);
    }


    private static class ReadableByteChannelImpl
            extends AbstractInterruptibleChannel    // Not really interruptible
            implements ReadableByteChannel
    {
        private final InputStream in;
        private static final int TRANSFER_SIZE = 8192;
        private byte[] buf = new byte[0];
        private final Object readLock = new Object();

        ReadableByteChannelImpl(InputStream in) {
            this.in = in;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (!isOpen()) {
                throw new ClosedChannelException();
            }

            int len = dst.remaining();
            int totalRead = 0;
            int bytesRead = 0;
            synchronized (readLock) {
                while (totalRead < len) {
                    int bytesToRead = Math.min((len - totalRead),
                            TRANSFER_SIZE);
                    if (buf.length < bytesToRead)
                        buf = new byte[bytesToRead];
                    if ((totalRead > 0) && !(in.available() > 0))
                        break; // block at most once
                    try {
                        begin();
                        bytesRead = in.read(buf, 0, bytesToRead);
                    } finally {
                        end(bytesRead > 0);
                    }
                    if (bytesRead < 0)
                        break;
                    else
                        totalRead += bytesRead;
                    dst.put(buf, 0, bytesRead);
                }
                if ((bytesRead < 0) && (totalRead == 0))
                    return -1;

                return totalRead;
            }
        }

        @Override
        protected void implCloseChannel() throws IOException {
//            in.close();
            System.out.printf("in not closed%n");
        }
    }
    private static class WritableByteChannelImpl
            extends AbstractInterruptibleChannel    // Not really interruptible
            implements WritableByteChannel
    {
        private final OutputStream out;
        private static final int TRANSFER_SIZE = 8192;
        private byte[] buf = new byte[0];
        private final Object writeLock = new Object();

        WritableByteChannelImpl(OutputStream out) {
            this.out = out;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            if (!isOpen()) {
                throw new ClosedChannelException();
            }

            int len = src.remaining();
            int totalWritten = 0;
            synchronized (writeLock) {
                while (totalWritten < len) {
                    int bytesToWrite = Math.min((len - totalWritten),
                            TRANSFER_SIZE);
                    if (buf.length < bytesToWrite)
                        buf = new byte[bytesToWrite];
                    src.get(buf, 0, bytesToWrite);
                    try {
                        begin();
                        out.write(buf, 0, bytesToWrite);
                    } finally {
                        end(bytesToWrite > 0);
                    }
                    totalWritten += bytesToWrite;
                }
                return totalWritten;
            }
        }

        @Override
        protected void implCloseChannel() throws IOException {
//            out.close();
            System.out.printf("out not closed%n");
        }
    }
}
