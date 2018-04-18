package java02.jmxmodel.jmxx;


import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Scanner;

public class SimpleMBeanServer {
    public static void registerMBean(
            final MBeanServer mbs,
            final String mBeanObjectName,
            final Class mBeanClass) {
        try {
            final ObjectName name = new ObjectName(mBeanObjectName);
            final Object mBean = mBeanClass.newInstance();
            mbs.registerMBean(mBean, name);
        } catch (InstantiationException | // Class.newInstance()
                IllegalAccessException |   // Class.newInstance()
                MalformedObjectNameException | InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] arguments) {
        final String mbeanObjectNameStr = "example:type=Status";
        final String mxbeanObjectNameStr = "example:type=Status2";

        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        registerMBean(mbs, mbeanObjectNameStr, Status.class);
        registerMBean(mbs, mxbeanObjectNameStr, Status2.class);

        System.out.println("Press ENTER to exit...");
        final String dummyValue = new Scanner(System.in).nextLine();

    }
}  