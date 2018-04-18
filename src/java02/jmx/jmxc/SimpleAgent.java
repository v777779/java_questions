package java02.jmx.jmxc;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 18-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class SimpleAgent {
    private MBeanServer mbs = null;

    public SimpleAgent() {

        mbs = ManagementFactory.getPlatformMBeanServer();

        Hello helloBean = new Hello();
        ObjectName helloName = null;

        try {
            helloName = new ObjectName("SimpleAgent:name=hellothere");
            mbs.registerMBean(helloBean,helloName);

        }catch (MalformedObjectNameException |InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    private static int waitForEnterPressed() {
        try {
            System.out.println("Press to continue...");
            return System.in.read();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("jconsole");  // console is invisible
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleAgent agent = new SimpleAgent();
        System.out.println("SimpleAgent is running");
        System.out.println("Select Local Process >> java02.jmx.jmxc.SimpleAgent >> Connect");
        System.out.println("  MBeans >> SimpleAgent >> hellothere");
        waitForEnterPressed();

    }

}
