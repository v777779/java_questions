package nio2.attributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
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

            System.out.printf(FORMAT, "ACL Builder");


        } catch (IOException | UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n", e);
        }
    }

    public static FileAttribute<List<AclEntry>> getFileAttribute(final Path path) {

        return new FileAttribute<List<AclEntry>>() {
            @Override
            public String name() {
                return "acl:acl";
            }

            @SuppressWarnings("unchecked")
            @Override
            public List<AclEntry> value() {
                try {
                    return (List<AclEntry>) Files.getAttribute(path, "acl:acl");
                } catch (IOException | UnsupportedOperationException e) {
                    System.out.printf("Exception:%s%n", e);
                }
                return null;
            }
        };
    }

    public static FileAttribute<List<AclEntry>> attributes(Path path) throws IOException {
        final List<AclEntry> list = new ArrayList<>();
        List rawList = (List) Files.getAttribute(path, "acl:acl");
        for (Object o : rawList) {
            AclEntry aclEntry = (AclEntry) o;
            list.add(aclEntry);
        }
        return new FileAttribute<>() {
            @Override
            public String name() {
                return "acl:acl";
            }

            @Override
            public List<AclEntry> value() {
                return list;
            }
        };
    }


}
