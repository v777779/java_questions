package nio2.attributes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 07-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainUserDef {
    private final String[] USER_ATTRS = {
            "w", "l", "r", "d"
    };

    public static void main(Path path, String attr) {
        try {
            UserDefinedFileAttributeView udfav =
                    Files.getFileAttributeView(path,
                            UserDefinedFileAttributeView.class);
            switch (attr.charAt(0)) {
                case 'W':
                case 'w':
                    udfav.write("files.description",
                            Charset.defaultCharset().encode("sample"));
                    break;
                case 'L':
                case 'l':
                    for (String name : udfav.list())
                        System.out.println(name);
                    break;
                case 'R':
                case 'r':
                    int size = udfav.size("files.description");
                    ByteBuffer buf = ByteBuffer.allocateDirect(size);
                    udfav.read("files.description", buf);
                    buf.flip();
                    System.out.println(Charset.defaultCharset().decode(buf));
                    break;
                case 'D':
                case 'd':
                    udfav.delete("files.description");

            }
        } catch (IOException e) {
            System.out.printf("Exception:%s%n", e);
        }
    }

}
