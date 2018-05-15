package java_03.iterator;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 24-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main01MultiThreadConcurrent {


    public static void main(String[] args) {
        System.out.println("Concurrent Modification iterator:");
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        for (int i = 0; i < 10; i++) {
            new Thread(new MyRunner(list, i)).start();
        }

    }


    private static class MyRunner implements Runnable {
        private final List<String> list;
        private final int id;

        public MyRunner(List<String> list, int id) {
            this.list = list;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    if (id % 2 == 0) {
                        list.add("" + id);
                    } else {
                        Iterator<String> it = list.iterator();
                        while(it.hasNext()) {
                            it.next();
                        }
                    }
        System.out.println(id + " " + list);  // используется Iterator внутри StringBuilder.append
//        StringBuilder sb = new StringBuilder(id + " " + list);


                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
                System.exit(0);
            }

            System.out.println(id + "finished");
        }
    }

}
