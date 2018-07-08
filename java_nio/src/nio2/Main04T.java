package nio2;

import nio2.walktree.*;
import nio2.watchers.MainWatch;
import util.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main04T {


    public static void main(String[] args) {

        System.out.printf(FORMAT, "Walk Tree:");
        Path path = Paths.get(".", "data", "nio2");
        Path pathD = Paths.get(path.toString(), "result.txt");
        Path pathE = Paths.get(path.toString(), "result_k.txt");
        Path pathR;

        try {
            WalkUtils.createUnderTree(path.resolve("walktree"),path);
            Files.walkFileTree(path.resolve("walktree"), new ViewVisitor());
//            WalkUtils.removeIfTree(path.resolve("walktree"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf(FORMAT, "Copy Tree:");
        path = Paths.get(".", "data", "nio2");
        pathD = Paths.get(path.toString(), "walktree");
        pathE = Paths.get(path.toString(), "temp", "copy", "walktree2");

        try {
            WalkUtils.createIfTree(pathD);

// source not exists
            if (!Files.exists(pathD)) {
                IOException ex = new IOException(String.format("path:%s Source not Exists%n", pathD));
                ex.setStackTrace(Thread.currentThread().getStackTrace());
                throw ex;

            }

// source is files, dest not exists or exists files or folder
            if (!Files.isDirectory(pathD)) {
                if (Files.exists(pathE) && Files.isDirectory(pathE)) {
                    pathE = pathE.resolve(path.getFileName());              // dest/source_file
                }
                Files.copy(path, pathE, StandardCopyOption.REPLACE_EXISTING);
            }

// source is folder dest exists files
            if (Files.exists(pathE) && !Files.isDirectory(pathE)) {
                IOException ex = new IOException(String.format("path:%s Target is File%n", pathE));
                ex.setStackTrace(Thread.currentThread().getStackTrace());
                throw ex;
            }

            EnumSet<FileVisitOption> set = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            CopyVisitor copyVisitor = new CopyVisitor(pathD, pathE);
            Files.walkFileTree(pathD, set, Integer.MAX_VALUE, copyVisitor);

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.printf(FORMAT, "Delete Tree:");
        path = Paths.get(".", "data", "nio2");
        pathD = Paths.get(path.toString(), "walktree");
        pathE = Paths.get(path.toString(), "temp", "copy", "walktree2");

        try {
            if (!Files.exists(pathD)) {
                WalkUtils.createIfTree(pathD);
            }

            EnumSet<FileVisitOption> set = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            DeleteVisitor deleteVisitor = new DeleteVisitor();
            Files.walkFileTree(pathD, set, Integer.MAX_VALUE, deleteVisitor);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf(FORMAT, "Move Tree:");
        path = Paths.get(".", "data", "nio2");
        pathD = Paths.get(path.toString(), "temp", "copy", "walktree2");
        pathE = Paths.get(path.toString(), "temp", "walktree");

        try {
            if (!Files.exists(pathD)) {
                WalkUtils.createIfTree(pathD);
            }
            if (Files.exists(pathE)) {
                Files.walkFileTree(pathE, new DeleteVisitor());
            }

            EnumSet<FileVisitOption> set = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            MoveVisitor moveVisitor = new MoveVisitor(pathD, pathE);
            Files.walkFileTree(pathD, set, Integer.MAX_VALUE, moveVisitor);

        } catch (IOException e) {
            e.printStackTrace();
        }
//


        System.out.printf(FORMAT, "Files Stream Methods:");
        path = Paths.get(".", "data", "nio2");
        pathD = Paths.get(path.toString(), "result.txt");
        pathE = Paths.get(path.toString(), "result_k.txt");

        Stream<String> ss = null;
        Stream<Path> sp = null;

        String regex = "(.*_k.*)|(t.*)";
        try {
            System.out.printf(FORMAT, "Files.find():");
            BiPredicate<Path, BasicFileAttributes> matcher = (p, a) -> a.isRegularFile() &&
                    p.getFileName().toString().matches("(.*_k.*)|(t.*)");
            sp = Files.find(path, 10, matcher);
            sp.forEach(p -> System.out.printf("path:%s%n", p));
            sp.close();

            System.out.printf(FORMAT, "Files.lines():");
            ss = Files.lines(pathE, Charset.forName("KOI8-R"));
            ss.forEach(s -> System.out.printf("path:%s%n", s));
            ss.close();

            System.out.printf(FORMAT, "Files.list():");

            sp = Files.list((path));
            sp.filter(p -> p.getFileName().toString().matches(regex))
                    .forEach(p -> System.out.printf("path:%s%n", p));
            sp.close();

            System.out.printf(FORMAT, "Files.walk():");
            Predicate<Path> predicate = p -> p.getFileName().toString().matches(regex);

            sp = Files.walk(path);
            sp.forEach(p -> System.out.printf("path:%-60s  name:%-32s match:%b%n", p, p.getFileName(), predicate));
            sp.close();
            System.out.printf("%n");
            System.out.printf(FORMAT, "Files.walk() predicate:");
            sp = Files.walk(path);
            sp.filter(predicate).forEach(p ->
                    System.out.printf("path:%-60s  name:%-32s match:%b%n", p, p.getFileName(), predicate));
            sp.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ss, sp);
        }
//

        System.out.printf(FORMAT, "Files Stream Methods:");
        path = Paths.get(".", "data", "nio2");
        pathD = Paths.get(path.toString(), "result.txt");
        pathE = Paths.get(path.toString(), "result_k.txt");

        try {
            if (!Files.exists(path.resolve("temp"))) {
                Files.createDirectory(path.resolve("temp"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ss, sp);
        }

// anonymous classes
        System.out.printf(FORMAT, "Anonymous Class Instances:");
        WalkUtils.createAnonymInstances();

//
        System.out.printf(FORMAT, "Folder Watchers:");
        path = Paths.get(".", "data", "nio2");
        pathD = Paths.get(path.toString(), "result.txt");
        pathE = Paths.get(path.toString(), "result_k.txt");

        try {

//            Runtime.getRuntime().exec("cmd /c start call java -ea -cp " +
//                    "out/production/java_nio nio2.watchers.MainWatch");
//            Runtime.getRuntime().exec("cmd /c start call java -ea -cp " +
//                    "out/production/java_nio nio2.watchers.MainFolder");
            Runtime.getRuntime().exec("cmd /c start java -ea -cp " +
                    "out/production/java_nio nio2.watchers.MainFolder");
            MainWatch.main(args);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ss, sp);
        }
    }

}
