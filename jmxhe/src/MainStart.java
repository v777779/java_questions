import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 19-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class MainStart {
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
        show();
    }
}
