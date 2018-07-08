package nio2.walktree;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MoveVisitor extends SimpleFileVisitor<Path> {
    private static final StandardCopyOption option = StandardCopyOption.REPLACE_EXISTING;
    private Path fromPath;
    private Path toPath;

    public MoveVisitor(Path fromPath, Path toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        System.out.printf("from:%-40s to:%-40s%n", fromPath,toPath);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path pSrc = fromPath.relativize(dir);
        Path pDst = toPath.resolve(pSrc);

        Files.copy(dir,pDst,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path pSrc = fromPath.relativize(file);
        Path pDst = toPath.resolve(pSrc);
        Files.move(file,pDst,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);

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
            if(!Files.deleteIfExists(dir)){
                throw new IOException(String.format("dir :%s can't delete folder%n",dir));
            }
         return FileVisitResult.CONTINUE;
    }
}