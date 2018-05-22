import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private static void show() {
        List<Hello> list = new ArrayList<>();
        Random rnd = new Random();

        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("tick");
                if(rnd.nextBoolean()) {
                    for (int i = 0; i < 10; i++) {
                        list.add(new Hello());
                    }
                }else {
                    for (int i = 0; i < 10; i++) {
                        if(!list.isEmpty()) {
                            list.remove(0);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        SimpleAgent agent = new SimpleAgent();
        System.out.println("Select Local Process >> SimpleAgent >> Connect");
        System.out.println("MBeans >> SimpleAgent >> hellothere");
        System.out.println("SimpleAgent is running");
//        waitForEnterPressed();

        try {
            Runtime.getRuntime().exec("jconsole");
        } catch (IOException e) {
            e.printStackTrace();
        }
        show();


    }

}
