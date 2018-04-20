package java02.jmx.jmxr;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
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
            mbs.registerMBean(helloBean, helloName);
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
            cs.start();

        } catch (MalformedObjectNameException | InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException |
                IOException e) {
            e.printStackTrace();
        }
    }

    private static int waitForEnterPressed() {
        try {
            System.out.println("Press to continue...");
            return System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {

        if(args.length > 0 && args[0].equals("param")) {
            System.out.println("Indirect start, parameters used");
        }else {
            System.out.println("Direct start");

            try {
                Runtime.getRuntime().exec("rmiregistry 9999");
                Runtime.getRuntime().exec("jconsole");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SimpleAgent agent = new SimpleAgent();

        System.out.println("Remote Service:");
        System.out.println("Cope and Paste tot Remote Process:");
        System.out.println("service:jmx:rmi:///jndi/rmi://localhost:9999/server");

        System.out.println("SimpleAgent is running");
        waitForEnterPressed();
    }

}
