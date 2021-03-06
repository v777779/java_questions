package util;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.Arrays;
import java.util.stream.Collectors;

import static util.IOUtils.FORMAT;
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

    public static final String[] STRINGS_ENC = {
            "Roses are red в красном",
            "Violets are blue то есть в синем",
            "Sugar is sweet наконец то сладкий",
            "And so are you да это ты."
    };

    public static CharBuffer readMappedBuffer(MappedByteBuffer mb) {
        CharBuffer cb = mb.asCharBuffer();
        while (cb.remaining() > 0) {
            System.out.printf("%c", cb.get());
        }
        System.out.println();
        return cb;
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalized");
    }


    public static void checkMappedBuffer(String sMode) {
        FileChannel.MapMode mapMode;
        if (sMode.equals("READ_WRITE"))
            mapMode = FileChannel.MapMode.READ_WRITE;
        else
            mapMode = FileChannel.MapMode.PRIVATE;

        System.out.printf(FORMAT, "MappedByteBuffer:" + sMode);

        FileChannel fc = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(PATH + "result_random.txt", "rw");
            raf.setLength(0);                           // clear files
            fc = raf.getChannel();
            String s = Arrays.stream(NIOUtils.STRINGS_ENC).collect(Collectors.joining(String.format("%n")));
// make UTF-16
            ByteBuffer b = ByteBuffer.allocate(s.length() * 2);
            b.asCharBuffer().put(s);
            fc.write(b);
            fc.force(true);
            fc.close();

// read  UTF-16
            fc = new RandomAccessFile(PATH + "result_random.txt", "rw").getChannel();
            long size = fc.size();
            System.out.printf("Size: %d%n", size);
            MappedByteBuffer mb = fc.map(mapMode, 0, size);
            CharBuffer cb = NIOUtils.readMappedBuffer(mb);

// reverse
            for (int i = 0; i < cb.limit() / 2; i++) {
                char c = cb.get(i);
                char c2 = cb.get(cb.limit() - i - 1);
                cb.put(i, c2);
                cb.put(cb.limit() - i - 1, c);
            }
            cb.flip();
            System.out.printf(FORMAT, "Check Character Buffer:");
            while (cb.remaining() > 0) {
                System.out.printf("%c", cb.get());
            }
            System.out.printf("%n");

// check read_only
            System.out.printf(FORMAT, "MappedMap READ_ONLY:");
            mb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size); // half files
            NIOUtils.readMappedBuffer(mb);

            try {
                mb.put(0, (byte) 'D');

            } catch (ReadOnlyBufferException e) {
                System.out.printf("Exception:%s%n", e);
            }

// garbage collect to UNMAP
            mb = null;
            cb = null;
            System.gc();
// garbage collect to UNMAP

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fc, raf);
        }
    }

    // read check git changes
    public static CharBuffer getFilledCharBuffer(String s) {
        CharBuffer cb = CharBuffer.allocate(s.length());
        for (int i = 0; i < s.length(); i++) {
            cb.put(s.charAt(i));
        }
        return cb.flip();
    }

    public static void readout(ByteBuffer b) {
        b.flip();
        while (b.hasRemaining()) {
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

            IOUtils.close(br);
        }
    }

    public static void readout(IntBuffer b) {
        b.flip();
        while (b.hasRemaining()) {
            System.out.printf("%d ", b.get());
        }
        System.out.printf("%n");
    }

    public static void readout(LongBuffer b) {
        b.flip();
        while (b.hasRemaining()) {
            System.out.printf("%d ", b.get());
        }
        System.out.printf("%n");
    }

    // conversion
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

    // mark()
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


    public static ReadableByteChannel newInstance(InputStream in) {
        return new ReadableByteChannelImpl(in);
    }

    public static WritableByteChannel newInstance(OutputStream out) {
        return new WritableByteChannelImpl(out);
    }


    // classes
    private static class ReadableByteChannelImpl
            extends AbstractInterruptibleChannel    // Not really interruptible
            implements ReadableByteChannel {
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
        protected void implCloseChannel() throws IOException {  // канал закрыт, но System.in нет
//            in.close();
        }
    }

    private static class WritableByteChannelImpl
            extends AbstractInterruptibleChannel    // Not really interruptible
            implements WritableByteChannel {
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
//            System.out.printf("out not closed%n");
        }
    }
}
