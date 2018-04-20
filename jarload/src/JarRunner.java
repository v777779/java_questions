import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class JarRunner {

    private static void fatal(String s, int code) {
        System.err.println(s);
        System.exit(code);
    }

    private static void fatal(String s) {
        fatal(s, 0);

    }

    private static void usage() {
        fatal("Usage: java JarRunner [args...]");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
//            usage();
          String path = JarRunner.class.getResource(".").getPath();
          String app =  path+"app/jmxm.jar";
          args = new String[] { "file:///"+app};

        }

        URL url = null;
        try {
            url = new URL(args[0]);
        } catch (IOException e) {
            fatal("Invalid URL: " + args[0]);
        }

        JarClassLoader cl = new JarClassLoader(url);
        String name = null;
        try {
            name = cl.getMainClassName();
        } catch (IOException e) {
            fatal("I/O error while loading JAR file", 1);
        }

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);

        try {
            cl.invokeClass(name, newArgs);
        } catch (ClassNotFoundException | NoSuchMethodException |InvocationTargetException e) {
            fatal("Class load error: " + e, 1);
        }

    }
}
