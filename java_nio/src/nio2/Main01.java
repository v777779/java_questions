package nio2;

import nio2.attributes.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.file.spi.FileSystemProvider;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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

        Path path = Paths.get(".", "data/nio2/", "result.txt").toAbsolutePath();
        File toFile = path.toFile();
        Path toPath = toFile.toPath();

        URI uri = URI.create("files:///" + path.toString().replaceAll("\\\\", "/"));
        Path pathURI = Paths.get(uri);

        System.out.printf("path:%s%n", path);
        System.out.printf("files:%s    exists:%b%n", toFile, toFile.exists());
        System.out.printf("path:%s    exists:%b%n", toPath, toPath.toFile().exists());
        System.out.printf("pathURI:%s exists:%b%n", pathURI, pathURI.toFile().exists());

        System.out.printf(FORMAT, "FileSystem");
        fs = FileSystems.getDefault();

        String regex = "(.*\\.)(.*)(@.*+)";
        Matcher m = Pattern.compile(regex).matcher(fs.toString());
        String name = "";
        if (m.matches()) {
            name = m.group(2);
        }
        System.out.printf("filesystem:%s separator:%s scheme:%s%n", name, fs.getSeparator(),
                fs.provider().getScheme());


        for (FileStore fileStore : fs.getFileStores()) {
            System.out.printf("store:%s%n", fileStore.name());
        }
        for (Path p : fs.getRootDirectories()) {
            System.out.printf("root:%s%n", p.toString());
        }

        System.out.printf(FORMAT, "FS getPath:");
        String sep = fs.getSeparator();


        Path pathX = fs.getPath(".", "data" + sep + "nio2" + sep, "result.txt").toAbsolutePath();
        Path pathY = fs.getPath(".", "data", "nio2", "result.txt").toAbsolutePath();
        System.out.printf("%s%n", pathX);
        System.out.printf("%s  %b%n", pathX, pathX.toFile().exists());
        System.out.printf("%s  %b%n", pathY, pathY.toFile().exists());
        System.out.printf("fs:%5.10s files:%s name:%s count:%d root:%s%n",
                pathX.getFileSystem(), pathX.getFileName(),
                pathX.getName(0), pathX.getNameCount(), pathX.getRoot());

        System.out.printf("parent :%s%n", pathX.getParent());
        System.out.printf("subpath:%s%n", pathX.subpath(1, 5));

// FileSystem
        System.out.printf(FORMAT, "Path demo :");
        System.out.printf(FORMAT, "Relative Path:");
        fs = FileSystems.getDefault();
        String root = fs.getPath(".").toAbsolutePath().getRoot().toString();
        path = fs.getPath(".", "data", "nio2", "result.txt");

        System.out.printf("%s%n", path);
        path = path.normalize();
        System.out.printf("Path normalized:%n%s%n", path.normalize());
        System.out.printf("File name: %s  exists:%b%n", path.getFileName(), path.toFile().exists());
        for (int i = 0; i < path.getNameCount(); i++) {
            System.out.printf("seg%d:%s ", i, path.getName(i));
        }
        System.out.printf("%n");
        System.out.printf("Parent: %s%n", path.getParent());
        System.out.printf("Root %s%n", path.getRoot());
        System.out.printf("SubPath[0..2]:%s%n", path.subpath(0, 2));
        System.out.printf("Path absolute:%b%n", path.isAbsolute());


        System.out.printf(FORMAT, "Absolute Path:");
        fs = FileSystems.getDefault();
        path = fs.getPath(".", "data", "nio2", "result.txt").toAbsolutePath();

        System.out.printf("%s%n", path);
        path = path.normalize();
        System.out.printf("Path normalized:%n%s%n", path.normalize());
        System.out.printf("File name: %s  exists:%b%n", path.getFileName(), path.toFile().exists());
        for (int i = 0; i < path.getNameCount(); i++) {
            System.out.printf("seg%d:%s ", i, path.getName(i));
        }
        System.out.printf("%n");
        System.out.printf("Parent: %s%n", path.getParent());
        System.out.printf("Root %s%n", path.getRoot());
        System.out.printf("SubPath[0..2]:%s%n", path.subpath(0, 2));
        System.out.printf("Path absolute:%b%n", path.isAbsolute());

        System.out.printf(FORMAT, "Root Directories");

        for (Path pRoot : fs.getRootDirectories()) {
            path = fs.getPath(pRoot.toString(), "data", "nio2", "result.txt");
            System.out.printf("%s absolute:%b  exists:%b%n", path, path.isAbsolute(), path.toFile().exists());

        }

        System.out.printf(FORMAT, "Path Normalize Relative Resolution");
        path = fs.getPath(".", "data", "nio2", "result.txt");     // relative path
        pathX = fs.getPath(".", "data", "stream", "buff-base64.txt");               // relative path

        Path pathR = path.relativize(pathX);
        Path pathE = Paths.get(path.toString(), pathR.toString());
        Path pathF = path.resolve(pathR);

        System.out.printf("Relative   :%s%n", path);
        System.out.printf("Absolute   :%s%n", path.toAbsolutePath());
        System.out.printf("Normalized :%s%n", path.toAbsolutePath().normalize());
        System.out.printf("Relative2  exists:%b:%s%n", pathX, pathX.toFile().exists());

        System.out.printf(FORMAT, "Relative path folders:");
        System.out.printf("rel :%s%n", path);
        System.out.printf("rel2:%s%n", pathX);

        System.out.printf("path:%-15s exists:%b%n", pathR, pathR.toFile().exists());
        System.out.printf("rest:%-25s exists:%b abs:%b%n", pathE, pathE.toFile().exists(), pathE.toAbsolutePath());
        System.out.printf("norm:%-25s exists:%b abs:%b%n", pathE.normalize(), pathE.normalize().toFile().exists(),
                pathE.normalize().isAbsolute());
        System.out.printf("rsvd:%-25s exists:%b abs:%b%n", pathF, pathF.toFile().exists(), pathF.toAbsolutePath());
        System.out.printf("norm:%-25s exists:%b abs:%b%n", pathF.normalize(), pathF.normalize().toFile().exists(),
                pathF.normalize().isAbsolute());

        System.out.printf(FORMAT, "Absolute path folders:");
        path = path.toAbsolutePath();
        pathX = pathX.toAbsolutePath();

        pathR = path.relativize(pathX);
        pathE = Paths.get(path.toString(), pathR.toString());
        pathF = path.resolve(pathR);

        System.out.printf("abs :%s%n", path);
        System.out.printf("abs2:%s%n", pathX);

        System.out.printf("path:%-15s exists:%b%n", pathR, pathR.toFile().exists());
        System.out.printf("rest:%-25s exists:%b abs:%b%n", pathE, pathE.toFile().exists(), pathE.toAbsolutePath());
        System.out.printf("norm:%-25s exists:%b abs:%b%n", pathE.normalize(), pathE.normalize().toFile().exists(),
                pathE.normalize().isAbsolute());
        System.out.printf("rsvd:%-25s exists:%b abs:%b%n", pathF, pathF.toFile().exists(), pathF.toAbsolutePath());
        System.out.printf("norm:%-25s exists:%b abs:%b%n", pathF.normalize(), pathF.normalize().toFile().exists(),
                pathF.normalize().isAbsolute());

        System.out.printf(FORMAT, "Relative Path siblings:");
        path = fs.getPath(".", "data", "nio2");
        pathX = fs.getPath(".", "data", "stream");

        pathR = path.relativize(pathX);
        pathE = Paths.get(path.toString(), pathR.toString());
        pathF = path.resolve(pathR);

        System.out.printf("path:%-25s exists:%b%n", pathR, pathR.toFile().exists());
        System.out.printf("rest:%-25s exists:%b abs:%b%n", pathE, pathE.toFile().exists(), pathE.toAbsolutePath());
        System.out.printf("norm:%-25s exists:%b abs:%b%n", pathE.normalize(), pathE.normalize().toFile().exists(),
                pathE.normalize().isAbsolute());
        System.out.printf("rsvd:%-25s exists:%b abs:%b%n", pathF, pathF.toFile().exists(), pathF.toAbsolutePath());
        System.out.printf("norm:%-25s exists:%b abs:%b%n", pathF.normalize(), pathF.normalize().toFile().exists(),
                pathF.normalize().isAbsolute());

        pathR = Paths.get("stream");
        System.out.printf("%npath: Path[%s]%n", pathR);
        pathE = path.resolve(pathR);
        pathF = path.resolveSibling(pathR);
        System.out.printf("resolve()  rest:%-25s exists:%b%n", pathE, pathE.toFile().exists());
        System.out.printf("resolve()  norm:%-25s exists:%b%n", pathE.normalize(), pathE.normalize().toFile().exists());
        System.out.printf("siblings() rsvd:%-25s exists:%b%n", pathF, pathF.toFile().exists());
        System.out.printf("siblings() norm:%-25s exists:%b%n", pathF.normalize(), pathF.normalize().toFile().exists());

        System.out.printf("%nstring: String[%s]%n", pathR);
        pathE = path.resolve(pathR.toString());
        pathF = path.resolveSibling(pathR.toString());
        System.out.printf("resolve()  rest:%-25s exists:%b%n", pathE, pathE.toFile().exists());
        System.out.printf("resolve()  norm:%-25s exists:%b%n", pathE.normalize(), pathE.normalize().toFile().exists());
        System.out.printf("siblings() rsvd:%-25s exists:%b%n", pathF, pathF.toFile().exists());
        System.out.printf("siblings() norm:%-25s exists:%b%n", pathF.normalize(), pathF.normalize().toFile().exists());

        System.out.printf(FORMAT, "Path comparisons:");
        path = fs.getPath(".", "data", "nio2");
        pathX = fs.getPath(".", "data", "stream").normalize();
        pathY = Paths.get("./data/nio2");

        System.out.printf("path : %s%n", path);
        System.out.printf("pathX: %s%n", pathX);
        System.out.printf("pathY: %s%n", pathY);


        System.out.printf("path.equals(pathX): %b%n", path.equals(pathX));
        System.out.printf("path.equals(pathY): %b%n", path.equals(pathY));
        System.out.printf("%n");
        System.out.printf("path.compareTo(pathX): %b%n", path.compareTo(pathX));
        System.out.printf("path.compareTo(pathY): %b%n", path.compareTo(pathY));
        System.out.printf("%n");
        System.out.printf("path.startsWith(\".\\data\"): %b%n", path.startsWith(".\\data"));
        System.out.printf("pathX.startsWith(\"data\") : %b%n", pathX.startsWith("data"));

        System.out.printf("%n");
        System.out.printf("path.endsWith(\"nio2\"): %b%n", path.endsWith("nio2"));
        System.out.printf("pathX.endsWith(\"stream\"): %b%n", pathX.endsWith("stream"));
        try {
            System.out.printf("%n");
            System.out.printf("path.toUri() : %s%n", path.toUri());
            System.out.printf("pathX.toUri(): %s%n", pathX.toUri());
            System.out.printf("path.realPath() : %s%n", path.toRealPath(LinkOption.NOFOLLOW_LINKS));
            System.out.printf("pathX.realPath(): %s%n", pathX.toRealPath(LinkOption.NOFOLLOW_LINKS));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf(FORMAT, "FileStore:");
        try {
            for (Path pathRoot : fs.getRootDirectories()) {

                if (!pathRoot.toFile().exists()) {
                    System.out.printf("Path: %s not ready%n", pathRoot);
                    continue;
                }

                FileStore fileStore = Files.getFileStore(pathRoot);
                System.out.printf("Path: %s%n", pathRoot);
                System.out.printf("Name: %s%n", fileStore.name());
                System.out.printf("Type: %s%n", fileStore.type());
                System.out.printf("Total space: %d%n", fileStore.getTotalSpace());
                System.out.printf("Unallocated space: %d%n",
                        fileStore.getUnallocatedSpace());
                System.out.printf("Usable space: %d%n",
                        fileStore.getUsableSpace());
                System.out.printf("Read only: %b%n", fileStore.isReadOnly());
                System.out.printf("%n");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


// File Attributes
        System.out.printf(FORMAT, "Files Attributes:");
        try {
            fs = FileSystems.getDefault();
            for (String s : fs.supportedFileAttributeViews()) {
                System.out.printf("attributes: %s%n", s);
            }

            for (Path rootPath : fs.getRootDirectories()) {
                System.out.printf("Drive:%s%n", rootPath);
                FileAttributeView fav = Files.getFileAttributeView(rootPath, BasicFileAttributeView.class);
                System.out.printf("%-25s supported:%b%n", BasicFileAttributeView.class.getSimpleName(), fav != null);
                fav = Files.getFileAttributeView(path, PosixFileAttributeView.class);
                System.out.printf("%-25s supported:%b%n", PosixFileAttributeView.class.getSimpleName(), fav != null);
                fav = Files.getFileAttributeView(path, AclFileAttributeView.class);
                System.out.printf("%-25s supported:%b%n", AclFileAttributeView.class.getSimpleName(), fav != null);
            }

// exception
            if (fs.getRootDirectories() == null) throw new IOException();

        } catch (IOException e) {
            e.printStackTrace();
        }

// File Attributes
        System.out.printf(FORMAT, "Files Attributes:");
        try {
            path = Paths.get("./data/nio2", "result.txt");
            pathX = Paths.get("./data/nio2", "result_symlink");


            if (pathX.toFile().exists()) {
                if (!pathX.toFile().delete()) throw new IOException();
            }
            pathX = Files.createLink(pathX, path);
            System.out.printf("path:%s  link:%b%n", path, Files.isSymbolicLink(path));
            System.out.printf("path:%s  link:%b%n", pathX, Files.isSymbolicLink(pathX));
            System.out.printf("path:%s%n", path);
            System.out.printf("real:%s%n", path.toRealPath(LinkOption.NOFOLLOW_LINKS));
            System.out.printf("%n");
            System.out.printf("path:%s%n", pathX);
            System.out.printf("real:%s%n", pathX.toRealPath(LinkOption.NOFOLLOW_LINKS));


            System.out.printf("%n");
            BasicFileAttributes ba = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            FileTime fileCTime = ba.creationTime();
            Object fileKey = ba.fileKey();
            boolean isDir = ba.isDirectory();
            boolean isOther = ba.isOther();
            boolean isRegular = ba.isRegularFile();
            boolean isSymLink = ba.isSymbolicLink();
            FileTime fileATime = ba.lastAccessTime();
            FileTime fileMTime = ba.lastModifiedTime();
            long size = ba.size();
//localDateTime
            LocalDateTime localCTime = LocalDateTime.ofInstant(fileCTime.toInstant(), ZoneId.systemDefault());
            LocalDateTime localATime = LocalDateTime.ofInstant(fileATime.toInstant(), ZoneId.systemDefault());
            LocalDateTime localMTime = LocalDateTime.ofInstant(fileMTime.toInstant(), ZoneId.systemDefault());
// print
            System.out.printf("files            :%s%n", path);
            System.out.printf("creationTime    :%1$tD %1$tT%n", localCTime);
            System.out.printf("fileKey         :%s%n", fileKey);
            System.out.printf("isDirectory     :%b%n", isDir);
            System.out.printf("isOther         :%b%n", isOther);
            System.out.printf("isRegular       :%b%n", isRegular);
            System.out.printf("isSymbolinkLink :%b%n", isSymLink);
            System.out.printf("lastAccessTime  :%1$tD %1$tT%n", localATime);
            System.out.printf("lastModifiedTime:%1$tD %1$tT%n", localMTime);
            System.out.printf("size            :%d%n", size);
// link
            MainAttr.showAttributes(pathX);
// folder
            MainAttr.showAttributes(path.subpath(0, path.getNameCount() - 1));
            System.out.printf(FORMAT, "Single Attribute:");
            Map<String, Object> map = new HashMap<>(Files.readAttributes(path, "*", LinkOption.NOFOLLOW_LINKS));

            System.out.printf("path: %s%n", path);
            for (String attr : map.keySet()) {
                Object value = map.get(attr);
                if (value instanceof FileTime) {
                    LocalDateTime time = LocalDateTime.ofInstant(((FileTime) value).toInstant(), ZoneId.systemDefault());
                    System.out.printf("%s: %2$tD %2$tT%n", attr, time);
                } else if (value instanceof Boolean) {
                    System.out.printf("%s: %b%n", attr, value);
                } else if (value instanceof Long) {
                    System.out.printf("%s: %d%n", attr, value);
                } else {
                    System.out.printf("%s: %s%n", attr, value);
                }
            }


            Set<String> set = new HashSet<>(Arrays.asList("creationTime", "lastAccessTime", "lastModifiedTime"));
// set
            Files.setAttribute(path, "lastModifiedTime", FileTime.from(Instant.now().plusSeconds(60)));
            MainAttr.showSet(path, set);
// set
            Files.setAttribute(path, "lastModifiedTime", ba.lastModifiedTime());
            MainAttr.showSet(path, set);

            System.out.printf(FORMAT, "BasicFileAttributesView set():");

            BasicFileAttributeView bv = Files.getFileAttributeView(path, BasicFileAttributeView.class);
            FileTime timeA = FileTime.from(Instant.now().plusSeconds(10));
            FileTime timeC = FileTime.from(Instant.now().plusSeconds(20));
            FileTime timeM = FileTime.from(Instant.now().plusSeconds(30));
// set
            bv.setTimes(timeC, timeA, timeM);
            MainAttr.showSet(path, set);
// set
            bv.setTimes(ba.creationTime(), ba.lastAccessTime(), ba.lastModifiedTime());
            MainAttr.showSet(path, set);

            System.out.printf(FORMAT, "DosFileAttributesView set():");
            DosFileAttributeView dv = Files.getFileAttributeView(path, DosFileAttributeView.class);
            DosFileAttributes da = dv.readAttributes();
            System.out.printf("path      :%s%n", path);
            System.out.printf("isArchive :%s%n", da.isArchive());
            System.out.printf("isHidden  :%s%n", da.isHidden());
            System.out.printf("isReadOnly:%s%n", da.isReadOnly());
            System.out.printf("isSystem  :%s%n", da.isSystem());
// set
            Files.setAttribute(path, "dos:" + MainAttr.ARCHIVE_NAME, false);
            Files.setAttribute(path, "dos:" + MainAttr.READONLY_NAME, true);
            MainAttr.showDos(path);
//set
            dv.setArchive(da.isArchive());
            dv.setReadOnly(da.isReadOnly());
            MainAttr.showDos(path);

            System.out.printf(FORMAT, "PosixFileAttributesView set():");
            try {
                PosixFileAttributeView pv = Files.getFileAttributeView(path, PosixFileAttributeView.class);
                System.out.printf("posix view:%s%n", pv);
                PosixFileAttributes pa = Files.readAttributes(path, PosixFileAttributes.class);
                System.out.printf("posix attr:%s%n", pa);

            } catch (UnsupportedOperationException e) {
                System.out.printf("Exception:%s%n", e);
            }
            MainPosix.mainPosixPerm(path);

            MainPosix.mainPosixAttr(path, "group", true);


            System.out.printf(FORMAT, "FileOwnerAttributeView:");
            MainOwner.main(path);

            System.out.printf(FORMAT, "AclFileAttributeView:");
            MainACL.main(path);

            System.out.printf(FORMAT, "UserDefinedFileAttributeView:");

            FileStore ffs = Files.getFileStore(path);
            if (!ffs.supportsFileAttributeView(UserDefinedFileAttributeView.class))
                System.out.println("User-defined attributes are supported.");
            else
                System.out.println("User-defined attributes are not supported.");

            MainUserDef.main(path, "L");
            MainUserDef.main(path, "R");

            System.out.printf(FORMAT, "FileStore Attributes:");
            for (FileStore fileStore : fs.getFileStores()) {
                System.out.printf("Volume:%n");
                System.out.printf("name             :%s type:%s%n",fileStore.name(),fileStore.type());
                System.out.printf("total space      : %d%n", (Long) fileStore.getAttribute("totalSpace"));
                System.out.printf("unallocated space: %d%n", (Long) fileStore.getAttribute("unallocatedSpace"));
                System.out.printf("usable space     : %d%n", (Long) fileStore.getAttribute("usableSpace"));
                System.out.printf("%n");

                System.out.printf("total space      : %d%n",  fileStore.getTotalSpace());
                System.out.printf("unallocated space: %d%n",  fileStore.getUnallocatedSpace());
                System.out.printf("usable space     : %d%n",  fileStore.getUsableSpace());
                System.out.printf("block size       : %d%n",  fileStore.getBlockSize());
                System.out.printf("read only        : %b%n",  fileStore.isReadOnly());
                System.out.printf("%n");
                System.out.printf("volume serial number: %d%n",
                        (Integer)fileStore.getAttribute("volume:vsn"));
                System.out.printf("is removable        : %b%n",
                        fileStore.getAttribute("volume:isRemovable"));
                System.out.printf("is CD-ROM           : %b%n",
                        fileStore.getAttribute("volume:isCdrom"));
                System.out.printf("%n");


            }


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


        } catch (IOException e) {
            e.printStackTrace();
        }


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
