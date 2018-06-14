package nio2.watchers;

import nio2.files.MainFileUtils;
import nio2.walktree.MoveVisitor;
import nio2.walktree.WalkUtils;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.EnumSet;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 14-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainFolder {
    //    private static void inkey() {
//        try {
//            System.out.print("Press <Enter> to continue...");
//            while (System.in.available() == 0) {}
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private static void inkey() {
        try {
            int count = 10;
            while (count-- > 0) {
                System.out.print(".");
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%n");
    }

    private static void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.printf(FORMAT, "Folder Changes:");

        assert (true);
        assert true : "Assert Message:assert check";

        Path path = Paths.get(".", "data", "nio2");
        Path pathD = Paths.get(path.toString(), "temp", "copy", "walktree");
        Path pathE = Paths.get(path.toString(), "temp", "walktree");
        Path pathR = path.resolve("walkfolder");
        DosFileAttributes da = null;
        try {
// create
            if (Files.exists(pathE.getParent())) {
                pathE = pathE.getParent().resolve("demo");
                System.out.printf("Folder TO create:%s%n", pathE);
                pause();
                WalkUtils.createForceFolder(pathE);     // remove and create
                System.out.printf("path:%s  created%n", pathE);

                System.out.printf("Folder TO remove:%s%n", pathE);
                pause();
                MainFileUtils.deleteFolder(pathE);
                System.out.printf("path:%s  removed exists:%b%n", pathE, Files.exists(pathE));
                pathE = pathE.getParent().resolve("walktree");  // revert back
            }
// move
            System.out.printf("Folder TO move:%s   %s%n", pathD, pathE);
            pause();
            MoveVisitor mv = new MoveVisitor(pathD, pathE);  // walktree >> move/walktree
            Files.walkFileTree(pathD, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, mv);

            class CheckPath {
                private Path path;
                public CheckPath(Path path) {
                    this.path = path;
                }
                private void set(boolean isReadOnly) throws IOException{
                    Files.setAttribute(path, "dos:readonly", isReadOnly);
                    DosFileAttributes da = Files.readAttributes(path, DosFileAttributes.class);
                    System.out.printf("path:%s readonly:%b%n", path, da.isReadOnly());
                }
                private Path get(){
                    return this.path;
                }
            }
// change
            System.out.printf("File Change:%n");
            pause();
            CheckPath checkPathS = new CheckPath(pathE.getParent().resolve("change"));       // temp/change
            CheckPath checkPathC =  new CheckPath(pathE);                    // temp/walktree/walk
            WalkUtils.createForceFolder(checkPathS.get()); // create
            checkPathS.set(true);
            checkPathC.set(true);
            pause();
            checkPathS.set(false);
            checkPathC.set(false);
            pause();
            WalkUtils.removeIfTree(checkPathS.get()); // remove
// copy under tree

// remove
            System.out.printf("Tree \"walktree\" remove:%s%n", pathE);
            pause();
//            WalkUtils.removeIfTree(pathE);
            System.out.printf("path:%s  removed exists:%b%n", pathE, Files.exists(pathE));
// remove
            System.out.printf("Folder remove:%s%n", pathR);
            pause();
            WalkUtils.removeIfTree(pathR);
            System.out.printf("path:%s  removed%n", pathR);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }
}
