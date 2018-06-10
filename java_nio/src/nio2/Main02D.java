package nio2;

import nio2.attributes.MainACL;
import nio2.attributes.MainAttrUtils;
import util.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02D {
    private static void pathInfo(Path path) throws IOException {
        System.out.printf("%n");
        System.out.printf("path            : %s%n", path);
        System.out.printf("exists          : %b%n", Files.exists(path));
        System.out.printf("not exists      : %b%n", Files.notExists(path));
        System.out.printf("is directory    : %b%n", Files.isDirectory(path));
        System.out.printf("is executable   : %b%n", Files.isExecutable(path));
        System.out.printf("is regular      : %b%n", Files.isRegularFile(path));
        System.out.printf("is readable     : %b%n", Files.isReadable(path));
        System.out.printf("is writable     : %b%n", Files.isWritable(path));
        System.out.printf("is symbolic link: %b%n", Files.isSymbolicLink(path));
        if (Files.exists(path)) {
            System.out.printf("is hidden       : %b%n", Files.isHidden(path));
        }
        System.out.printf("size            : %d%n", Files.size(path));

    }

    public static void main(String[] args) {


        System.out.printf(FORMAT, "Files Checking Path:");

        Path path = Paths.get(".", "data", "nio");
        Path pathR = Paths.get(path.toString(), "result.txt");
        Path pathC = Paths.get(path.toString(), "check.cmd");
        Path pathN = Paths.get(path.toString(), "check.bat");
        FileSystem fs = FileSystems.getDefault();
        List<Path> rootList = new ArrayList<>();
        fs.getRootDirectories().forEach(rootList::add);

        try {
            pathInfo(path);
            pathInfo(pathR);
            pathInfo(pathC);
            pathInfo(pathN);

            for (Path p : rootList) {
                pathInfo(p);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf(FORMAT, "Files :");
        path = Paths.get(".", "data", "nio");
        try {
            MainACL.main(pathR);
            pathC = Paths.get(path.toString(), "check.bat");
            Path pathD = Paths.get(path.toString(), "check.ini");
            Path pathE = Paths.get(path.toString(), "check.txt");

            if (Files.exists(pathC)) Files.delete(pathC);
            if (Files.exists(pathD)) Files.delete(pathD);
            if (Files.exists(pathE)) Files.delete(pathE);
            path = Paths.get(path.toString(), "result.txt");

            FileAttribute<List<AclEntry>> fileAttr = MainAttrUtils.attributes(path);
            System.out.printf(FORMAT, "CreateFile <path> FileAttributes<ACL>:");
            pathR = Files.createFile(pathC, fileAttr);
            pathInfo(pathR);
            pathR = Files.createFile(pathD, fileAttr);
            pathInfo(pathR);
            System.out.printf(FORMAT, "CreateFile <path> :");
            pathR = Files.createFile(pathE);
            pathInfo(pathR);
// create file Posix
            System.out.printf(FORMAT, "FileAttributes<Posix>:");
            try {
                Set<PosixFilePermission> set = Files.getPosixFilePermissions(path);
                FileAttribute<Set<PosixFilePermission>> fpAttr = PosixFilePermissions.asFileAttribute(set);
            } catch (UnsupportedOperationException e) {
                System.out.printf("Exception:%s%n", e);
            }

            try {
                Set<PosixFilePermission> set = PosixFilePermissions.fromString("rw-r-xr-x");
                FileAttribute<Set<PosixFilePermission>> fpAttr = PosixFilePermissions.asFileAttribute(set);
                System.out.printf("name:%s%n", fpAttr.name());
                for (PosixFilePermission perm : fpAttr.value()) {
                    System.out.printf("permission:%s%n", perm.toString());
                }
                if (!Files.isDirectory(path)) {
                    path = path.subpath(0, path.getNameCount() - 1);
                }
                path = Paths.get(path.toString(), "resultPosix.txt");

                pathR = Files.createFile(path, fpAttr);
                pathInfo(pathR);

            } catch (UnsupportedOperationException e) {
                System.out.printf("Exception:%s%n", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

// create temp File
        System.out.printf(FORMAT, "Create Temp Files:");
        List<OutputStream> listStreams = new ArrayList<>();
        try {
            path = Paths.get(".", "data", "temp");
            String prefix = "temp";
            String suffix = "file";
            if (!Files.exists(path)) {
                path = Files.createDirectory(path);
            }
            List<Path> listTempFiles = new ArrayList<>();
            final List<Path> listHookFiles = new ArrayList<>();


            listTempFiles.add(Files.createTempFile(path, prefix, suffix));
            listTempFiles.add(Files.createTempFile(path, prefix, suffix));
            listTempFiles.add(Files.createTempFile(path, prefix, suffix));
            listTempFiles.add(Files.createTempFile(path, prefix, null));
            listTempFiles.add(Files.createTempFile(path, prefix, null));
            System.out.printf("TempFiles:%n");
            for (Path filePath : listTempFiles) {
                System.out.printf("%s%n", filePath);
            }

// program to delete on exit
            for (Path tempPath : listTempFiles) {
                tempPath.toFile().deleteOnExit();
            }

// runtime hook
            Thread hook = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (Path filePath : listHookFiles) {
                            Files.deleteIfExists(filePath);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            listHookFiles.add(Files.createTempFile(path, null, suffix));
            listHookFiles.add(Files.createTempFile(path, null, suffix));

            Runtime.getRuntime().addShutdownHook(hook);
            System.out.printf("HookFiles:%n");
            for (Path filePath : listHookFiles) {
                System.out.printf("%s%n", filePath);
            }

// output stream
            OutputStream out;
            pathC = Paths.get(path.toString(), prefix + "12332.tmp");
            Files.deleteIfExists(pathC);
            out = Files.newOutputStream(pathC,
                    StandardOpenOption.DELETE_ON_CLOSE, StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE);
            listStreams.add(out);
            out = Files.newOutputStream(Paths.get(path.toString(), prefix + "12333.tmp"),
                    StandardOpenOption.DELETE_ON_CLOSE, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
            listStreams.add(out);
            System.out.printf("StreamFiles:%n");
            for (OutputStream stream : listStreams) {
                System.out.printf("%s%n", stream.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(listStreams.toArray(new OutputStream[0]));
        }


        System.out.printf(FORMAT, "Read Files :");
        path = Paths.get(".", "data", "nio");
        Path pathD = Paths.get(path.toString(), "result_u.txt");
        Path pathE = Paths.get(path.toString(), "result_k.txt");

        try {
            byte[] bytes = Files.readAllBytes(pathD);
//            InputStream in = new ByteArrayInputStream(bytes);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
//            String s;
//            while ((s = br.readLine()) != null) {
//                System.out.printf("%s%n", s);
//            }
//            br.close();
//            in.close();  // не требуется для ByteArray
            System.out.printf("%s%n", new String(bytes, StandardCharsets.UTF_8));

// conversion one
            ByteBuffer b = ByteBuffer.wrap(Files.readAllBytes(Paths.get(path.toString(), "result_w.txt")));
            CharBuffer cb = CharBuffer.wrap(new String(b.array(), Charset.forName("WINDOWS-1251")));
            System.out.printf("%s%n", cb);
// conversion two
            ByteBuffer b2 = ByteBuffer.wrap(Files.readAllBytes(pathE));
            CharBuffer cb2 = Charset.forName("KOI8-R").decode(b2);
            System.out.printf("%s%n", cb2);
            List<String> list = Files.readAllLines(pathD);
            for (String string : list) {
                System.out.printf("%s%n", string);
            }

            List<String> listR = Files.readAllLines(pathE, Charset.forName("KOI8-R"));
            for (String string : listR) {
                System.out.printf("%s%n", string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf(FORMAT, "Read Large Files :");
        pathD = Paths.get(".", "data", "nio", "result.txt");
        pathE = Paths.get(".", "data", "nio", "result_k.txt");

        BufferedReader br = null;
        BufferedWriter bw = null;
        InputStream in = null;
        OutputStream out = null;
        try {
// conversion one
            br = Files.newBufferedReader(pathD);
            br.lines().forEach(s -> System.out.printf("%s%n", s));
            br.close();
// conversion two
            br = Files.newBufferedReader(pathE, Charset.forName("KOI8-R"));
            br.lines().forEach(s -> System.out.printf("%s%n", s));
            br.close();
// input stream
            pathD = Paths.get(".", "data", "nio", "result_k.txt");
            pathE = Paths.get(".", "data", "nio", "result_in.txt");
            Files.copy(pathD, pathE, StandardCopyOption.REPLACE_EXISTING);

            in = Files.newInputStream(pathE);
            String s = new String(in.readAllBytes(), Charset.forName("KOI8-R"));
            System.out.printf("%s%n", s);
            in.close();

// temp
//            Files.copy(pathD,pathE,StandardCopyOption.REPLACE_EXISTING);
//            System.out.printf("path:%s  size:%d%n",pathE.getFileName(),Files.size(pathE));
//            OutputStream out = Files.newOutputStream(pathE, StandardOpenOption.CREATE,
//                   StandardOpenOption.TRUNCATE_EXISTING);
//            System.out.printf("path:%s  size:%d%n",pathE.getFileName(),Files.size(pathE));
//            out.write(121);
//            out.close();
//            in = Files.newInputStream(pathE);
//            s = new String(in.readAllBytes(),StandardCharsets.UTF_8);
//            System.out.printf("%s%n",s);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br, bw, in, out);
        }


        System.out.printf(FORMAT, "Write Files :");
        path = Paths.get(".", "data", "nio");
        pathD = Paths.get(path.toString(), "result_k.txt");
        pathE = Paths.get(path.toString(), "result_k_small.txt");

        try {
// bytes
            System.out.printf("Bytes:%n");
// source
            Files.deleteIfExists(pathE);
            byte[] bytes = Files.readAllBytes(pathD);

// write all bytes[]
            Files.write(pathE, bytes, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            pathR = Files.write(pathE, bytes, StandardOpenOption.APPEND);
            pathInfo(pathR);

// write 25 bytes
            pathR = Files.write(pathE, Arrays.copyOfRange(bytes, 0, 25), StandardOpenOption.TRUNCATE_EXISTING);
            pathInfo(pathR);
            System.out.printf("%s%n", new String(Files.readAllBytes(pathE), Charset.forName("KOI8-R")));

// lines
            System.out.printf("Lines:%n");
            pathD = Paths.get(path.toString(), "result_u.txt");
            pathE = Paths.get(path.toString(), "result_k.txt");
// source
            List<String> listK = Files.readAllLines(pathD, StandardCharsets.UTF_8);
            listK.addAll(Files.readAllLines(pathE, Charset.forName("KOI8-R")));
// write all
            pathE = Paths.get(path.toString(), "result_k_small.txt");
            pathR = Files.write(pathE, listK, Charset.forName("KOI8-R"), StandardOpenOption.TRUNCATE_EXISTING);
// checkout
            System.out.printf("%s%n", new String(Files.readAllBytes(pathR), Charset.forName("KOI8-R")));

            System.out.printf("Truncate check:%n");
            Path pathF = Paths.get(path.toString(), "result_k_small_copy.txt");
            Files.copy(pathE, pathF, StandardCopyOption.REPLACE_EXISTING);
            System.out.printf("path:%s  size:%d%n", pathF.getFileName(), Files.size(pathF));
            listK = listK.subList(0, 2);
            pathR = Files.write(pathF, listK, Charset.forName("KOI8-R"), StandardOpenOption.TRUNCATE_EXISTING);
            System.out.printf("path:%s  size:%d%n", pathF.getFileName(), Files.size(pathF));
// checkout
            System.out.printf("%s%n", new String(Files.readAllBytes(pathR), Charset.forName("KOI8-R")));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br, bw, in, out);
        }

        System.out.printf(FORMAT, "Write Large Files :");
        path = Paths.get(".", "data", "nio");
        pathD = Paths.get(path.toString(), "result_u.txt");
        pathE = Paths.get(path.toString(), "result_u_large.txt");

        br = null;
        bw = null;
        in = null;
        out = null;
        try {
// source
            URL url = pathD.toUri().toURL();
            br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            br.lines().forEach(s -> System.out.printf("%s%n", s));
            br.close();
// new file
            br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            Files.deleteIfExists(pathE);
            bw = Files.newBufferedWriter(pathE, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            String str;
            while ((str = br.readLine()) != null) {
                bw.write(str);
                bw.newLine();
                bw.write(str);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            br.close();

            br = Files.newBufferedReader(pathE, StandardCharsets.UTF_8);
            br.lines().forEach(s -> System.out.printf("%s%n", s));
            br.close();
// copy
            pathR = Files.createTempFile(Paths.get("./data/temp"), "tmp", null); // dst
            Files.copy(pathE, pathR, StandardCopyOption.REPLACE_EXISTING); // temp file loaded
// write to tmp autodelete
            pathD = Paths.get(path.toString(), "result_k.txt");              // src
            br = Files.newBufferedReader(pathD, Charset.forName("KOI8-R"));         // dst
            out = Files.newOutputStream(pathR, StandardOpenOption.APPEND, StandardOpenOption.DELETE_ON_CLOSE);
            bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            while ((str = br.readLine()) != null) {
                bw.write(str);
                bw.newLine();
            }
            bw.flush();
// copy back
            Files.copy(pathR, pathE, StandardCopyOption.REPLACE_EXISTING); // large file loaded
            bw.close();
            br.close();
// checkout
            br = Files.newBufferedReader(pathE, StandardCharsets.UTF_8);
            br.lines().forEach(s -> System.out.printf("%s%n", s));
            br.close();

// temp
            System.out.printf(FORMAT,"Write Truncated:");
            pathE = Paths.get(path.toString(),"result_truncated.txt");
            Files.copy(pathD, pathE, StandardCopyOption.REPLACE_EXISTING);
            System.out.printf("path:%s  size:%d%n", pathE.getFileName(), Files.size(pathE));
            out = Files.newOutputStream(pathE, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            System.out.printf("path:%s  size:%d%n", pathE.getFileName(), Files.size(pathE));
            String s = "Truncated строка в KOI8-R";
            out.write(s.getBytes(Charset.forName("KOI8-R")));
            out.close();
// checkout
            System.out.printf("path:%s  size:%d%n", pathE.getFileName(), Files.size(pathE));
            in = Files.newInputStream(pathE);
            System.out.printf("UTF-8 :%s%n", new String(in.readAllBytes(), StandardCharsets.UTF_8));
            in.close();
            in = Files.newInputStream(pathE);
            System.out.printf("KOI8-R:%s%n", new String(in.readAllBytes(), Charset.forName("KOI8-R")));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br, bw, in, out);
        }


//        System.out.printf(FORMAT, "Files :");
//        Path path = Paths.get(".", "data", "nio");
//        Path pathR = Paths.get(path.toString(), "result.txt");
//
//        try {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    }

}
