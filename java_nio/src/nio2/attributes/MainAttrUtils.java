package nio2.attributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainAttrUtils {
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

    public static void main(String[] args) {

    }

}
