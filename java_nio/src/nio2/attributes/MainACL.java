package nio2.attributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.util.List;

import static util.IOUtils.FORMAT;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainACL {
    public static void main(Path path) {
        try {
            System.out.printf("Owner: %s%n%n",
                    Files.getAttribute(path, "acl:owner"));
            @SuppressWarnings("unchecked")
            List<AclEntry> aclentries =
                    (List<AclEntry>) Files.getAttribute(path, "acl:acl");
            for (AclEntry aclentry : aclentries)
                System.out.printf("%s%n%n", aclentry);

            System.out.printf(FORMAT,"ACL Builder");


        }catch (IOException |UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n",e);
        }
    }
}
