package nio2;

import nio2.files.MainFileUtils;
import util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03F {
    public static void main(String[] args) {

        System.out.printf(FORMAT, "Random Access File:");
        Path path = Paths.get(".", "data", "nio");
        Path pathC = Paths.get(path.toString(), "result.txt");
        Path pathD = Paths.get(path.toString(), "result_u.txt");
        Path pathE = Paths.get(path.toString(), "result_channel.txt");
        Path pathR;

        FileChannel fc = null;
        SeekableByteChannel sc = null;
        OpenOption[] options = new OpenOption[]{
                StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.SYNC
        };
        List<OpenOption> listOptions = Arrays.stream(options).collect(Collectors.toList());
        FileAttribute<List<AclEntry>> fileAttr = null;
        FileAttribute<Set<PosixFilePermission>> posixAttr = null;
        try {
            final int RECORD_LEN = 25;
// delete and copy file
            Files.deleteIfExists(pathE);
            Files.copy(pathD, pathE, StandardCopyOption.REPLACE_EXISTING);
// write at position
            System.out.printf(FORMAT, "Seekable Channel Write at position:");
            fc = FileChannel.open(pathE, options);
            sc = fc.position(RECORD_LEN * 3);                 // get SeekableByteChannel
            ByteBuffer b = ByteBuffer.wrap("<John Doe>".getBytes());
            System.out.printf("b:%d%n", b.array().length);
            sc.write(b);
// read at position
            sc.position(0);
            b = ByteBuffer.allocate(RECORD_LEN);
            System.out.printf("b:%d%n", b.array().length);
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            int len;
            while ((len = sc.read(b)) > 0) {
                b.flip();                               // ограничивает реальным числом байт
                outByteStream.write(b.array(), 0, len);
                b.clear();
            }
            b = ByteBuffer.wrap(outByteStream.toByteArray());
            System.out.printf("b:%d%n", b.array().length);
            System.out.printf("%s", StandardCharsets.UTF_8.decode(b));

            sc.close();

// write at position with attributes
            System.out.printf(FORMAT, "newByteChannel() EnumSet and FileAttribute Read/Write at position:");

            FileAttribute<List<AclEntry>> fAttr = MainFileUtils.getFileAttribute(pathE);
            fc = FileChannel.open(pathE, new HashSet<>(listOptions), fAttr);
            sc = fc.position(RECORD_LEN * 4);                 // get SeekableByteChannel
            b = ByteBuffer.wrap("Super John Link".getBytes());
            System.out.printf("b:%d%n", b.array().length);
            sc.write(b);
            sc.close();


// read at position with attributes
            sc = Files.newByteChannel(pathE, EnumSet.of(StandardOpenOption.READ), fAttr); // get Seekable channel
            sc.position(0);
            len = MainFileUtils.outSeekableChannel(sc, StandardCharsets.UTF_8);
            System.out.printf("buffer length:%d%n", len);
            sc.close();

// read at pos to ByteArrayOutputStream
            System.out.printf(FORMAT, "newByteChannel() EnumSet Read at position:");
            sc = Files.newByteChannel(pathE, EnumSet.of(StandardOpenOption.READ)); // get Seekable channel
            sc = sc.position(RECORD_LEN);
            len = MainFileUtils.outSeekableChannel(sc, StandardCharsets.UTF_8);
            System.out.printf("buffer length:%d%n", len);
            sc.close();

// read at pos 0 to ByteBuffer(sc.size)
            System.out.printf(FORMAT, "newByteChannel() EnumSet Truncate and Read All to ByteBuffer at position:");
            sc = Files.newByteChannel(pathE, StandardOpenOption.READ, StandardOpenOption.WRITE); // get Seekable channel
            sc.truncate(125);
            sc.position(0);
            b = ByteBuffer.allocate((int) sc.size());
            while ((len = sc.read(b)) > 0) {
                System.out.printf("%s", new String(b.array(), 0, len, StandardCharsets.UTF_8));
                b.clear();
            }
            System.out.printf("%n");
            len = b.array().length;
            System.out.printf("buffer length:%d%n", len);
            sc.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fc, sc);
        }

        System.out.printf(FORMAT, "Creating Directories:");
        path = Paths.get(".", "data", "nio");
        pathC = Paths.get(path.toString(), "result.txt");
        pathD = Paths.get(path.toString(), "dir");
        pathE = Paths.get(path.toString(), "folder");

        boolean result = false;
        DosFileAttributeView dv = null;
        DosFileAttributes da = null;
        try {
            if (Files.exists(pathD)) {
                dv = Files.getFileAttributeView(pathD, DosFileAttributeView.class);
                dv.setReadOnly(false);
            }
            Files.deleteIfExists(pathD);
            Files.createDirectory(pathD);
// file read only
            File folder = pathD.toFile();
            result = folder.setReadOnly();
            System.out.printf("dir  readonly: %-25s exists:%b readOnly:%b%n", folder.toPath(), folder.exists(), result);
            File file = pathC.toFile();
            result = file.setReadOnly();
            System.out.printf("file readonly: %-25s exists:%b readOnly:%b%n", file.toPath(), file.exists(), result);
            System.out.printf(FORMAT, "Creating Directories Attributes:");


// attributes read only
            dv = Files.getFileAttributeView(pathD, DosFileAttributeView.class);
            da = Files.readAttributes(pathD, DosFileAttributes.class);

            System.out.printf("default: %s  read:%-5b hidden:%-5b system:%-5b archive:%-5b%n", folder,
                    da.isReadOnly(), da.isHidden(), da.isSystem(), da.isArchive());

            dv.setReadOnly(true);
            dv.setSystem(true);
            dv.setHidden(true);
            dv.setArchive(true);
            MainFileUtils.outAttributes(pathD, "set");
            MainFileUtils.setAttributes(pathD, true, false, false, false);
            MainFileUtils.outAttributes(pathD, "back");
// attributes
            fileAttr = MainFileUtils.getFileAttribute(path);
            Files.deleteIfExists(pathE);
            Files.createDirectory(pathE, fileAttr);
//posix
            System.out.printf(FORMAT, "Creating Directories Posix:");
            try {
                Path pathF = Paths.get(path.toString(), "posix");
                Files.deleteIfExists(pathF);
                posixAttr = MainFileUtils.getPosixAttribute("rw-------");
                Files.createDirectory(pathF, posixAttr);
            } catch (UnsupportedOperationException e) {
                System.out.printf("Exception:%s%n", e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            IOUtils.close(fc, sc);
        }

        System.out.printf(FORMAT, "Creating Temporary Directories:");
        path = Paths.get(".", "data", "nio");
        pathC = Paths.get(path.toString(), "result.txt");
        pathD = Paths.get(path.toString(), "temp");

        result = false;
        String prefix = "temp";
        try {
            if (Files.exists(pathD)) {
                File[] files = pathD.toFile().listFiles();
                if (files != null && files.length > 0) {

                    result = Files.list(pathD).map(p -> p.toFile().delete()).reduce((b1, b2) -> b1 & b2)
                            .orElse(false);
                    if (!result) {
// with cause
                        Throwable t = new Throwable();
                        t.setStackTrace(Thread.currentThread().getStackTrace());
//                    throw new IOException("Can't delete temp directory",t);
// with direct exception
                        IOException e = new IOException("Can't delete temp directory");
                        e.setStackTrace(Thread.currentThread().getStackTrace());
                        throw e;
                    }
                }
            } else {
                Files.createDirectory(pathD);
            }

// temp dir
            Files.createTempDirectory(pathD, prefix);  // simple
            Files.createTempDirectory(pathD, prefix);
            Files.createTempDirectory(pathD, prefix);
// delete on Exit
            File[] files = pathD.toFile().listFiles();
            if (files == null) throw new IOException();
            for (File file : files) {
                file.deleteOnExit();
            }

// attributes
            List<Path> listTmp = new ArrayList<>();
            fileAttr = MainFileUtils.getFileAttribute(path);
            pathR = Files.createTempDirectory(pathD, prefix, fileAttr);  // with attributes
            listTmp.add(pathR);
            pathR = Files.createTempDirectory(pathD, prefix);
            listTmp.add(pathR);

            Thread hook = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (Path path : listTmp) {
                            Files.deleteIfExists(path);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            Runtime.getRuntime().addShutdownHook(hook);
            Files.list(pathD).forEach(p -> System.out.printf("%s%n", p.getFileName()));

//posix
            System.out.printf(FORMAT, "Creating Temp Directories Posix:");
            try {
                List<Path> list = new ArrayList<>();
                posixAttr = MainFileUtils.getPosixAttribute("rw-------");
                pathR = Files.createTempDirectory(pathD, prefix, posixAttr);
                list.add(pathR);
                pathR = Files.createTempDirectory(pathD, prefix, posixAttr);
                list.add(pathR);

                for (Path path2 : list) {
                    path.toFile().deleteOnExit();
                }
            } catch (UnsupportedOperationException e) {
                System.out.printf("Exception:%s%n", e);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            IOUtils.close(fc, sc);
        }

// list dir
        System.out.printf(FORMAT, "Listing Directory list().stream:");
        pathD = Paths.get(".", "data", "nio");
        pathE = Paths.get(".", "data", "stream");
        Stream<Path> stream = null;
        DirectoryStream<Path> ds = null;
        final AtomicInteger aInt = new AtomicInteger(0);
        try {
            stream = Files.list(pathD);  // поток списка файлов
            aInt.set(0);
            stream.forEach(p -> {
                System.out.printf("%-25s ", p.getFileName());
                int i = aInt.incrementAndGet();
                if ((i + 1) % 5 == 0) System.out.printf("%n");
            });
            System.out.printf("%n");

            System.out.printf(FORMAT, "Listing new DirectoryStream().iterator:");
            ds = Files.newDirectoryStream(pathE);
            int count = 0;
            for (Path p : ds) {
                System.out.printf("%-25s ", p.getFileName());
                count++;
                if (count % 5 == 0) System.out.printf("%n");
            }
            System.out.printf("%n");

            System.out.printf(FORMAT, "Listing new DirectoryStream() Filter Regex:");
            String regex = "[rsb].*$";
            DirectoryStream.Filter<Path> f = p -> Pattern.compile(regex).matcher(p.getFileName().toString()).matches();
            ds = Files.newDirectoryStream(pathE, f);
            MainFileUtils.outStream(ds);
            ds.close();

            System.out.printf(FORMAT, "Listing new DirectoryStream() Filter PatternMatcher Regex:");

            DirectoryStream.Filter<Path> fr = p ->
                    FileSystems.getDefault().getPathMatcher("regex:" + regex).matches(p.getFileName());

            DirectoryStream<Path> dsR = Files.newDirectoryStream(pathE,fr);
            MainFileUtils.outStream(dsR);
            dsR.close();

            System.out.printf(FORMAT, "Listing new DirectoryStream() Filter PatternMatcher Glob:");
            String glob = "{bu,re,st}*.*";
            DirectoryStream.Filter<Path> fg = p ->
                    FileSystems.getDefault().getPathMatcher("glob:" + glob).matches(p.getFileName());
            DirectoryStream<Path> dsG = Files.newDirectoryStream(pathE, fg);
            MainFileUtils.outStream(dsG);
            dsG.close();

            System.out.printf(FORMAT, "Listing new DirectoryStream() Filter(String):");
            String globStr = "{bu,re,st}*.*";
            DirectoryStream<Path> dsS = Files.newDirectoryStream(pathE, globStr);
            MainFileUtils.outStream(dsS);
            dsS.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ds);
        }
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
