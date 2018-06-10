package nio2;

import util.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.EnumSet;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03F {
    public static void main(String[] args) {
        System.out.printf(FORMAT, "Read Large Files :");
        Path path = Paths.get(".", "data", "nio");
        Path pathD = Paths.get(path.toString(), "result.txt");
        Path pathE = Paths.get(path.toString(), "result_channel.txt");

        BufferedReader br = null;
        BufferedWriter bw = null;
        InputStream in = null;
        OutputStream out = null;
        FileChannel fc = null;
        SeekableByteChannel sc = null;
        OpenOption[] options = new OpenOption[]{
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.SYNC
        };
        try {
            final int RECORD_LEN = 25;
            Files.deleteIfExists(pathE);
            Files.copy(pathD, pathE,StandardCopyOption.REPLACE_EXISTING);

            fc = FileChannel.open(pathD, options);
            sc = fc.position(RECORD_LEN * 2);                 // ret SeekableByteChannel
            ByteBuffer b = ByteBuffer.wrap("John Doe".getBytes());
            System.out.printf("b[]:%d%n", b.array().length);
            sc.write(b);
            b.clear();
            sc = Files.newByteChannel(pathD, EnumSet.of(StandardOpenOption.READ));
            sc = sc.position(RECORD_LEN);
            System.out.printf("b[]:%d%n", b.array().length);
            sc.read(b);
            System.out.printf("b[]:%d%n", b.array().length);
            sc.close();
            System.out.printf("%s%n", StandardCharsets.UTF_8.decode(b));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br, bw, in, out);
        }


//        System.out.printf(FORMAT, "Read Large Files :");
//        Path path = Paths.get(".", "data", "nio");
//        Path pathD = Paths.get(path.toString(), "result.txt");
//        Path pathE = Paths.get(path.toString(), "result_k.txt");
//
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            in = pathD.toUri().toURL().openStream();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(br, bw, in, out);
//        }
//

//        System.out.printf(FORMAT, "Read Large Files :");
//        Path path = Paths.get(".", "data", "nio");
//        Path pathD = Paths.get(path.toString(), "result.txt");
//        Path pathE = Paths.get(path.toString(), "result_k.txt");
//
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            in = pathD.toUri().toURL().openStream();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(br, bw, in, out);
//        }
//

//        System.out.printf(FORMAT, "Read Large Files :");
//        Path path = Paths.get(".", "data", "nio");
//        Path pathD = Paths.get(path.toString(), "result.txt");
//        Path pathE = Paths.get(path.toString(), "result_k.txt");
//
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            in = pathD.toUri().toURL().openStream();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(br, bw, in, out);
//        }
//

//        System.out.printf(FORMAT, "Read Large Files :");
//        Path path = Paths.get(".", "data", "nio");
//        Path pathD = Paths.get(path.toString(), "result.txt");
//        Path pathE = Paths.get(path.toString(), "result_k.txt");
//
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            in = pathD.toUri().toURL().openStream();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(br, bw, in, out);
//        }
//


//        System.out.printf(FORMAT, "Read Large Files :");
//        Path path = Paths.get(".", "data", "nio");
//        Path pathD = Paths.get(path.toString(), "result.txt");
//        Path pathE = Paths.get(path.toString(), "result_k.txt");
//
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            in = pathD.toUri().toURL().openStream();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(br, bw, in, out);
//        }
//

    }
}
