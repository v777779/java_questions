package nio2.links;

import nio2.files.MainFileUtils;

import java.io.IOException;
import java.nio.file.*;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainLinks {
    public static void main(String[] args) {

// Run in Administrative mode
// Run Admin console
//D:\__cources\_sandbox\java_questions>java -cp out/production/java_nio nio2.links.MainLinks

        System.out.printf(FORMAT, "Symbolic link:");
        Path path = Paths.get(".", "data", "nio2");
        Path pathC = Paths.get(".", "data", "nio2","temp","links");
        Path pathD = Paths.get(path.toString(), "result.txt");
        Path pathE = Paths.get(pathC.toString(), "result_link_s.txt");
        Path pathR = Paths.get(pathC.toString(), "result_link_h.txt");

        try {
            if(Files.exists(pathC)) {
                MainFileUtils.deleteFolderRegex(pathC,".*");
            }
            Files.createDirectories(pathC);

            Files.copy(pathD,pathC.resolve(pathD.getFileName()),StandardCopyOption.REPLACE_EXISTING);
// files links
            System.out.printf(FORMAT,"File links:");
            Files.createSymbolicLink(pathE,pathD);
            Files.createLink(pathR,pathD);
            System.out.printf("path:%-32s  exists:%b  symbolic:%b  regular:%b%n",pathE,
                    Files.exists(pathE), Files.isSymbolicLink(pathE),Files.isRegularFile(pathE));

            System.out.printf("path:%-32s  exists:%b  symbolic:%b  regular:%b%n",pathR,
                    Files.exists(pathR), Files.isSymbolicLink(pathR),Files.isRegularFile(pathR));

            System.out.printf(FORMAT,"Directory links:");
            path = Paths.get(".", "data", "nio2");
            pathC = Paths.get(".", "data", "nio2","links");
            pathD = Paths.get(path.toString(), "move");
            pathE = Paths.get(pathC.toString(), "move_link_s");
            pathR = Paths.get(pathC.toString(), "move_link_h");

            if(!Files.exists(pathD)) Files.createDirectory(pathD);

            System.out.printf("pathD:%-32s  exists:%b%n",pathD,Files.exists(pathD));
            System.out.printf("pathE:%-32s  exists:%b%n",pathE,Files.exists(pathE));
            System.out.printf("pathR:%-32s  exists:%b%n",pathR,Files.exists(pathR));

            System.out.printf("Create symbolic:%n");
            Files.createSymbolicLink(pathE,pathD);
            System.out.printf("sym  :%-32s  exists:%b%n",pathE,Files.exists(pathE));
            try {
                System.out.printf("Create hard:%n");
                Files.createLink(pathR, pathD);
                System.out.printf("hard :%-32s  exists:%b%n", pathR, Files.exists(pathR));
            }catch (AccessDeniedException e) {
                System.out.printf("Exception Hard Link not Allowed on Folder:%s%n",e);
            }

            System.out.printf("path:%-12s  exists:%b  symbolic:%b  regular:%b%n",pathE.getFileName(),
                    Files.exists(pathE), Files.isSymbolicLink(pathE),Files.isRegularFile(pathE));

            System.out.printf("path:%-12s  exists:%b  symbolic:%b  regular:%b%n",pathR.getFileName(),
                    Files.exists(pathR), Files.isSymbolicLink(pathR),Files.isRegularFile(pathR));

        } catch (IOException e) {
            System.out.printf("Exception:%s%n",e);
        }
    }
}
