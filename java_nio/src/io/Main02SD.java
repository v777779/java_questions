package io;

import io.audio.AudioMP3;
import io.objstream.Base;
import io.objstream.BaseFactory;

import javax.sound.midi.spi.MidiDeviceProvider;
import javax.sound.midi.spi.MidiFileReader;
import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.midi.spi.SoundbankReader;
import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static nio.Main01.FORMAT;
import static nio.Main01.PATH;


/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02SD {


    public static void main(String[] args) {
        String s = "string value mark digital strong requirement";
        String s2 = "new matcher lesson case file value ";

//  ByteArrayInputStream
        System.out.printf(FORMAT, "ByteArrayInputStream");
        ByteArrayInputStream bs = new ByteArrayInputStream(s.getBytes());
        ByteArrayInputStream bs2 = new ByteArrayInputStream(s.getBytes(), 5, 27);

        System.out.println("bs:" + bs.available());
        IOUtils.readout(bs);
        IOUtils.readout(bs2);
        System.out.println();

//  FileInputStream
        System.out.printf(FORMAT, "FileInputStream");
        FileInputStream fs = null;
        FileInputStream fs2 = null;
        FileInputStream fs3 = null;
        try {
            fs = new FileInputStream(PATH+"result.txt");
            fs2 = new FileInputStream(fs.getFD());
            fs3 = new FileInputStream(new File(PATH+"result.txt"));

            IOUtils.readout(fs);
            IOUtils.readout(fs2, new byte[10]);  // по сути это тот же самый поток и он уже закрыт
            IOUtils.readout(fs3, new byte[25]);

// читаем попеременно
            fs = new FileInputStream(PATH+"result.txt");
            fs2 = new FileInputStream(fs.getFD());
            IOUtils.readout(fs, new byte[50], 50);            // читаем попеременно
            IOUtils.readout(fs2, new byte[50], 50);
            IOUtils.readout(fs, new byte[50]);                   // закрываем оба потока

            System.out.println("fs2:" + fs2.available());  // поток тоже закрыт
        } catch (IOException e) {
            System.out.println("IOException:" + e);
        } finally {
            IOUtils.closeStream(fs);
            IOUtils.closeStream(fs2);
            IOUtils.closeStream(fs3);

        }
//  SequenceInputStream
        System.out.printf(FORMAT, "SequenceInputStream");
        SequenceInputStream sIn = null;
        SequenceInputStream sIn2 = null;
        try {
            List<InputStream> list=new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                list.add(new ByteArrayInputStream(s.getBytes()));
            }

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

            sIn = new SequenceInputStream(en);
            sIn2 = new SequenceInputStream(sIn, new FileInputStream(PATH+"result.txt"));



            IOUtils.readout(sIn2);
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeStream(sIn);
            IOUtils.closeStream(sIn2);
        }

// ObjectInputStream
        System.out.printf(FORMAT, "ObjectInputStream:");
        ObjectInputStream sin = null;
        ObjectOutputStream sout = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            sout = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(PATH+"person.txt"), 100)
            );

            for (int i = 0; i < 10; i++) {
                sout.write(String.format("%s%n", s).getBytes());
            }
            sout.flush();
            sout.close();

            in = new BufferedInputStream(new FileInputStream(PATH+"person.txt"), 100);  // internal buffer
            sin = new ObjectInputStream(in);  // internal buffer
            int c;
            while ((c = sin.read()) != -1) {
                System.out.printf("%c", c);
            }
            sin.close();
            in.close();  // underlying stream close

// person and cat
            sout = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(PATH+"person.dat"), 100)
            );
            for (Base p : BaseFactory.newList(10)) {
                sout.writeObject(p);
            }
            sout.flush();
            sout.close();

            in = new BufferedInputStream(new FileInputStream(PATH+"person.dat"), 100);  // internal buffer
            sin = new ObjectInputStream(in);  // internal buffer

            List<Base> listB = new ArrayList<>();
            Base b;
            try {
                while ((b = (Base) sin.readObject()) != null) {
                    listB.add(b);
                }
            } catch (EOFException e) {
                // eof reached
            } finally {
                sin.close();
                in.close();
            }

            listB.stream().sorted(
                    Comparator.comparing(v -> v.getClass().getName())
                            .thenComparing(v -> ((Base) v).getPrice())
                            .thenComparing(v -> ((Base) v).getName())
                            .thenComparing(v -> ((Base) v).getHeight()))
                    .forEach(p -> System.out.printf("%s%n", p));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in);
        }


// AudioInputStream
        System.out.printf(FORMAT, "AudioInputStream Wav:");
        in = null;
        AudioInputStream as = null;
        SourceDataLine line = null;
        try {
            as = AudioSystem.getAudioInputStream(new File(PATH+"short.wav"));
            AudioFormat fmt = as.getFormat();

            long frames = as.getFrameLength();
            double duration = (double) frames / fmt.getFrameRate();
            System.out.printf("audio: %.2f sec%n", duration);

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
            line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(fmt);
            line.start();
            byte[] bytes = new byte[4096];
            int n;
            int count = 0;
            while ((n = as.read(bytes)) != -1) {
                line.write(bytes, 0, n);
                System.out.print(".");
                if(count++ > 64) break;
            }
            line.drain();
            line.stop();
            System.out.printf(FORMAT, "AudioInputStream MP3:");
            AudioMP3.testPlay(PATH+"short.mp3");

        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();

        } finally {
            IOUtils.closeStream(in);
            if (line != null) line.close();
        }

// audio providers
        System.out.printf(FORMAT, "Audio Providers:");
        List<Class> listC = new ArrayList<>();
        listC.add(MixerProvider.class);
        listC.add(FormatConversionProvider.class);
        listC.add(AudioFileReader.class);
        listC.add(AudioFileWriter.class);
        listC.add(MidiDeviceProvider.class);
        listC.add(SoundbankReader.class);
        listC.add(MidiFileWriter.class);
        listC.add(MidiFileReader.class);

        for (Class c : listC) {
            System.out.printf(FORMAT, "Providers for " + c.getName() + ":");
//            List<?> listP = JDK13Services.getProviders(c);
//            listP.forEach(v -> System.out.printf("%s%n", v.getClass().getName()));

        }

//        Class var0 = AudioFileReader.class;
//        if (!MixerProvider.class.equals(var0) &&
//                !FormatConversionProvider.class.equals(var0) &&
//                !AudioFileReader.class.equals(var0) &&
//                !AudioFileWriter.class.equals(var0) &&
//                !MidiDeviceProvider.class.equals(var0) &&
//                !SoundbankReader.class.equals(var0) &&
//                !MidiFileWriter.class.equals(var0) &&
//                !MidiFileReader.class.equals(var0)) {
//            System.out.println("class:"+var0.getName());
//        }


//        Provider p[] = Security.getProviders();
//        for (Provider provider : p) {
//            for (Object o : provider.keySet()) {
//                System.out.println(o+" : "+provider.get(o));
//            }
//        }

//  PipedInputStream
        System.out.printf(FORMAT, "PipedInputStream");
//        https://www.concretepage.com/java/java-pipedoutputstream-pipedinputstream-example
        final PipedOutputStream po = new PipedOutputStream();
        final PipedInputStream pi = new PipedInputStream();
        final ReentrantLock sLock = new ReentrantLock();

        Runnable rOut = () -> {
            try {
                po.connect(pi);
                po.write(s.getBytes());
                po.flush();
                po.close();
                synchronized (sLock) {
                    sLock.wait();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }finally {
                IOUtils.closeStream(po);
            }
        };
        Runnable rIn = () -> {
            try {
                while (pi.available() == 0) {};
                byte[] bytes = new byte[10];
                int len;
                while ((len = pi.read(bytes)) > 0) {
                    System.out.printf("%s", new String(bytes,0,len));
                }
                System.out.println();
                synchronized (sLock) {
                    sLock.notifyAll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeStream(pi);
            }
        };

        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.execute(rOut);
        exec.execute(rIn);
        exec.shutdown();

// StringBufferInputStream
        System.out.printf(FORMAT, "StringBufferInputStream >> StringReader:");
        StringReader sr = null;
        try {
            String string = new String(Files.readAllBytes(Paths.get(PATH+"result.txt")), Charset.forName("utf-8"));
            sr = new StringReader(string);

            char[] chars = new char[10];
            int len;
            while ((len = sr.read(chars)) > 0) {
                System.out.print(new String(chars, 0, len));
            }
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in);
        }

    }


}
