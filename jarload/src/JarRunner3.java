import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class JarRunner3 {
    private static final String[] JAR_FILES = new String[]{
            "app/jmxh.jar", "lib/jmxtools.jar", "lib/jmxri.jar"
    };

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

    private static JarClassLoader getJarLoader(String s) {
        URL url = null;
        try {
            url = new URL(s);
        } catch (IOException e) {
            fatal("Invalid URL: " + s);
        }

        return new JarClassLoader(url);
    }

    // независимая загрузка всех классов
    private static void loadJarClasses(String pathToJar) throws IOException, ClassNotFoundException {
        pathToJar = pathToJar.substring(9, pathToJar.length());
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);
        }
    }

    // независимая загрузка одного класса
    private static void loadJarClass(String pathToJar, String name) throws IOException {
        pathToJar = pathToJar.substring(9, pathToJar.length());
        try {
            File jarFile = new File(pathToJar);
            URLClassLoader loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});

            Class.forName(name, true, loader);
            System.out.println("Success");
            loader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Runtime load JAR app and JAR libraries with common loader and internal classpath:");
        System.out.println("Build module jmxh. Build Artifact Module jmxh: jmxh.jar");
        System.out.println("Copy from module jmxh: jmxh.jar to app/jmxh.jar");
        System.out.println("                       jmxtools.jar to lib/jmxtools.jar");
        System.out.println("                       jmxri.jar to lib/jmxri.jar");



        if (args.length < 1) {
//            usage();
            String path = JarRunner3.class.getResource(".").getPath();

            args = new String[JAR_FILES.length];
            for (int i = 0; i < JAR_FILES.length; i++) {
                args[i] = "file:///" + path+ JAR_FILES[i];
            }
        }


        JarClassLoader cl = getJarLoader(args[0]);  // loader JAR app
        cl.addURL(args[1]);  // analog -cp  add JAR to classpath
        cl.addURL(args[2]);

        String name = null;
        try {
            name = cl.getMainClassName();
        } catch (IOException e) {
            fatal("I/O error while loading JAR file", 1);
        }

        String[] newArgs = new String[args.length - 3];
        System.arraycopy(args, 3, newArgs, 0, newArgs.length);

        try {
//            loadJarClasses(args[1]);
//            loadJarClass(args[1],"com.sun.jdmk.comm.HtmlAdaptorServer");
            cl.loadClass("com.sun.jdmk.comm.HtmlAdaptorServer");
            cl.invokeClass(name, newArgs);
        } catch (ClassNotFoundException e) {
            fatal("Class not found: " + e, 1);
        } catch (NoSuchMethodException e) {
            fatal("No such method: " + e, 1);
        } catch (InvocationTargetException e) {
            fatal(e.getTargetException().toString(), 1);// to loara class which issue NoClassDefFoundError
        }

    }
}
