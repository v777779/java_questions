package nio2.walktree;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteVisitor extends SimpleFileVisitor<Path> {


    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if(Files.exists(file)){
            if(Files.deleteIfExists(file)) {
                System.out.printf("file:%-40s  deleted%n", file);
            }else {
                throw new IOException(String.format("can't delete file:%s",file));
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {

        System.err.printf("%s%n",exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if(exc != null) throw exc;

        if(Files.exists(dir)){
            if(Files.deleteIfExists(dir)) {
                System.out.printf("dir :%-40s  deleted%n", dir);
            }else {
                throw new IOException(String.format("can't delete folder:%s",dir));
            }
        }
         return super.postVisitDirectory(dir, exc);
    }
}