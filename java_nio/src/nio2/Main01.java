package nio2;

import util.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import static util.IOUtils.FORMAT;
import static util.IOUtils.PATH;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {
    public static void main(String[] args) {


// Files
        System.out.printf(FORMAT, "NIO2 Files:");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        FileChannel fin = null;
        FileChannel fout = null;
        ReadableByteChannel rin = null;
        WritableByteChannel wout = null;

        try {
            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"));
            out = new BufferedOutputStream(new FileOutputStream(PATH + "result_out.txt"));


            fin = new FileInputStream(PATH + "result.txt").getChannel();
            fout = new FileOutputStream(PATH + "result_fout.txt").getChannel();

            rin = Channels.newChannel(System.in);
            wout = Channels.newChannel(System.out);


// simple channels
            System.out.printf("Type any txt('exit' or <Enter> to exit)%n");
            Charset charset = Charset.defaultCharset();
            ByteBuffer b = ByteBuffer.allocate(200);

            while ( rin.read(b) != -1) {          // получили данные в буфер
                b.flip();                       // отсекаем
                while (b.hasRemaining()) {      // пишем в выходной поток
                    wout.write(b);
                }
                b.rewind();
                if(b.remaining()==1 ||charset.decode(b).toString().contains("exit")){
                    b.clear();
                    break;
                }
                b.clear();
            }
// file channels
            while (fin.read(b) != -1) {          // получили данные в буфер
                b.flip();                       // отсекаем
                while (b.hasRemaining()) {      // пишем в выходной поток
                    fout.write(b);
                }
                b.clear();
            }
// streams
            byte[] bytes = new byte[50];
           int len;
            while ((len = in.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }

        } catch (IOException  e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in, out,fin,fout,rin);
        }

    }
}
