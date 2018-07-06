package nio2.walktree_CHECK_THIS;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class CopyVisitor extends SimpleFileVisitor<Path> {
    private static final StandardCopyOption option = StandardCopyOption.REPLACE_EXISTING;
    private Path fromPath;
    private Path toPath;

    public CopyVisitor(Path fromPath, Path toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
        System.out.printf("from:%-40s to:%-40s%n", fromPath, toPath);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path pSrc = fromPath.relativize(dir);
        Path pDst = toPath.resolve(pSrc);

        System.out.printf("dir:%-40s pSrc:%-40s pDst:%-40s%n", dir, pSrc, pDst);
        if (!Files.exists(pDst)) {
            Files.createDirectories(pDst);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path pSrc = fromPath.relativize(file);
        Path pDst = toPath.resolve(pSrc);

        System.out.printf("files:%-40s pSrc:%-40s pDst:%-40s%n", file, pSrc, pDst);
        Files.copy(file, pDst, option);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {

        System.err.printf("%s%n", exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Path pSrc = fromPath.relativize(dir);
        Path pDst = toPath.resolve(pSrc);

        if (exc != null) throw exc;

        FileTime lastModifiedTime = Files.getLastModifiedTime(dir);
        Files.setLastModifiedTime(pDst, lastModifiedTime);
        return FileVisitResult.CONTINUE;
    }
}