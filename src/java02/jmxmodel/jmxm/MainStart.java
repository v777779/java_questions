package java02.jmxmodel.jmxm;

import java.io.IOException;

import static java02.jmxmodel.jmxm.ModelMBeanDemonstrator.pause;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainStart {
    public static void main(String[] args) {
        System.out.println("Run MainStart ");
        System.out.println("Run JConsole");
        System.out.println("JConsole >> MBeans >> modelmbean");
        try {
            Runtime.getRuntime().exec("jconsole");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ModelMBeanDemonstrator me = new ModelMBeanDemonstrator();
        me.createAndRegisterRawModelMBean();
        pause(1000000);
    }
}
