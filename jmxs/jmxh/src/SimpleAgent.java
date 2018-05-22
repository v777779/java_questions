import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Scanner;

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
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();

        Hello helloBean = new Hello();
        ObjectName adapterName = null;
        ObjectName helloName = null;

        try {
            helloName = new ObjectName("SimpleAgent:name=helloThere");
            mbs.registerMBean(helloBean, helloName);

            adapterName = new ObjectName("SimpleAgent:name=htmladapter,port=8000");
            adapter.setPort(8000);
            mbs.registerMBean(adapter, adapterName);
            adapter.start();

//            JMXServiceURL url = new JMXServiceURL("service:jmxc:rmi:///jndi/rmi://localhost:9999/server");
//            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
//            cs.start();

        } catch (MalformedObjectNameException | InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    private static int waitForEnterPressed() {
            System.out.println("Press to continue...");
            new Scanner(System.in).nextLine();
            System.exit(0);
        return -1;
    }

    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("jconsole");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleAgent agent = new SimpleAgent();
        System.out.println("Html Adapter Service:");
        System.out.println("SimpleAgent is running");
        System.out.println("Open Firefox URL localhost:8000 ");
        System.out.println("Look at JConsole  jmxh.jar >> MBeans >> SimpleAgent >> hellothere ");
        waitForEnterPressed();

    }

}
