package nio2;

import nio2.async.AsyncFileRunner;
import nio2.async.AsyncSocketRunner;
import nio2.links.MainLinksRun;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 14-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main05A {

    public static void main(String[] args) {
        Path path = Paths.get(".", "data", "nio2");
        Path pathC = Paths.get(path.toString(), "async");
        Path pathD = Paths.get(path.toString(), "result_k.txt");
        BufferedReader br = null;
        FileInputStream in = null;

// async files
        AsyncFileRunner.main(args);
// async sockets
// async groups
        System.out.printf(FORMAT, "Asynchronous Channel Group:");
        AsyncSocketRunner.main(args);

// questions
//        FileSystems.getDefault().getPath("path");
//        FileSystems.getDefault().provider().getPath(URI.create("path"));
//        FileSystems.getDefault().getRootDirectories();
//        Paths.get("path");
//        System.out.println(path.relativize(pathC));
//        Path pathS = path.resolveSibling("nio");
//        System.out.println(pathS);
//        pathS.toUri();

// symbolic links
        MainLinksRun.main(args);

    }
}
