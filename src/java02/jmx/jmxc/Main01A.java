package java02.jmx.jmxc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 17-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01A {
    private static Random rnd = new Random();

    public static void main(String[] args) {
        List<Main01> list = new ArrayList<>();

        int counter = 0;
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("tick" + counter++ + " size:" + list.size());
                if (rnd.nextBoolean()) {
                    for (int i = 0; i < 10; i++) {
                        list.add(new Main01());
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        if (!list.isEmpty()) {
                            list.remove(0);
                        }
                    }
                }
//                Runtime.getRuntime().gc();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
