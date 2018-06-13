package nio2.walktree;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ViewVisitor extends SimpleFileVisitor<Path> {
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