package nio2.watchers;

import nio2.walktree_CHECK_THIS.WalkUtils;
import util.IOUtils;

import java.io.IOException;
import java.nio.file.*;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 14-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainWatch {
    public static void main(String[] args) {
        System.out.printf(FORMAT, "Folder Watchers:");

        Path path = Paths.get(".", "data", "nio2");
        Path pathD = Paths.get(path.toString(), "temp", "copy", "walktree_CHECK_THIS");
        Path pathE = Paths.get(path.toString(), "temp", "walktree_CHECK_THIS");
        Path pathR = path.resolve("walkfolder");
        WatchService ws = null;

        try {
            WalkUtils.createUnderTree(pathD, path);
            WalkUtils.createUnderTree(pathR, path);
            WalkUtils.removeIfTree(pathE);


            WatchEvent.Kind[] kinds = new WatchEvent.Kind[]{
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.OVERFLOW
            };
            ws = FileSystems.getDefault().newWatchService();
        //    path.register(ws, kinds);      // nio2
            pathE.getParent().register(ws, kinds);      // temp
            pathR.register(ws, kinds);                  // walkfolder

            while (true) {
                WatchKey key;
                key = ws.take();
                for (WatchEvent event : key.pollEvents()) {
                    Path p = (Path) event.context();
                    WatchEvent.Kind k = event.kind();
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {  // it's possible == as static final
                        System.out.printf("path:%s event:%s%n", p, k);
                        continue;
                    }
                    System.out.printf("path:%s event:%s%n", p, k);
                }
                if(!key.reset()) {
                    System.out.printf("Key is invalid:%n");
                    break;
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(ws);
        }
    }
}
