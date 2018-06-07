package nio2;

import java.io.File;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01 {
    public static void main(String[] args) {

// FileSystem
        FileSystem fs = FileSystems.getDefault();

        System.out.printf(FORMAT, "FileSystem:");
        List<FileSystemProvider> list = FileSystemProvider.installedProviders();
        for (FileSystemProvider provider : list) {
            System.out.printf("%s scheme:%s%n", provider, provider.getScheme());
        }


// Files
        System.out.printf(FORMAT, "Path:");

        Path path = Paths.get(".", "data/nio/", "result.txt").toAbsolutePath();
        File toFile = path.toFile();
        Path toPath = toFile.toPath();

        URI uri = URI.create("file:///"+path.toString().replaceAll("\\\\","/"));
        Path pathURI = Paths.get(uri);

        System.out.printf("path:%s%n", path);
        System.out.printf("file:%s    exists:%b%n", toFile, toFile.exists());
        System.out.printf("path:%s    exists:%b%n", toPath, toPath.toFile().exists());
        System.out.printf("pathURI:%s exists:%b%n", pathURI, pathURI.toFile().exists());

        System.out.printf(FORMAT,"FileSystem");
        fs = FileSystems.getDefault();

        String regex = "(.*\\.)(.*)(@.*+)";
        Matcher m = Pattern.compile(regex).matcher(fs.toString());
        String name = "";
        if(m.matches()) {
            name = m.group(2);
        }
        System.out.printf("filesystem:%s separator:%s scheme:%s%n",name, fs.getSeparator(),
                fs.provider().getScheme());


        for (FileStore fileStore : fs.getFileStores()) {
            System.out.printf("store:%s%n",fileStore.name());
        }
        for (Path p : fs.getRootDirectories()) {
            System.out.printf("root:%s%n",p.toString());
        }

        System.out.printf(FORMAT,"FS getPath:");
        String sep = fs.getSeparator();


        Path pathX = fs.getPath(".","data"+sep+"nio"+sep,"result.txt").toAbsolutePath();
        Path pathY = fs.getPath(".","data","nio","result.txt").toAbsolutePath();
        System.out.printf("%s%n",pathX);
        System.out.printf("%s  %b%n",pathX,pathX.toFile().exists());
        System.out.printf("%s  %b%n",pathY,pathY.toFile().exists());
        System.out.printf("fs:%5.10s file:%s name:%s count:%d root:%s%n",
                pathX.getFileSystem(),   pathX.getFileName(),
                pathX.getName(0),pathX.getNameCount(), pathX.getRoot());

        System.out.printf("parent :%s%n", pathX.getParent());
        System.out.printf("subpath:%s%n", pathX.subpath(1,5));




//// Files
//        System.out.printf(FORMAT, "NIO2 Files:");
//        BufferedInputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"));
//            out = new BufferedOutputStream(new FileOutputStream(PATH + "result_out.txt"));
//            byte[] bytes = new byte[50];
//           int len;
//            while ((len = in.read(bytes)) > 0) {
//                out.write(bytes, 0, len);
//            }
//        } catch (IOException  e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(in, out);
//        }

//// Files
//        System.out.printf(FORMAT, "NIO2 Files:");
//        BufferedInputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"));
//            out = new BufferedOutputStream(new FileOutputStream(PATH + "result_out.txt"));
//            byte[] bytes = new byte[50];
//           int len;
//            while ((len = in.read(bytes)) > 0) {
//                out.write(bytes, 0, len);
//            }
//        } catch (IOException  e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(in, out);
//        }

//// Files
//        System.out.printf(FORMAT, "NIO2 Files:");
//        BufferedInputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"));
//            out = new BufferedOutputStream(new FileOutputStream(PATH + "result_out.txt"));
//            byte[] bytes = new byte[50];
//           int len;
//            while ((len = in.read(bytes)) > 0) {
//                out.write(bytes, 0, len);
//            }
//        } catch (IOException  e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(in, out);
//        }

//// Files
//        System.out.printf(FORMAT, "NIO2 Files:");
//        FileChannel fin = null;
//        FileChannel fout = null;
//        try {
//            fin = new FileInputStream(PATH + "result.txt").getChannel();
//            fout = new FileOutputStream(PATH + "result_fout.txt").getChannel();
//            while (fin.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    fout.write(b);
//                }
//                b.clear();
//            }

//// Files
//        System.out.printf(FORMAT, "NIO2 Files:");
//        BufferedInputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            in = new BufferedInputStream(new FileInputStream(PATH + "result.txt"));
//            out = new BufferedOutputStream(new FileOutputStream(PATH + "result_out.txt"));
//            byte[] bytes = new byte[50];
//           int len;
//            while ((len = in.read(bytes)) > 0) {
//                out.write(bytes, 0, len);
//            }
//        } catch (IOException  e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(in, out);
//        }


//// Files
//        System.out.printf(FORMAT, "NIO2 Files:");
//        ReadableByteChannel rin = null;
//        WritableByteChannel wout = null;
//        try {
//            rin = Channels.newChannel(System.in);
//            wout = Channels.newChannel(System.out);
//            System.out.printf("Type any txt('exit' or <Enter> to exit)%n");
//            Charset charset = Charset.defaultCharset();
//            ByteBuffer b = ByteBuffer.allocate(200);
//            while ( rin.read(b) != -1) {          // получили данные в буфер
//                b.flip();                       // отсекаем
//                while (b.hasRemaining()) {      // пишем в выходной поток
//                    wout.write(b);
//                }
//                b.rewind();
//                if(b.remaining()==1 ||charset.decode(b).toString().contains("exit")){
//                    b.clear();
//                    break;
//                }
//                b.clear();
//            }
//        } catch (IOException  e) {
//            e.printStackTrace();
//        } finally {
//            IOUtils.close(in, out,fin,fout,rin);
//        }

    }
}
