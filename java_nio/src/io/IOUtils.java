package io;

import java.io.*;
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
    public static final String FORMAT = "%n%s%n------------------------%n";
    public static final String PATH = "./data/nio/";
    public static final String STRING = "string value mark digital strong requirement";
    public static final String STRING2 = "new matcher lesson case file value ";
    public static final String STRING_ENC = "Строка для проверки encoding new matcher lesson case file value\n";

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
        if (!in.markSupported()) {
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
        } catch (IOException e) {
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

    // Readers
    public static void checkMark(BufferedReader br, boolean isExceeded, int size, int bSize) {
        if (!br.markSupported()) {
            System.out.println("mark() is not supported");
            return;
        }
        try {
            int limit = size;
            if (isExceeded) limit -= 30;  // делаем меньше
            br.read();  // read 2 bytes
            br.read();
            br.mark(limit);

            String s;
            size = 0;
            while((s = br.readLine())!= null) {  // через построчное чтение
                System.out.printf("%s%n",s);
                size += s.length()+2;
            }
            System.out.println("read:" + size + " limit:" + limit + " buffer:" + bSize);

            System.out.println();
            System.out.println("read : ok");
            br.reset();
            System.out.println("reset: ok");
        } catch (IOException e) {
            System.out.println("<< reset() Exception"+e+" >>");
        } finally {
            IOUtils.closeStream(br);
        }

    }

    public static void checkMark(Reader r, boolean isExceeded, int size, int bSize) {
        if (!r.markSupported()) {
            System.out.println("mark() is not supported");
            return;
        }
        try {
            int limit = size;
            if (isExceeded) limit -= 10;  // делаем меньше
            r.read();  // read 2 bytes
            r.read();
            r.mark(limit);

            int len;
            char[] chars = new char[50];
            while (r.ready() && (len = r.read(chars)) > 0) {
                System.out.printf("%s", new String(chars, 0, len));
            }
            System.out.println("read:" + size + " limit:" + limit + " buffer:" + bSize);

            System.out.println();
            System.out.println("read : ok");
            r.reset();
            System.out.println("reset: ok");
        } catch (IOException e) {
            System.out.println("<< reset() Exception"+e+" >>");
        } finally {
            IOUtils.closeStream(r);
        }

    }


    public static void readout(BufferedReader br, InputStream in) {
        try {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.printf("%s%n", s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(br);
            IOUtils.closeStream(in);
        }
    }

    public static void readout(BufferedReader br) {
        try {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.printf("%s%n", s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(br);
        }
    }

    public static void readout(LineNumberReader lr) {
        try {
            String s;
            while ((s = lr.readLine()) != null) {
                System.out.printf("%02d:%s%n",lr.getLineNumber(), s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(lr);
        }
    }



    public static void readout(Reader r) {
        try {
            int len;
            char[] chars = new char[50];
            while (r.ready() && (len = r.read(chars)) > 0) {
                System.out.printf("%s", new String(chars, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(r);
        }
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
