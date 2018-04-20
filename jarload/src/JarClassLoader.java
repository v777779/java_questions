import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class JarClassLoader extends URLClassLoader {
    private URL url;


    public JarClassLoader(URL url) {   // replace with custom constructor
        super(new URL[]{url});
        this.url = url;
    }

    public void addURL(String s) {
        URL url = null;
        try{
            url = new URL(s);
            addURL(url);

        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getMainClassName() throws IOException {
        URL u = new URL("jar", "", url + "!/");              // url for jar
        JarURLConnection uc = (JarURLConnection) u.openConnection();
        Attributes attr = uc.getMainAttributes();                               // attributes from jar
        if (attr != null) return attr.getValue(Attributes.Name.MAIN_CLASS);
        return null;
    }

    public void invokeClass(String name, String[] args) throws
            ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException {
// reflection
        Class c = loadClass(name);
        Method m = c.getMethod("main", new Class[]{args.getClass()});  // String.class
        m.setAccessible(true);
        int mods = m.getModifiers();

        if(m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
        !Modifier.isPublic(mods)) {
            throw new NoSuchMethodException("main");                        // main not void, not static, not public
        }

        try {
            m.invoke(null,new Object[] {args});
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void getClass(String name) throws ClassNotFoundException {

        Class c = loadClass(name);

    }


}
