package io;

import io.cypher.CypherUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import static nio.Main01.FORMAT;
import static nio.Main01.PATH;

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
            list.add(new FileInputStream(PATH+"result.txt")); // does not support mark()
//            Enumeration<InputStream> en = new IOUtils.Enumerator<>(list.iterator());
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
            bs2 = new BufferedInputStream(new FileInputStream(PATH+"result.txt"), 100);  // internal buffer

            IOUtils.checkMarkSequence(bs, 75);       // supports mark()
            System.out.println("===================");
            IOUtils.readout(bs);                       // up to the end
            System.out.println("===================");
            IOUtils.readout(bs2);                       // up to the end

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(list);
            IOUtils.closeStream(ss);
            IOUtils.closeStream(bs);
            IOUtils.closeStream(bs2);
        }

// DataInputStream
        System.out.printf(FORMAT, "DataInputStream:");
        DataInputStream dIn = null;
        DataOutputStream dOut = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        BufferedReader br = null;               // readline support
        try {
            out = new BufferedOutputStream(new FileOutputStream(PATH+"data.txt"));
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

            in = new BufferedInputStream(new FileInputStream(PATH+"data.txt"));
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
            in = new BufferedInputStream(new FileInputStream(PATH+"result.txt"));
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
            IOUtils.closeStream(dIn);
            IOUtils.closeStream(dOut);
            IOUtils.closeStream(br);
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }

// CipherInputStream
        System.out.printf(FORMAT, "CipherInputStream:");
        CypherUtils.main(args);

//

        System.out.printf(FORMAT, "LineNumberInputStream >> LineNumberReader:");
        in = null;
        LineNumberReader lr = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH+"result.txt"), 100);  // internal buffer
            lr = new LineNumberReader(new InputStreamReader(in));  // translate InputStream >> Reader >> LinNumberReader
            while ((s = lr.readLine()) != null) {
                System.out.println(lr.getLineNumber() + ":" + s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(lr);
        }

// CheckedInputStream
        System.out.printf(FORMAT, "CheckedInputStream:");
        in = null;
        CheckedInputStream cin = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PATH+"result.txt"), 100);  // internal buffer
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
            IOUtils.closeStream(in);
            IOUtils.closeStream(cin);
        }
        System.exit(0);


    }


}
