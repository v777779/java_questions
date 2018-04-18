package java02.jmxmodel.jmxx;


import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

/**
 * Simple remote JMX client intended to show differences seen from client
 * perspective between a standard MBean and an MXBean.
 */
public class SimpleClient {
    public static String getServerStatusViaMBean(
            final MBeanServerConnection mbsc) {
        String statusToReturn = "";
        final String mbeanObjectNameStr = "example:type=Status";
        try {
            final ObjectName objectName = new ObjectName(mbeanObjectNameStr);
            final StatusMBean statusBeanProxy =
                    JMX.newMBeanProxy(mbsc, objectName, StatusMBean.class);
            statusToReturn = statusBeanProxy.getStatus().toString();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        return statusToReturn;
    }

    public static String getServerStatusViaMXBean(final MBeanServerConnection mbsc) {
        String statusToReturn = "";
        final String mbeanObjectNameStr = "example:type=Status2";
        try {
            final ObjectName objectName = new ObjectName(mbeanObjectNameStr);
            final StatusMXBean statusBeanProxy =
                    JMX.newMXBeanProxy(mbsc, objectName, StatusMXBean.class);
            statusToReturn = statusBeanProxy.getStatus().toString();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        return statusToReturn;
    }

    public static void main(final String[] arguments) {
        final String jmxUrlString =
                "service:jmx:rmi:///jndi/rmi://127.0.0.1:1199/jmxrmi";
        try {
            final JMXServiceURL jmxUrl = new JMXServiceURL(jmxUrlString);
            final JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
            final MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            System.out.println("MBean reports status: " + getServerStatusViaMBean(mbsc));
            System.out.println("MXBean reports status: " + getServerStatusViaMXBean(mbsc));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}  