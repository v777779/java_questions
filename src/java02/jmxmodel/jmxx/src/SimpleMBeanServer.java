package java02.jmxmodel.jmxx.src;

import java02.jmxmodel.jmxx.Status;
import java02.jmxmodel.jmxx.Status2;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class SimpleMBeanServer {
    public static void registerMBean(
            final MBeanServer mbs,
            final String mBeanObjectName,
            final Class mBeanClass) {
        try {
            final ObjectName name = new ObjectName(mBeanObjectName);
            final Object mBean = mBeanClass.newInstance();
            mbs.registerMBean(mBean, name);
        } catch (InstantiationException badInstance) { // Class.newInstance()
            System.err.println("Unable to instantiate provided class with class "
                    + "name " + mBeanClass.getName() + ":\n"
                    + badInstance.getMessage());
        } catch (IllegalAccessException illegalAccess)   // Class.newInstance()
        {
            System.err.println("Illegal Access trying to instantiate "
                    + mBeanClass.getName() + ":\n"
                    + illegalAccess.getMessage());
        } catch (MalformedObjectNameException badObjectName) {
            System.err.println(mBeanObjectName + " is a bad ObjectName:\n"
                    + badObjectName.getMessage());
        } catch (InstanceAlreadyExistsException duplicateMBeanInstance) {
            System.err.println(mBeanObjectName + " already existed as an MBean:\n"
                    + duplicateMBeanInstance.getMessage());
        } catch (MBeanRegistrationException mbeanRegistrationProblem) {
            System.err.println("ERROR trying to register " + mBeanObjectName + ":\n"
                    + mbeanRegistrationProblem.getMessage());
        } catch (NotCompliantMBeanException badMBean) {
            System.err.println("ERROR: " + mBeanObjectName + " is not compliant:\n"
                    + badMBean.getMessage());
        }
    }

    public static void main(final String[] arguments) {
        final String mbeanObjectNameStr = "example:type=Status";
        final String mxbeanObjectNameStr = "example:type=Status2";
        //final String mbean3ObjectNameStr = "example:type=Status3";
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        registerMBean(mbs, mbeanObjectNameStr, Status.class);
        registerMBean(mbs, mxbeanObjectNameStr, Status2.class);
        //registerMBean(mbs, mbean3ObjectNameStr, Status3.class);
        System.console().printf("Press ENTER to exit.");
        final String dummyValue = System.console().readLine();
    }
}  