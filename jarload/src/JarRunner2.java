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
public class JarRunner2 {

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

        return  new JarClassLoader(url);
    }

    private static void loadJarClasses(String pathToJar) throws IOException,ClassNotFoundException {
        pathToJar = pathToJar.substring(9,pathToJar.length());
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);
        }
    }


    private static void loadJarClass(String pathToJar, String name) throws IOException{
        pathToJar = pathToJar.substring(9,pathToJar.length());
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
        if (args.length < 1) {
//            usage();
          String path = JarRunner2.class.getResource(".").getPath();
          String app  =  path+"app/jmxh.jar";
          String lib  =  path+"lib/jmxtools.jar";
          String lib2 =  path+"lib/jmxri.jar";
          args = new String[] { "file:///"+app, "file:///"+lib, "file:///"+lib2};

        }


        JarClassLoader cl = getJarLoader(args[0]);
        JarClassLoader cl2 = getJarLoader(args[1]);
        JarClassLoader cl3= getJarLoader(args[2]);


        String name = null;
        try {
            name = cl.getMainClassName();
        } catch (IOException e) {
            fatal("I/O error while loading JAR file", 1);
        }

        String[] newArgs = new String[args.length - 3];
        System.arraycopy(args, 3, newArgs, 0, newArgs.length);

        try {
            loadJarClass(args[1],"com.sun.jdmk.comm.HtmlAdaptorServer");

            cl.invokeClass(name, newArgs);
        } catch (ClassNotFoundException  e ) {
            fatal("Class not found: " + e, 1);
        } catch (NoSuchMethodException  e) {
            fatal("No such method: "+e,1);
        }catch (InvocationTargetException e) {
            fatal(e.getTargetException().toString(), 1);
        }catch (IOException e) {
            fatal("Jar load error: "+e,1);
        }

    }
}
