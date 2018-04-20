import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 20-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class JarRunner2MainStart {
    public static void main(String[] args) {
        System.out.println("Runtime load JAR app and JAR libraries with different loaders and external -cp classpath:");
        System.out.println("Build module jmxh. Build Artifact Module jmxh: jmxh.jar");
        System.out.println("Copy from module jmxh: jmxh.jar to app/jmxh.jar");
        System.out.println("                       jmxtools.jar to lib/jmxtools.jar");
        System.out.println("                       jmxri.jar to lib/jmxri.jar");
        try {
            Runtime.getRuntime().exec("cmd /c start java "+
                    "-cp out/production/jarload/;out/production/jarload/app/*;"+
                    "out/production/jarload/lib/* JarRunner2" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
