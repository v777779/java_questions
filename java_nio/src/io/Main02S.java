package io;


import io.cypher.CypherUtils;
import util.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02S {
    private static final Lock sLock = new ReentrantLock();




    private static class FilterStream extends FilterInputStream {
        protected FilterStream(InputStream in) {
            super(in);
        }
    }


    public static void main(String[] args) {
//IO
        System.out.printf(FORMAT, "InputStreams");
        System.out.println();
        System.out.printf(FORMAT, "FileInputStream");
        FileInputStream fs = null;
        FileInputStream fs2 = null;
        FileInputStream fs3 = null;
        StringJoiner sj = new StringJoiner("");
        try {
            fs = new FileInputStream(PATH+"result.txt");
            fs2 = new FileInputStream( new File(PATH+"result.txt"));

            System.out.println("fs len:" + fs.available());
            int len;
            byte[] bytes = new byte[100];


            while ((len = fs.read(bytes)) > 0) {
                sj.add(new String(bytes, 0, len, Charset.forName("utf-8")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fs != null) fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(sj);


        System.out.printf(FORMAT, "ByteArrayInputStream");
// does not require close()
        int c;
        ByteArrayInputStream bs = new ByteArrayInputStream(IOUtils.STRING.getBytes());
        ByteArrayInputStream bs2 = new ByteArrayInputStream(IOUtils.STRING.getBytes(), 5, 15);
        System.out.println("bs:" + bs.available());
        while ((c = bs.read()) != -1) {
            System.out.printf("%c", c);
        }
        System.out.println();


        System.out.printf(FORMAT, "SequenceInputStream");
// two streams one after another
        byte[] bytes = new byte[10];

        SequenceInputStream sIn = null;
        try {
            sIn = new SequenceInputStream(
                    new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8")),
                    new ByteArrayInputStream(IOUtils.STRING2.getBytes("utf-8")));

            sj = new StringJoiner("");
            int len;
            while ((len = sIn.read(bytes)) > 0) {
                sj.add(new String(bytes).substring(0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (sIn != null) {
                try {
                    sIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(sj);

        System.out.printf(FORMAT, "FilterInputStream");
// parent for decorators
// BufferedInputStream, CheckedInputStream, CipherInputStream, DataInputStream,
// DeflaterInputStream, DigestInputStream, InflaterInputStream, LineNumberInputStream,
// ProgressMonitorInputStream, PushbackInputStream
        FilterInputStream ft = null;
        try {
            ft = new FilterStream(new FileInputStream(PATH+"result.txt"));


            System.out.println("ft len:" + ft.available());

            bytes = new byte[100];
            sj = new StringJoiner("");
            int len;
            while ((len = ft.read(bytes)) > 0) {
                sj.add(new String(bytes, 0, len, Charset.forName("utf-8")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ft != null) ft.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(sj);


        System.out.printf(FORMAT, "StringReader");
        StringReader sR = null;
        try {
            char[] chars = new char[10];
            int len;
            sR = new StringReader(IOUtils.STRING);
            while ((len = sR.read(chars)) > 0) {
                System.out.print(new String(chars, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sR != null) sR.close();
        }
        System.out.println();

        System.out.printf(FORMAT, "PipedInputStream");
//        https://www.concretepage.com/java/java-pipedoutputstream-pipedinputstream-example
        final PipedOutputStream po = new PipedOutputStream();
        final PipedInputStream pi = new PipedInputStream();

        Runnable rOut = () -> {
            try {
                po.connect(pi);
                Thread.sleep(10);
                po.write(IOUtils.STRING.getBytes("utf-8"));
                po.flush();
                po.close();
                synchronized (sLock) {
                    sLock.wait();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            System.out.println("pipedOut: closed");

        };
        Runnable rIn = () -> {
            try {

                while (pi.available() == 0) { // awaiting
                    System.out.print(".");
                    Thread.sleep(1);
                }
                System.out.println();
                while (pi.available() > 0) {
                    System.out.printf("%c", pi.read());
                }
                System.out.println();

                synchronized (sLock) {
                    sLock.notifyAll();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    pi.close();
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

// List of InputStream to common InputStream via SequenceInputStream

        List<InputStream> ins = new ArrayList<>();
        try {
            ins.add(new FileInputStream(PATH+"result.txt"));
            ins.add(new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8")));
            ins.add(new ByteArrayInputStream(IOUtils.STRING2.getBytes("utf-8")));

            InputStream in = ins.stream().reduce(SequenceInputStream::new).orElse(null);
            if (in != null) {
                while ((c = in.read()) != -1) System.out.printf("%c", c);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                for (InputStream in : ins) {
                    if (in != null) in.close();  // close all resources
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

// FilterInputStream
        System.out.printf(FORMAT, "CipherInputStream:");
        CypherUtils.main(args);

// InputStream mark()
        System.out.printf(FORMAT, "InputStream mark():");
        InputStream in = null;
        try {
            System.out.printf(FORMAT, "ByteArrayInputStream mark():");
            in = new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8"));
            IOUtils.checkMark(in, false);

            System.out.printf(FORMAT, "ByteArrayInputStream mark():");
            in = new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8"));
            IOUtils.checkMark(in, true);

            System.out.printf(FORMAT, "BufferedInputStream mark():");
            in = new BufferedInputStream(new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8")));
            IOUtils.checkMark(in, false);

            System.out.printf(FORMAT, "BufferedInputStream mark():");
            in = new BufferedInputStream(new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8")));
            IOUtils.checkMark(in, true);

            System.out.printf(FORMAT, "BufferedInputStream mark() buffer size less than data length:");
            in = new BufferedInputStream(new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8")),10);
            IOUtils.checkMark(in, false);

            System.out.printf(FORMAT, "BufferedInputStream mark() with buffer lest than data length:");
            in = new BufferedInputStream(new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8")),10);
            IOUtils.checkMark(in, true);


            System.out.printf(FORMAT, "BufferedInputStream mark() buffer size less than data length:");
            in = new BufferedInputStream(new FileInputStream(PATH+"result.txt"),100);
            IOUtils.checkMark(in, false);

            System.out.printf(FORMAT, "BufferedInputStream mark() with buffer lest than data length:");
            in = new BufferedInputStream(new FileInputStream(PATH+"result.txt"),100);
            IOUtils.checkMark(in, true);


            System.out.printf(FORMAT, "FileInputStream mark():");
            in = new FileInputStream(PATH+"result.txt");
            IOUtils.checkMark(in, true);

            System.out.printf(FORMAT, "SequenceInputStream mark():");
            in = new SequenceInputStream(new ByteArrayInputStream(IOUtils.STRING.getBytes("utf-8")),
                    new ByteArrayInputStream(IOUtils.STRING2.getBytes("utf-8")));
            IOUtils.checkMark(in, false);

        } catch (IOException e) {
            e.printStackTrace();
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

}
