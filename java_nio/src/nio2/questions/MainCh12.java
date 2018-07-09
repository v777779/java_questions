package nio2.questions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainCh12 {
    public static void run(String path) throws IOException {
        String cp = "out/production/java_nio";
        String name = "nio2.questions.MainCh12";
        Runtime.getRuntime().exec("cmd /c start java -cp " + cp + " " + name + " " + path);
    }


    public static void main(String[] args) {
//        Create a Java application named Touch for setting a file’s or
//        directory’s timestamp to the current time. This application has the
//        following usage syntax: java Touch pathname.

        args = new String[]{"./data/nio2/result.txt"};

        if (args == null || args.length == 0 || args[0].isEmpty()) {
            System.out.println("Usage: java MainCh12 Path");
        }
        try {
            Path path = Paths.get(args[0]);
            Path pathC = path.getParent().resolve("result_time.txt");
//            FileTime lastTime = FileTime.from(Instant.now());
//            DosFileAttributeView dv = Files.getFileAttributeView(path, DosFileAttributeView.class);
//            dv.setTimes(lastTime,da.lastAccessTime(),da.creationTime());


            Files.deleteIfExists(pathC);
            path = Files.copy(path, pathC);
            FileTime last = Files.getLastModifiedTime(pathC);
            LocalDateTime time = LocalDateTime.ofInstant(last.toInstant(), ZoneId.systemDefault());
            System.out.printf("path:%s time:%2$tD %2$tT%n", path, time);

            last = FileTime.from(LocalDateTime.now().toInstant(ZoneOffset.ofHoursMinutes(3, 0)));
            time = LocalDateTime.ofInstant(last.toInstant(), ZoneId.systemDefault());
            System.out.printf("path:%s now :%2$tD %2$tT%n", pathC, time);
            Files.setLastModifiedTime(path, last);

            last = Files.getLastModifiedTime(pathC);
            time = LocalDateTime.ofInstant(last.toInstant(), ZoneId.systemDefault());
            System.out.printf("path:%s last:%2$tD %2$tT%n", pathC, time);

            Thread.sleep(10000);
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception: " + e);
        }

    }
}
