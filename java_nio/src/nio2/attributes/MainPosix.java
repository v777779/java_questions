package nio2.attributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainPosix {
    public static void mainPosixPerm(Path path) {
        try {
            PosixFileAttributes pfa;
            pfa = Files.readAttributes(path, PosixFileAttributes.class);
            System.out.printf("Group: %s%n", pfa.group());
            for (PosixFilePermission perm : pfa.permissions())
                System.out.printf("Permission: %s%n", perm);
        } catch (IOException|UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n",e);
        }
    }

    public static void mainPosixAttr(Path path, String group, boolean setAttr) {

        try {
            System.out.printf("Group: %b%n",
                    Files.getAttribute(path, "posix:group"));
            @SuppressWarnings("unchecked")
            Set<PosixFilePermission> perms =
                    (Set<PosixFilePermission>)
                            Files.getAttribute(path, "posix: permissions");
            for (PosixFilePermission perm : perms)
                System.out.printf("Permission: %s%n", perm);
            if (setAttr) {
                GroupPrincipal gp = path.getFileSystem().
                        getUserPrincipalLookupService().
                        lookupPrincipalByGroupName(group);
                Files.setAttribute(path, "posix:group", gp);
                System.out.printf("Group: %b%n",
                        Files.getAttribute(path, "posix:group"));
            }
        }catch (IOException |UnsupportedOperationException e) {
            System.out.printf("Exception:%s%n",e);
        }
    }
}
