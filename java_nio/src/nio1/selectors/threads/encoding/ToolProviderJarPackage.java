package nio1.selectors.threads.encoding;

import java.util.spi.ToolProvider;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 03-Jul-18
 * Email: vadim.v.voronov@gmail.com
 */
public class ToolProviderJarPackage {
    public static void main(String[] args) {
         ToolProvider javaPackager = ToolProvider.findFirst("javapackager").orElseThrow(IllegalArgumentException::new);
        System.out.println(javaPackager);

    }
}
