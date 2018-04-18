package java01.pkgb;

import java01.pkga.Main03Base;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03 extends Main03Base {
    int i = 2;

    private static abstract class M3A {
        private int m3a = 1;


    }

    private static class M3B extends M3A {
        private int m3b = 2;

    }

    private static class M3C extends M3A {
        private int m3c = 4;
    }

    private interface IM3A {
        int iM3a = 5;
        int iM3b = 6;


    }

    public static void main(String[] args) {
        Main03 m3 = new Main03();
        System.out.println(m3.show());

        int a = 1;
        float b = 1;

        String c1 = "",c2 = "", c3="";

        char n2 = '\uFF80';

        float c = a+b;

        System.out.printf("%4x\n",(int)n2);

// default access
        System.out.println(m3.i);
//        System.out.println(m3.p);

// abstract
        M3A m = new M3B();
        M3A m2 = new M3C();

        System.out.println(m.m3a + " " + ((M3B) m).m3b);
        System.out.println(m2.m3a + " " + ((M3C) m2).m3c);


    }


}
