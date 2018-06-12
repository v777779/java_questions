package nio2.files;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
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

    public static void outAttributes(Path path, String prefix) throws IOException {
        DosFileAttributeView dv = Files.getFileAttributeView(path, DosFileAttributeView.class);
        DosFileAttributes da = dv.readAttributes();
        System.out.printf("%-7s: %s  read:%-5b hidden:%-5b system:%-5b archive:%-5b%n", prefix, path,
                da.isReadOnly(), da.isHidden(), da.isSystem(), da.isArchive());
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


}
