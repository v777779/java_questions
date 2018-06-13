package nio2.walktree;

import nio2.files.MainFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 13-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class WalkUtils {
    public static void createTree(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

        Path pathC = path.getParent();
        Path pathD = Paths.get(pathC.toString(), "result.txt");
        Path pathE = Paths.get(pathC.toString(), "result_k.txt");
        Path pathR;

        pathR = Paths.get(path.toString(), "walk");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
        Files.copy(pathD, pathR.resolve(pathD.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pathE, pathR.resolve(pathE.getFileName()), StandardCopyOption.REPLACE_EXISTING);

        pathR = Paths.get(path.toString(), "wmode");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
        Files.copy(pathD, pathR.resolve(pathD.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(pathD, pathR.resolve("result_k.txt"), StandardCopyOption.REPLACE_EXISTING);

        pathR = Paths.get(path.toString(), "wnet");
        if (!Files.exists(pathR)) Files.createDirectory(pathR);
    }

    public static void removeTree(Path path) throws IOException {
        if (!Files.exists(path)) return;
        MainFileUtils.deleteFolder(path);  // recursive
    }


}
