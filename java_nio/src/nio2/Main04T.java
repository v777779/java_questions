package nio2;

import nio2.files.MainFileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04T {
    private static class Util {
        private static void createTree(Path path) throws IOException {
            if(!Files.exists(path)) {
                Files.createDirectory(path);
            }

            Path pathC = path.getParent();
            Path pathD = Paths.get(pathC.toString(), "result.txt");
            Path pathE = Paths.get(pathC.toString(), "result_k.txt");
            Path pathR;

            pathR = Paths.get(path.toString(),"walk");
            if(!Files.exists(pathR)) Files.createDirectory(pathR);
            Files.copy(pathD,pathR.resolve(pathD.getFileName()),StandardCopyOption.REPLACE_EXISTING);
            Files.copy(pathE,pathR.resolve(pathE.getFileName()),StandardCopyOption.REPLACE_EXISTING);

            pathR = Paths.get(path.toString(),"wmode");
            if(!Files.exists(pathR)) Files.createDirectory(pathR);
            Files.copy(pathD,pathR.resolve(pathD.getFileName()),StandardCopyOption.REPLACE_EXISTING);
            Files.copy(pathD,pathR.resolve("result_k.txt"),StandardCopyOption.REPLACE_EXISTING);

            pathR = Paths.get(path.toString(),"wnet");
            if(!Files.exists(pathR)) Files.createDirectory(pathR);
        }

        private static void removeTree(Path path) throws IOException {
            if(!Files.exists(path)) return;
            MainFileUtils.deleteFolderRegex(path,".*");
        }
    }
    private static class PrintVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            LocalDateTime time = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(),ZoneId.systemDefault());
            System.out.printf("pre dir    :%1$-40s modified : %2$tD %2$tT  size:%3$d %n",dir,time,attrs.size());
            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            LocalDateTime time = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(),ZoneId.systemDefault());
            System.out.printf("visit file :%1$-40s modified : %2$tD %2$tT  size:%3$d %n",file,time,attrs.size());
            return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            System.out.printf("failed file: %-40s : %-40s %n",file,exc);
            return super.visitFileFailed(file, exc);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            System.out.printf("post dir   :%-40s exception: %-40s %n",dir,exc);


            return super.postVisitDirectory(dir, exc);
        }
    }

    public static void main(String[] args) {

        System.out.printf(FORMAT, "Tree Walk:");
        Path path = Paths.get(".", "data", "nio2");
        Path pathD = Paths.get(path.toString(), "result.txt");
        Path pathE = Paths.get(path.toString(), "result_k.txt");
        Path pathR;

        try {

            Util.createTree(path.resolve("walktree"));
            Files.walkFileTree(path.resolve("walktree"),new PrintVisitor());
//            MainFileUtils.deleteSubFolderRegex(path,"w.*");

        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.printf(FORMAT, "Read Large Files :");
//        Path path = Paths.get(".", "data", "nio2");
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
//        Path path = Paths.get(".", "data", "nio2");
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
//        Path path = Paths.get(".", "data", "nio2");
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
//        Path path = Paths.get(".", "data", "nio2");
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
