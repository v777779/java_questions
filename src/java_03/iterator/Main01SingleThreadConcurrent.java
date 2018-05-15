package java_03.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 24-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01SingleThreadConcurrent {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        Iterator<String> it = list.iterator();
        list.remove(0);
        it.next(); // Concurrent
    }



}
