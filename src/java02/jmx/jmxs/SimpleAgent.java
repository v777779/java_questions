package java02.jmx.jmxs;

import java02.jmx.jmxc.Hello;

import javax.management.*;
import javax.swing.*;
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

        } catch (MalformedObjectNameException | InstanceAlreadyExistsException |
                MBeanRegistrationException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("jconsole");
        } catch (IOException e) {
            e.printStackTrace();
        }


        SimpleAgent agent = new SimpleAgent();
        System.out.println("SimpleAgent Swing is running");
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jFrame.setSize(800,600);
                jFrame.setVisible(true);
            }
        });


    }

}
