package nio2.attributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserPrincipal;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainOwner {
    public static void main(Path path) {
        try {
            System.out.printf("Owner: %s%n", Files.getOwner(path));
            UserPrincipal up = path.getFileSystem().
                    getUserPrincipalLookupService().
                    lookupPrincipalByName("jeff");
            System.out.println(up);
            Files.setOwner(path, up);
            System.out.printf("Owner: %s%n", Files.getOwner(path));

        } catch (IOException |UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n",e);
        }

    }
}
