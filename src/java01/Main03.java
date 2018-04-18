package java01;

import java.util.HashMap;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03 {
    private interface A {
        int sum(int x, int y);
        static int avg(int x, int y) {
            return 0;
        }
    }

    private static class B implements A {

        public int diff(int x, int y) {
            return x - y;
        }

        @Override
        public int sum(int x, int y) {
            return x+y;
        }
    }

    private static class C extends B {

        public int mult(int x, int y) {
            return x * y;
        }


        public int diff(int x, int y) {
            return y-x;
        }
    }


    public static void main(String[] args) {
        A aB = new B();
        A aC = new C();
        B bB = new B();
        B bC = new C();
        C cC = new C();

        System.out.println(aB.sum(1,1));
//        System.out.println(aC.mult(1,1));
        System.out.println(bB.diff(2,1));
        System.out.println(bC.diff(2,1));
        System.out.println(cC.mult(2,2));


        HashMap<Integer, String> map = new HashMap<>();
        map.put(5,null);


    }


}
