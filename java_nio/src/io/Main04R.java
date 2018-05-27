package io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;

import static nio.Main01.FORMAT;
import static nio.Main01.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 23-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04R {

    public static void main(String[] args) {
        System.out.printf(FORMAT, "Reader:");

// Charset
        System.out.printf(FORMAT, "Charset:");
        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(PATH + "result.txt");
            Charset cUTF = Charset.forName("UTF-8");
            Charset cWIN = Charset.forName("WINDOWS-1251");
            Charset cKOI8 = Charset.forName("KOI8-R");
            CharsetDecoder dUTF = new UserDecoder(Charset.forName("UTF-8"), 2, 2);
            CharsetDecoder dWIN = new UserDecoder(Charset.forName("WINDOWS-1251"), 2, 2);
            CharsetDecoder dKOI8 = new UserDecoder(Charset.forName("KOI8-R"), 2, 2);

//            for (Charset charset : Charset.availableCharsets().values()) {
//                System.out.println(charset.toString());
//            }
            fs.close();

            System.out.printf(FORMAT, "Charset " + cUTF + ":");
            fs = new FileInputStream(PATH + "result_u.txt");
            br = new BufferedReader(new InputStreamReader(fs, cUTF));
            IOUtils.readout(br, fs); // closes br >> InputStreamReader, closes in

            System.out.printf(FORMAT, "User CharsetDecoder " + dUTF + ":");
            fs = new FileInputStream(PATH + "result_u.txt");
            br = new BufferedReader(new InputStreamReader(fs, dUTF));       // closes InputStreamReader
            IOUtils.readout(br, fs);                                        // closes br >> InputStreamReader, in


            System.out.printf(FORMAT, "Charset " + cWIN + ":");
            fs = new FileInputStream(PATH + "result_w.txt");
            br = new BufferedReader(new InputStreamReader(fs, cWIN));
            IOUtils.readout(br, fs);

            System.out.printf(FORMAT, "User CharsetDecoder " + dWIN + ":");
            fs = new FileInputStream(PATH + "result_w.txt");
            br = new BufferedReader(new InputStreamReader(fs, dWIN));           // closes InputStreamReader
            IOUtils.readout(br, fs); // closes br >> InputStreamReader, closes in

            System.out.printf(FORMAT, "Charset " + cKOI8 + ":");
            fs = new FileInputStream(PATH + "result_k.txt");
            br = new BufferedReader(new InputStreamReader(fs, cKOI8));
            IOUtils.readout(br, fs);

            System.out.printf(FORMAT, "User CharsetDecoder " + dKOI8 + ":");
            fs = new FileInputStream(PATH + "result_k.txt");
            br = new BufferedReader(new InputStreamReader(fs, dKOI8));           // closes InputStreamReader
            IOUtils.readout(br, fs); // closes br >> InputStreamReader, closes in

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fs);
        }


// FileReader
        System.out.printf(FORMAT, "FileReader and BufferedReader:");
        br = null;
        try {
            br = new BufferedReader(new FileReader(PATH + "result_u.txt"), 100);  // internal buffer
            String s;
            while ((s = br.readLine()) != null) {
                System.out.printf("%s%n", s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(br);
        }

// BufferedReader
        System.out.printf(FORMAT, "BufferedReader:");
        fs = null;
        br = null;
        try {
            int bSize = 250;
            fs = new FileInputStream(PATH + "result_u.txt");
            br = new BufferedReader(new InputStreamReader(fs, Charset.defaultCharset()), bSize);  // internal buffer size 100 char
            IOUtils.readout(br, fs);

            System.out.printf(FORMAT, "BufferedReader mark() false:");
            fs = new FileInputStream(PATH + "result_u.txt");
            br = new BufferedReader(new InputStreamReader(fs, Charset.defaultCharset()), bSize);  // internal buffer size 100 char
            IOUtils.checkMark(br, false, fs.available(), bSize);


            System.out.printf(FORMAT, "BufferedReader mark() true:");
            fs = new FileInputStream(PATH + "result_u.txt");
            br = new BufferedReader(new InputStreamReader(fs, Charset.defaultCharset()), bSize);  // internal buffer size 100 char
            IOUtils.checkMark(br, true, fs.available(), bSize);
            int k = 1;


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(br);
            IOUtils.closeStream(fs);
        }


// CharArrayReader
        System.out.printf(FORMAT, "CharArrayReader:");
        CharArrayReader cr = null;
        try {
            cr = new CharArrayReader(IOUtils.STRING_ENC.toCharArray());  // utf-8
            char[] chars = new char[50];
            int len;
            while ((len = cr.read(chars)) > 0) {
                System.out.printf("%s", new String(chars, 0, len));
            }
            cr.close();
// encoded
            final CharsetDecoder decoder = Charset.forName("WINDOWS-1251").newDecoder();
            final CharsetEncoder encoder = Charset.forName("WINDOWS-1251").newEncoder();

            char[] chs = IOUtils.STRING_ENC.toCharArray();              // characters
            byte[] bbs = encoder.encode(CharBuffer.wrap(chs)).array();  // encoded WINDOWS-1251 bytes
            chars = decoder.decode(ByteBuffer.wrap(bbs)).array();       // decoded WINDOWS-1251

            System.out.printf("orig:%s", new String(chs));
            System.out.printf("utf8:%s", new String(bbs, Charset.forName("UTF-8")));
            System.out.printf("byte:%s", new String(bbs, Charset.forName("WINDOWS-1251")));
            System.out.printf("char:%s", new String(chars));

            cr = new CharArrayReader(chars);
            cr.mark(chs.length);
            chars = new char[50];
            while ((len = cr.read(chars)) > 0) {
                System.out.printf("%s", new String(chars, 0, len));
            }
            cr.reset();
            while ((len = cr.read(chars)) > 0) {
                System.out.printf("%s", new String(chars, 0, len));
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding Exception:" + e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cr != null) cr.close();

        }


        System.out.printf(FORMAT, "PipedReader");
        final PipedWriter ppw = new PipedWriter();
        final PipedReader ppr = new PipedReader();
        final ReentrantLock sLock = new ReentrantLock();
        Runnable rOut = () -> {
            try {
                ppw.connect(ppr);
                Thread.sleep(10);
                String s = IOUtils.STRING_ENC; // UTF-8
                byte[] bbs = s.getBytes(Charset.forName("WINDOWS-1251"));
                String s2 = new String(bbs, Charset.forName("WINDOWS-1251"));            // bytes >> win-1251
                String s3 = new String(s.getBytes(Charset.forName("WINDOWS-1251")));    // UTF-8 >> win-1251
                ppw.write(String.format("UTF-8:%n"));
                ppw.write(s);
                ppw.write(String.format("WINDOWS-1251:%n"));
                ppw.write(s2);
                ppw.write(String.format("UTF-8 >> WINDOWS-1251:%n"));
                ppw.write(s3);

                ppw.flush();

                synchronized (sLock) {
                    sLock.wait();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                try {

                    ppw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("pipedOut: closed");

        };
        Runnable rIn = () -> {
            try {
                char[] chars = new char[50];
                int len;
                while (!ppr.ready()) {     // awaiting
                    System.out.print(".");
                    Thread.sleep(1);
                }
                System.out.println();
                while (ppr.ready() && (len = ppr.read(chars)) > 0) {  // блокирующее чтение Reader
                    String s = new String(chars, 0, len);
                    System.out.printf("%s", s);
                }

                synchronized (sLock) {
                    sLock.notifyAll();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    ppr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("pipedIn: closed");
        };

        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.execute(rOut);
        exec.execute(rIn);
        exec.shutdown();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// StringReader
        System.out.printf(FORMAT, "StringReader:");
        StringReader sr = null;
        br = null;
        try {
            String s = IOUtils.STRING_ENC;
            sr = new StringReader(s);
            IOUtils.readout(sr);
// string reader
            int bSize = 200;
            System.out.printf(FORMAT, "CheckMark StringReader:");
            sr = new StringReader(s);
            IOUtils.checkMark((Reader) sr, false, s.length(), bSize);
            sr = new StringReader(s);
            IOUtils.checkMark((Reader) new BufferedReader(sr, bSize), true, s.length(), bSize);

// buffered reader
            System.out.printf(FORMAT, "CheckMark BufferedReader(StringReader):");
            sr = new StringReader(s);
            IOUtils.checkMark((Reader) new BufferedReader(sr, bSize), false, s.length(), bSize);
            sr = new StringReader(s);
            IOUtils.checkMark((Reader) new BufferedReader(sr, bSize), true, s.length(), bSize);


        } finally {
            IOUtils.closeStream(br);
        }

// LineNumberReader
        System.out.printf(FORMAT, "LineNumberReader:");
        LineNumberReader lr = null;
        br = null;
        try {
            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
            lr = new LineNumberReader(br);
            IOUtils.readout(lr); // closes br


        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(lr); // closes br
        }


// FilterReader
        System.out.printf(FORMAT, "FilterReader:");
        FilterReader ftr = null;
        br = null;
        try {
            ftr = new FilterReader(new FileReader(PATH + "result.txt")) {
                @Override
                public int read(char[] cbuf, int off, int len) throws IOException {
                    len = super.read(cbuf, off, len);
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            if (cbuf[i] == '*') cbuf[i] = '+';
                            if (cbuf[i] == '-') cbuf[i] = '/';
                        }
                    }
                    return len;
                }
            };
            br = new BufferedReader(ftr, 100);  // internal buffer
            IOUtils.readout(br);


        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(br);
        }

// PushBackReader
        System.out.printf(FORMAT, "PushbackReader:");
        PushbackReader pbr = null;
        br = null;
        try {
            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
            pbr = new PushbackReader(br, 10); // all bytes returned

            final StringBuilder sb = new StringBuilder();
            final char[] chars = new char[100];

            BiPredicate<String, Integer> p = (s1, v1) -> String.CASE_INSENSITIVE_ORDER
                    .compare(new String(chars, 0, v1), s1.substring(1)) == 0;

            int len;
            int c;
            while ((c = pbr.read()) > 0) {
                switch (c) {
                    default:
                        sb.append((char) c);
                        break;
                    case '/':
                        int c2 = pbr.read();
                        int c3 = pbr.read();
                        if (c2 == '*' && c3 == '*') {
                            sb.append("<!>");
                            break;
                        } else {
                            sb.append('/');
                            pbr.unread(c2);
                            pbr.unread(c3);
                        }
                        break;
                    case '*':
                        String s = "* created:"; // case insensitive
                        len = pbr.read(chars, 0, s.length() - 1);
                        if (p.test(s, len)) {
                            sb.append(">> Developed by");
                            break;
                        } else {
                            sb.append(s.charAt(0));
                            pbr.unread(chars, 0, len);
                        }
                        break;
                }
            }
            System.out.printf("%s", sb);

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(pbr);
        }

// PrintWriter
        System.out.printf(FORMAT, "DecoratorReader:");
        PrintWriter pw = null;
        PrintWriter pwb = null;
        FileReader fr = null;
        br = null;
        BufferedWriter bw = null;
        try {
            fr = new FileReader(PATH + "result.txt");
            pw = new PrintWriter(System.out);
            char[] chars = new char[100];
            int len;
            while (fr.ready()) {
                len = fr.read(chars);
                if (len > 0) {
                    pw.printf("%s", new String(chars, 0, len));
                }
            }

            pw.flush();  // DO NOT CLOSE
            System.out.println("System out working...");

            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
            bw = new BufferedWriter(new FileWriter(PATH + "printw.txt"), 100);
            pwb = new PrintWriter(bw);
            String s;
            while ((s = br.readLine()) != null) {
                pwb.printf("%s%n", s);
            }
            pwb.close(); // flushes pwb, closes bw
            br.close();

            br = new BufferedReader(new FileReader(PATH + "printw.txt"), 100);  // internal buffer
            while ((s = br.readLine()) != null) {
                pw.printf("%s%n", s);
            }
            pw.flush();

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(br);
            IOUtils.closeStream(fr);
        }


//// DecoratorReader
//        System.out.printf(FORMAT, "DecoratorReader:");
//        br = null;
//        try {
//            br = new BufferedReader(new FileReader(PATH + "result.txt"), 100);  // internal buffer
//            IOUtils.readout(br);
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeStream(br);
//        }
//
    }

    private static class UserDecoder extends CharsetDecoder {
        /**
         * Initializes a new decoder.  The new decoder will have the given
         * chars-per-byte values and its replacement will be the
         * string <code>"&#92;uFFFD"</code>.
         *
         * @param cs                  The charset that created this decoder
         * @param averageCharsPerByte A positive float value indicating the expected number of
         *                            characters that will be produced for each input byte
         * @param maxCharsPerByte     A positive float value indicating the maximum number of
         *                            characters that will be produced for each input byte
         * @throws IllegalArgumentException If the preconditions on the parameters do not hold
         */
        protected UserDecoder(Charset cs, float averageCharsPerByte, float maxCharsPerByte) {
            super(cs, averageCharsPerByte, maxCharsPerByte);
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            final CharsetDecoder decoder = charset().newDecoder();
            CoderResult cr = null;
            try {
                if (!in.hasRemaining()) return CoderResult.UNDERFLOW; // no input data

                final CharBuffer cout = decoder.decode(in);
                cr = decoder.flush(cout);                           // check for flush
                while (cout.hasRemaining()) {
                    out.put(cout.get());
                }
            } catch (CharacterCodingException e) {
                e.printStackTrace();
            }
            return cr;
        }

        @Override
        public String toString() {
            return charset().toString();
        }
    }

}
