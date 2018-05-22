package io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class IOUtils {
    public static void closeStream(Closeable in) {
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeStream(List<InputStream> list) {
        for (InputStream in : list) {
            closeStream(in);
        }
    }

    public static void readout(InputStream in) {
        try {
            int c;
            while ((c = in.read()) != -1) {
                System.out.printf("%c", c);
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void readout(InputStream in, byte[] bytes) {
        try {
            int len;
            while ((len = in.read(bytes)) > 0) {
                System.out.printf("%s", new String(bytes, 0, len));
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void readout(InputStream in, byte[] bytes, int size) {
        try {
            int len;
            len = in.read(bytes, 0, size);
            System.out.printf("%s", new String(bytes, 0, len));
            System.out.println();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    public static void checkMark(InputStream in, boolean isExceeded) {
        if(!in.markSupported()) {
            System.out.println("mark() is not supported");
            return;
        }

        try {
            byte[] bytes = new byte[100];
            int size = in.available() - 4;
            in.read();  // read 2 bytes
            in.read();
            in.mark(size);
            if (isExceeded) size++;
            int len;
            while (size > 0 && (len = in.read(bytes, 0, size > bytes.length ? bytes.length : size)) > 0) {
                System.out.printf("%s", new String(bytes, 0, len));
                size -= bytes.length;
            }
            System.out.println();
            System.out.println("read : ok");
            in.reset();
            System.out.println("reset: ok");
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in);
        }

    }


    private static void readCount(InputStream in, byte[] bytes, int limit) throws IOException {
        while (limit > 0) {
            int len = in.read(bytes, 0, limit);  // прочитали первые len/2 байт
            if (len <= 0) return;
            System.out.printf("bytes[" + len + "]:%s:%n", new String(bytes, 0, len));
            limit -= len;
        }
    }

    public static void checkMarkSequence(InputStream in, int len) throws IOException {
// window 0..5< len >5+len..eof    len+5 < in.available()

        if (in.markSupported()) {
            byte[] bytes = new byte[len];
            readCount(in, bytes, len / 2);
            System.out.println("mark:" + (len - 2) + " read:" + (len - 4));
            in.mark(len - 2);    // окно в len байт
            readCount(in, bytes, len - 4);
            System.out.println("reset:" + " read:" + (len - 2));
            in.reset();
            readCount(in, bytes, len - 2);
// exceed limit
            System.out.println("reset:" + " read:" + (len));
            in.reset();
            readCount(in, bytes, len);
            System.out.println("reset:" + " read:" + (len - 50));
            in.reset();
            readCount(in, bytes, len - 50);
// next mark
            System.out.println("mark next:" + " read:" + (len - 50));
            in.mark(len);
            readCount(in, bytes, len);
            System.out.println("reset:" + " read:" + (len - 50));
            in.reset();
            readCount(in, bytes, len - 50);
        } else {
            System.out.println("Mark is not supported");
            return;
        }
        System.out.println();
    }


    public static class Enumerator<T> implements Enumeration<T> {
        private final Iterator<T> iterator;

        public Enumerator(Iterator<T> iterator) {
            this.iterator = iterator;
        }


        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public T nextElement() {
            return iterator.next();
        }
    }
}
