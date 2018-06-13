package nio2.files;

import util.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 11-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainFileUtils {
    private static final int RECORD_LEN = 25;

    public static FileAttribute<List<AclEntry>> getFileAttribute(final Path path) {
        return new FileAttribute<>() {
            @Override
            public String name() {
                return "acl:acl";
            }

            @Override
            public List<AclEntry> value() {
                try {
                    final List<AclEntry> list = new ArrayList<>();
                    for (Object o : (List) Files.getAttribute(path, "acl:acl")) {
                        AclEntry aclEntry = (AclEntry) o;
                        list.add(aclEntry);
                    }
                    return list;
                } catch (IOException | UnsupportedOperationException e) {
                    System.out.printf("Exception:%s%n", e);
                }
                return null;
            }
        };
    }

    public static FileAttribute<Set<PosixFilePermission>> getPosixAttributeChecked(Path path) {
        try {
            @SuppressWarnings("unchecked")
            Set<PosixFilePermission> setPerms = (Set<PosixFilePermission>)
                    Files.getAttribute(path, "posix: permissions");

            return PosixFilePermissions.asFileAttribute(setPerms);


        } catch (IOException | UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n", e);
        }
        return null;
    }

    public static FileAttribute<Set<PosixFilePermission>> getPosixAttribute(String s) {
        try {
            Set<PosixFilePermission> setPerms = PosixFilePermissions.fromString(s);
            return PosixFilePermissions.asFileAttribute(setPerms);
        } catch (UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n", e);
        }
        return null;
    }

    public static FileAttribute<Set<PosixFilePermission>> getPosixAttribute(Path path) {
        try {
            Set<PosixFilePermission> setPerms = new HashSet<>();
            for (Object o : (Set) Files.getAttribute(path, "posix: permissions")) {
                if (o instanceof PosixFilePermission) {
                    setPerms.add((PosixFilePermission) o);
                }
            }
            return PosixFilePermissions.asFileAttribute(setPerms);
        } catch (IOException | UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n", e);
        }
        return null;
    }

    public static void setAttributes(Path path, boolean... attributes) throws IOException {
        DosFileAttributeView dv = Files.getFileAttributeView(path, DosFileAttributeView.class);
        DosFileAttributes da = dv.readAttributes();
        if (attributes.length > 0) {
            dv.setReadOnly(attributes[0]);
        }
        if (attributes.length > 1) {
            dv.setHidden(attributes[1]);
        }
        if (attributes.length > 2) {
            dv.setSystem(attributes[2]);
        }
        if (attributes.length > 3) {
            dv.setArchive(attributes[3]);
        }

    }

    public static void setAttributes(Path path, boolean read, boolean hide, boolean sys, boolean arc) throws IOException {
        DosFileAttributeView dv = Files.getFileAttributeView(path, DosFileAttributeView.class);
        dv.setReadOnly(read);
        dv.setHidden(hide);
        dv.setSystem(sys);
        dv.setArchive(arc);
    }

    public static void outAttributes(Path path, String message) throws IOException {
        DosFileAttributeView dv = Files.getFileAttributeView(path, DosFileAttributeView.class);
        DosFileAttributes da = dv.readAttributes();
        System.out.printf("%-7s: %s  read:%-5b hidden:%-5b system:%-5b archive:%-5b%n", message, path,
                da.isReadOnly(), da.isHidden(), da.isSystem(), da.isArchive());
    }

    public static String toTime(FileTime fileTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        return String.format("%1$tD %1$tT", dateTime);
    }

    public static void outAllAttributes(Path path, String message) throws IOException {
        DosFileAttributeView dv = Files.getFileAttributeView(path, DosFileAttributeView.class);
        DosFileAttributes da = dv.readAttributes();


        System.out.printf("%-8s: %-25s  %n", message, path);
        System.out.printf("        :read:%-5b  hidden:%-5b  system:%-5b  archive:%-5b%n",
                da.isReadOnly(), da.isHidden(), da.isSystem(), da.isArchive());
        System.out.printf("        :modified:%s%n", toTime(da.lastModifiedTime()));
        System.out.printf("        :access  :%s%n", toTime(da.lastAccessTime()));
        System.out.printf("        :create  :%s%n", toTime(da.creationTime()));

    }

    public static int outSeekableChannel(SeekableByteChannel sc, Charset charset) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(RECORD_LEN);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int len;
        while ((len = sc.read(b)) > 0) {
            b.flip();                               // ограничивает реальным числом байт
            bout.write(b.array(), 0, len);
            b.clear();
        }
        b = ByteBuffer.wrap(bout.toByteArray());
        System.out.printf("%s", charset.decode(b));

        return b.array().length;
    }

    private static class UserInt {
        int value;

        private UserInt(int value) {
            this.value = value;
        }

        public static UserInt newInstance() {
            return new UserInt(0);
        }

        public int increment() {
            return ++value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static void outStream(Stream<Path> stream) {
        final UserInt value = UserInt.newInstance();
        StringBuilder sb = new StringBuilder();
        stream.forEach(p -> {
            sb.append(String.format("%-25s ", p.getFileName()));
            int i = value.increment();
            if (i % 5 == 0) sb.append(String.format("%n"));
        });
        if (value.getValue() % 5 != 0) sb.append(String.format("%n"));

        System.out.printf("%s", sb.toString());
    }

    public static void outStream(DirectoryStream<Path> ds) {
        int i = 0;
        Formatter f = new Formatter(Locale.ENGLISH);
        for (Path p : ds) {
            f.format("%-25s ", p.getFileName());
            i++;
            if (i % 5 == 0) f.format("%n");
        }
        if (i % 5 != 0) f.format("%n");
        f.flush();
        System.out.printf("%s", f.toString());
    }

    public static void outPath(Path path) throws IOException {
        outPath(path, StandardCharsets.UTF_8);
    }

    public static void outPath(Path path, Charset charset) throws IOException {

        FileInputStream in = new FileInputStream(path.toAbsolutePath().toString());
        BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
        Formatter f = new Formatter(Locale.ENGLISH);
        try {
            String s;
            while ((s = br.readLine()) != null) {
                f.format("%s%n", s);
            }
            f.flush();
            System.out.printf("%s", f.toString());
        } finally {
            IOUtils.close(br, in, f);
        }
    }

    public static void outToChannel(Path path) throws IOException {
        outToChannel(path, StandardCharsets.UTF_8);
    }

    public static void outToChannel(Path path, Charset charset) throws IOException {
        FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WritableByteChannel wc = Channels.newChannel(out);
        Formatter f = new Formatter(Locale.ENGLISH);
        try {
            fc.transferTo(0, fc.size(), wc);
            f.format("%s", out.toString(charset));
            f.flush();
            System.out.printf("%s", f.toString());
        } finally {
            IOUtils.close(fc, wc, f, out);
        }
    }


    public static void outChannel(Path path, Charset charset) throws IOException {
        FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        BufferedOutputStream bout = new BufferedOutputStream(out);// it's not necessary
        WritableByteChannel wc = Channels.newChannel(out);          // channel is buffered by ByteBuffer
        ByteBuffer b = ByteBuffer.allocate(8);
        Formatter f = new Formatter(Locale.ENGLISH);
        try {
            while (fc.read(b) > 0) {
                b.flip();
                wc.write(b);
                b.compact();  // clear only data that was read
            }
            out.flush();
//*****************************
//            byte[] bytes = out.toByteArray();
//            String s = new String(bytes, charset);
//            f.format("%s%n", s);
//*****************************
            f.format("%s", out.toString(charset));
            f.flush();
            System.out.printf("%s", f.toString());
        } finally {
            IOUtils.close(fc, wc, f, out);
        }
    }

    public static void outFolder(Path path) throws IOException {
        DirectoryStream<Path> ds = null;
        if (!Files.exists(path)) return;
        try {
            ds = Files.newDirectoryStream(path);
            for (Path p : ds) {
                System.out.printf("%s%n", p);
            }
        } finally {
            IOUtils.close(ds);
        }
    }

    public static void deleteFolder(Path path) throws IOException {
        DirectoryStream<Path> ds = null;
        if (!Files.exists(path)) return;
        try {
            ds = Files.newDirectoryStream(path, "*");  // поток ЗАКРЫТЬ ОБЯЗАТЕЛЬНО
            for (Path p : ds)
                Files.deleteIfExists(p);
            Files.deleteIfExists(path);
        } finally {
            IOUtils.close(ds);
        }
    }

    public static void deleteFolderGlobe(Path path, String globe) throws IOException {
        DirectoryStream<Path> ds = null;
        if (!Files.exists(path)) return;
        DirectoryStream.Filter<Path> filter = p -> FileSystems.getDefault()
                .getPathMatcher("glob:" + globe)
                .matches(p.getFileName());
        try {
            ds = Files.newDirectoryStream(path, filter);  // поток ЗАКРЫТЬ ОБЯЗАТЕЛЬНО
            for (Path p : ds)
                Files.deleteIfExists(p);
            Files.deleteIfExists(path);
        } finally {
            IOUtils.close(ds);
        }
    }

    public static void deleteFolderRegex(Path path, String regex) throws IOException {
        DirectoryStream<Path> ds = null;
        if (!Files.exists(path)) return;
        DirectoryStream.Filter<Path> fr = p -> FileSystems.getDefault()
                .getPathMatcher("regex:" + regex)
                .matches(p.getFileName());
        try {
            ds = Files.newDirectoryStream(path, fr);  // поток ЗАКРЫТЬ ОБЯЗАТЕЛЬНО
            for (Path p : ds)
                Files.deleteIfExists(p);
            Files.deleteIfExists(path);
        } finally {
            IOUtils.close(ds);
        }
    }

}
