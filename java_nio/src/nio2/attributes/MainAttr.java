package nio2.attributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainAttr {
// from AbstractBasicFileAttributeView class
    private static final String SIZE_NAME = "size";
    private static final String CREATION_TIME_NAME = "creationTime";
    private static final String LAST_ACCESS_TIME_NAME = "lastAccessTime";
    private static final String LAST_MODIFIED_TIME_NAME = "lastModifiedTime";
    private static final String FILE_KEY_NAME = "fileKey";
    private static final String IS_DIRECTORY_NAME = "isDirectory";
    private static final String IS_REGULAR_FILE_NAME = "isRegularFile";
    private static final String IS_SYMBOLIC_LINK_NAME = "isSymbolicLink";
    private static final String IS_OTHER_NAME = "isOther";

//    public void setAttribute(String attribute, Object value)
//            throws IOException
//    {
//        if (attribute.equals(LAST_MODIFIED_TIME_NAME)) {
//            setTimes((FileTime)value, null, null);
//            return;
//        }
//        if (attribute.equals(LAST_ACCESS_TIME_NAME)) {
//            setTimes(null, (FileTime)value, null);
//            return;
//        }
//        if (attribute.equals(CREATION_TIME_NAME)) {
//            setTimes(null, null, (FileTime)value);
//            return;
//        }
//        throw new IllegalArgumentException("'" + name() + ":" +
//                attribute + "' not recognized");
//    }

//DosFileAttributeView
    public static final String READONLY_NAME = "readonly";
    public static final String ARCHIVE_NAME = "archive";
    public static final String SYSTEM_NAME = "system";
    public static final String HIDDEN_NAME = "hidden";
    public static final String ATTRIBUTES_NAME = "attributes";


    public static void showAttributes(Path path) throws IOException {
        BasicFileAttributes ba = Files.readAttributes(path, BasicFileAttributes.class,
                LinkOption.NOFOLLOW_LINKS);

        FileTime fileCTime = ba.creationTime();
        Object fileKey = ba.fileKey();
        boolean isDir = ba.isDirectory();
        boolean isOther = ba.isOther();
        boolean isRegular = ba.isRegularFile();
        boolean isSymLink = ba.isSymbolicLink();
        FileTime fileATime = ba.lastAccessTime();
        FileTime fileMTime = ba.lastModifiedTime();
        long size = ba.size();

        LocalDateTime localCTime = LocalDateTime.ofInstant(fileCTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime localATime = LocalDateTime.ofInstant(fileATime.toInstant(), ZoneId.systemDefault());
        LocalDateTime localMTime = LocalDateTime.ofInstant(fileMTime.toInstant(), ZoneId.systemDefault());

        System.out.printf("%n");
        System.out.printf("file            :%s%n", path);
        System.out.printf("creationTime    :%1$tD %1$tT%n", localCTime);
        System.out.printf("fileKey         :%s%n", fileKey);
        System.out.printf("isDirectory     :%b%n", isDir);
        System.out.printf("isOther         :%b%n", isOther);
        System.out.printf("isRegular       :%b%n", isRegular);
        System.out.printf("isSymbolinkLink :%b%n", isSymLink);
        System.out.printf("lastAccessTime  :%1$tD %1$tT%n", localATime);
        System.out.printf("lastModifiedTime:%1$tD %1$tT%n", localMTime);
        System.out.printf("size            :%d%n", size);

    }

    public static void showSet(Path path, Set<String> set) throws IOException{
        System.out.printf("%n");
        System.out.printf("path: %s%n", path);
        for (String attr : set) {
            Object value = Files.getAttribute(path, attr, LinkOption.NOFOLLOW_LINKS);
            if (value instanceof FileTime) {
                LocalDateTime time = LocalDateTime.ofInstant(((FileTime) value).toInstant(), ZoneId.systemDefault());
                System.out.printf("%-25s: %2$tD %2$tT%n", attr, time);
            } else if (value instanceof Boolean) {
                System.out.printf("%-25s: %b%n", attr, value);
            } else if (value instanceof Long) {
                System.out.printf("%-25s: %d%n", attr, value);
            } else {
                System.out.printf("%-25s: %s%n", attr, value);
            }
        }
    }

    public static void showDos(Path path) throws IOException{
        DosFileAttributeView dv = Files.getFileAttributeView(path,DosFileAttributeView.class);
        DosFileAttributes da = dv.readAttributes();
        System.out.printf("%n");
        System.out.printf("path      :%s%n", path);
        System.out.printf("isArchive :%s%n", da.isArchive());
        System.out.printf("isHidden  :%s%n", da.isHidden());
        System.out.printf("isReadOnly:%s%n", da.isReadOnly());
        System.out.printf("isSystem  :%s%n", da.isSystem());
    }
    public static FileAttribute<List<AclEntry>> attributes(Path path) throws IOException {
        final List<AclEntry> list = new ArrayList<>();
        List rawList = (List) Files.getAttribute(path, "acl:acl");
        for (Object o : rawList) {
            AclEntry aclEntry = (AclEntry) o;
            list.add(aclEntry);
        }
        return new FileAttribute<>() {
            @Override
            public String name() {
                return "acl:acl";
            }

            @Override
            public List<AclEntry> value() {
                return list;
            }
        };
    }
}
