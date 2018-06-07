package io;

import io.cypher.CypherUtils;
import util.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.*;

import static java.util.zip.Deflater.BEST_COMPRESSION;
import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03D {
    public static void main(String[] args) {
//IO Decorators

        System.out.printf(FORMAT, "Stream Decorators");

        String s = "string value mark digital strong ";
        String s2 = "new matcher lesson case file value ";

// BufferedInputStream
        System.out.printf(FORMAT, "BufferedInputStream:");
        BufferedInputStream bs = null;
        BufferedInputStream bs2 = null;
        SequenceInputStream ss = null;
        List<InputStream> list = new ArrayList<>();
        try {
            for (int i = 0; i < 2; i++) {
                list.add(new ByteArrayInputStream(s.getBytes()));
            }
            list.add(new FileInputStream(PATH + "result.txt")); // does not support mark()
//            Enumeration<InputStream> en = new util.IOUtils.Enumerator<>(list.iterator());
            Enumeration<InputStream> en = new Enumeration<InputStream>() {
                final Iterator<InputStream> it = list.iterator();

                @Override
                public boolean hasMoreElements() {
                    return it.hasNext();
                }

                @Override
                public InputStream nextElement() {
                    return it.next();
                }
            };
            ss = new SequenceInputStream(en);           // does not support mark()
            bs = new BufferedInputStream(ss);
            bs2 = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer

            IOUtils.checkMarkSequence(bs, 75);       // supports mark()
            System.out.println("===================");
            IOUtils.readout(bs);                       // up to the end
            System.out.println("===================");
            IOUtils.readout(bs2);                       // up to the end

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(list);
            IOUtils.close(ss);
            IOUtils.close(bs);
            IOUtils.close(bs2);
        }

// DataInputStream
        System.out.printf(FORMAT, "DataInputStream:");
        DataInputStream dIn = null;
        DataOutputStream dOut = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        BufferedReader br = null;               // readline support
        try {
            out = new BufferedOutputStream(new FileOutputStream(PATH + "data.txt"));
            dOut = new DataOutputStream(out);

            dOut.writeBoolean(true);
            dOut.writeChar('$');
            dOut.writeByte(115);
            dOut.writeInt(12003);
            dOut.writeLong(100254L);
            dOut.writeFloat(2.75F);
            dOut.writeDouble(1.235);
            dOut.writeUTF("Проверка UTF8");

            dOut.flush();
            dOut.close();

            in = new BufferedInputStream(new FileInputStream(PATH + "data.txt"));
            in.mark(0);
            dIn = new DataInputStream(in);                  // support mark for DataInputStream()
            System.out.println("bytes:" + dIn.available());

            int c;
            while ((c = dIn.read()) != -1) {
                System.out.printf("%02X ", c);
            }

            System.out.println();
            System.out.println("dIn:" + dIn.available());
            in.reset();
            System.out.println("dIn:" + dIn.available());

            System.out.printf("bool:%5b char:%c byte:%3d int:%8d long:%8d float:%8.3f double:%8.3f%n",
                    dIn.readBoolean(), dIn.readChar(), dIn.readByte(), dIn.readInt(), dIn.readLong(),
                    dIn.readFloat(), dIn.readDouble());
            System.out.printf("%s", dIn.readUTF());
            System.out.println();
// readline
            System.out.printf(FORMAT, "DataInputStream.readLine() >> BufferedReader:");
            in.close();
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"));
//            DataInputStream dIn = new DataInputStream(in);    //              dIn.readline() deprecated

            br = new BufferedReader(new InputStreamReader(in)); // closes in    br.readline() recommended
            in.mark(in.available());

            String line;
            while ((line = br.readLine()) != null) {  // readline
                System.out.printf("%s%n", line);
            }

            in.reset();
            System.out.println("mark:");
            while ((line = br.readLine()) != null) {  // readline
                System.out.printf("%s%n", line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(dIn);
            IOUtils.close(dOut);
            IOUtils.close(br);
            IOUtils.close(in);
            IOUtils.close(out);
        }

// CipherInputStream
        System.out.printf(FORMAT, "CipherInputStream:");
        CypherUtils.main(args);

//

        System.out.printf(FORMAT, "LineNumberInputStream >> LineNumberReader:");
        in = null;
        LineNumberReader lr = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
            lr = new LineNumberReader(new InputStreamReader(in));  // translate InputStream >> Reader >> LinNumberReader
            while ((s = lr.readLine()) != null) {
                System.out.println(lr.getLineNumber() + ":" + s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
            IOUtils.close(lr);
        }

// CheckedInputStream
        System.out.printf(FORMAT, "CheckedInputStream:");
        CheckedInputStream cin = null;
        in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
            Checksum cIF = new Checksum() {
                private long checksum = 0;

                @Override
                public void update(int b) {
                    checksum += b;
                }

                @Override
                public void update(byte[] b, int off, int len) {
                    for (int i = off; i < off + len; i++) {
                        checksum += b[i]; // XOR
                    }
                }

                @Override
                public long getValue() {
                    return checksum;
                }

                @Override
                public void reset() {
                    checksum = 0;
                }
            };

            cin = new CheckedInputStream(in, cIF);
            cin.mark(cin.available());
            Checksum checksum = cin.getChecksum();

            System.out.println("checksum:" + checksum.getValue());
// read all files
            int c;
            while ((c = cin.read()) != -1) {
                System.out.printf("%c", c);
                if (cin.available() < 10) break;
            }
            System.out.println();

            System.out.println("checksum:" + checksum.getValue());
            cin.reset();
            checksum.reset();
            System.out.println("checksum:" + checksum.getValue());

            while ((c = cin.read()) != -1) {
                System.out.printf("%c", c);
                if (cin.available() < 10) break;
            }
            System.out.println();
            System.out.println("checksum:" + cin.getChecksum().getValue());


        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
            IOUtils.close(cin);
        }

// DeflaterInputStream
        System.out.printf(FORMAT, "DeflaterInputStream:");
        DeflaterInputStream din = null;
        InflaterInputStream iin = null;
        in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
            out = new BufferedOutputStream(new FileOutputStream(PATH + "result.dft")); // compressed
            Deflater def = new Deflater(BEST_COMPRESSION, false);  // true no header for GZIP and PKZIP
            din = new DeflaterInputStream(in, def);
//            byte[] bytes = din.readAllBytes();  //JDK9
            byte[] bytes = new byte[100];
            int len;
            int source = in.available();
            int compressed = 0;
            while ((len = din.read(bytes)) > 0) {
                out.write(bytes, 0, len);  // compressed data
                compressed += len;
            }
            System.out.println("source: " + source + " compressed: " + compressed);

            din.close();            // close underlying in
            out.close();            // close compressed file
            System.out.println("Inflater:");
            in = new BufferedInputStream(new FileInputStream(PATH + "result.dft"), 100);  // internal buffer
            Inflater inf = new Inflater(false); // true  no header for GZIP PKZIP
            iin = new InflaterInputStream(in, inf);

            source = in.available();
            compressed = 0;
            while ((len = iin.read(bytes)) > 0) {
                compressed += len;
                System.out.printf("%s", new String(bytes, 0, len));
            }

            iin.close();            // close underlying in
            System.out.println("source: " + source + " uncompressed: " + compressed);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(din); // closes underlying in
            IOUtils.close(iin); // closes underlying in
            IOUtils.close(in);
            IOUtils.close(out);
        }


// InflaterInputStream
        System.out.printf(FORMAT, "Deflater and Inflater:");
        try {
            // Encode a String into bytes
            String inputString = "blahblahblah$\u20BD\u20AC";
            byte[] input = inputString.getBytes("UTF-8");

            // Compress the bytes
            byte[] output = new byte[100];
            Deflater compressor = new Deflater();
            compressor.setInput(input);
            compressor.finish();
            int compressedDataLength = compressor.deflate(output);

            // Decompress the bytes
            Inflater decompressor = new Inflater();
            decompressor.setInput(output, 0, compressedDataLength);
            byte[] result = new byte[100];
            int resultLength = decompressor.inflate(result);
            decompressor.end();

            // Decode the bytes into a String
            String outputString = new String(result, 0, resultLength, "UTF-8");
            System.out.println(outputString);


        } catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }


// DigestInputStream
        System.out.printf(FORMAT, "DigestInputStream:");
        DigestInputStream ds = null;
        in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            MessageDigest mds = MessageDigest.getInstance("SHA");

            System.out.println("hash MD5:" + toHex(md5.digest()));
            System.out.println("hash SHA:" + toHex(mds.digest()));

            ds = new DigestInputStream(in, md5);
            ds.mark(ds.available() + 1);          // full stream
            byte[] bytes = new byte[100];
            int len;
            while ((len = ds.read(bytes)) > 0) {
                System.out.printf("%s", new String(bytes, 0, len));
            }
            byte[] hash = md5.digest();
            System.out.println("hash MD5:" + toHex(hash));
            System.out.println("reset:");
            ds.reset();
            System.out.println("hash MD5:" + toHex(ds.getMessageDigest().digest()));

            System.out.printf(FORMAT, "Set new digest:");
            ds.setMessageDigest(mds);
            System.out.println("hash SHA:" + toHex(ds.getMessageDigest().digest()));
            while ((len = ds.read(bytes)) > 0) {
                System.out.printf("%s", new String(bytes, 0, len));
            }
            hash = ds.getMessageDigest().digest();
            System.out.println("hash SHA:" + toHex(hash));
            System.out.println("reset:");
            ds.reset();
            System.out.println("hash SHA:" + toHex(mds.digest()));
            ds.setMessageDigest(mds);

            while ((len = ds.read(bytes)) > 0) {
            }
            System.out.println("read:");
            System.out.println("hash SHA:" + toHex(mds.digest()));

            ds.close();
// url
            System.out.printf(FORMAT, "URL.stream():");
            URL url = new File(PATH + "result.txt").toURI().toURL();
            ds = new DigestInputStream(url.openStream(), md5);
            System.out.println("hash MD5:" + toHex(ds.getMessageDigest().digest()));

            while ((len = ds.read(bytes)) > 0) { // просто читает поток
            }
            System.out.println("hash MD5:" + toHex(ds.getMessageDigest().digest()));


        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
        }


// PushbackInputStream
        System.out.printf(FORMAT, "PushbackInputStream:");
        PushbackInputStream pin = null;
        in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
            pin = new PushbackInputStream(in, 10); // all bytes returned

            final StringBuilder sb = new StringBuilder();
            final byte[] bytes = new byte[100];

            BiPredicate<String, Integer> p = (s1, v1) -> String.CASE_INSENSITIVE_ORDER
                    .compare(new String(bytes, 0, v1), s1.substring(1)) == 0;

            int len;
            int c;
            while ((c = pin.read()) > 0) {
                switch (c) {
                    default:
                        sb.append((char) c);
                        break;
                    case '/':
                        int c2 = pin.read();
                        int c3 = pin.read();
                        if (c2 == '*' && c3 == '*') {
                            sb.append("<!>");
                            break;
                        } else {
                            sb.append('/');
                            pin.unread(c2);
                            pin.unread(c3);
                        }
                        break;
                    case '*':
                        s = "* created:"; // case insensitive
                        len = pin.read(bytes, 0, s.length() - 1);
                        if (p.test(s, len)) {
                            sb.append(">> Developed by");
                            break;
                        } else {
                            sb.append(s.charAt(0));
                            pin.unread(bytes, 0, len);
                        }
                        break;
                }
            }
            System.out.printf("%s", sb);

        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
            IOUtils.close(pin);
        }

// ProgressMonitorInputStream
        System.out.printf(FORMAT, "ProgressMonitorInputStream:");
        ProgressMonitorInputStream pmin = null;
        DigestInputStream dis = null;
        in = null;
        try {
            URL url = new File(PATH+"result.txt").toURI().toURL();

            in = new BufferedInputStream(url.openStream(), 100);  // internal buffer
//            JFrame pFrame = ProgressFrame.setup();
//            pmin = new ProgressMonitorInputStream(pFrame, "Verifying Key", in);
            pmin = new ProgressMonitorInputStream(null, "Verifying Key", in);
            ProgressMonitor pm = pmin.getProgressMonitor();
            pm.setMaximum(in.available());
            pm.setMillisToPopup(10);
            MessageDigest ms = MessageDigest.getInstance("SHA-256");
            System.out.println("hash SHA256: " + toHex(ms.digest()));
            dis = new DigestInputStream(pmin, ms);
            try {
                int c;
                while ((c = dis.read())!= -1) {
                    Thread.sleep(50,10);
                    System.out.printf("%c",c);
                }
            } catch (InterruptedIOException e) {
                System.out.printf("%nCancel pressed...%n");
            }
            byte[] hash = ms.digest();
            System.out.println("hash SHA256: " + toHex(hash));


        } catch (IOException | NoSuchAlgorithmException |InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
        }


// DecoratorInputStream
        System.out.printf(FORMAT, "DecoratorInputStream:");
        PrintStream ps = null;
        in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
            IOUtils.readout(in);
        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
        }
        System.exit(0);

// DecoratorInputStream
        System.out.printf(FORMAT, "DecoratorInputStream:");
        in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
            IOUtils.readout(in);
        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
        }
        System.exit(0);



//// DecoratorInputStream
//        System.out.printf(FORMAT, "DecoratorInputStream:");
//        in = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"), 100);  // internal buffer
//            util.IOUtils.readout(in);
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        } finally {
//            util.IOUtils.close(in);
//        }
//        System.exit(0);

    }

    private static int[] toInt(byte[] b) {
        if (b == null) return null;
        return IntStream.range(0, b.length).map(v -> b[v] & 0xFF).toArray();
    }

    private static String toString(byte[] b) {
        if (b == null) return null;
        return IntStream.range(0, b.length).mapToObj(v -> (b[v] & 0xFF) + " ").collect(Collectors.joining());
    }

    private static String toHex(byte[] b) {
        if (b == null) return null;
        return IntStream.range(0, b.length)
                .mapToObj(v -> String.format("%02X ", b[v] & 0xFF))
                .collect(Collectors.joining());
    }

    private static class ProgressFrame extends  JFrame {
        private ProgressMonitor pm;
        private JTextArea jTextArea;


        public ProgressFrame(ProgressMonitor pm) throws HeadlessException {
            super("Progress Monitor Frame");
            this.pm = pm;
            this.jTextArea = new JTextArea();

            setLayout(new BorderLayout());
            add(jTextArea,BorderLayout.CENTER);
        }

        private void append(String s) {
            jTextArea.append(s);
        }

        private static JFrame setup(){
            JFrame jFrame = new ProgressFrame(null);
            jFrame.setSize(800, 600);
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
            return jFrame;
        }


    }

}
