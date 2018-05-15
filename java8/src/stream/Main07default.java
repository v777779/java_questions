package stream;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main07default {
    private interface IA {
        void print();

        static void calc(int x, int y) {
            System.out.println("IA calc plus");
        }

        static void reuse(int x, int y) {
            System.out.println("IA reuse calc");
        }

        default void mul(int x, int y) {
            System.out.println("IA calc mul");
        }
        default void div(int x, int y) {
            System.out.println("IA calc div");
        }
    }

    private interface IB {

        default void reuse(int x, int y) {
            System.out.println("IA reuse calc");
        }

    }

    private interface IC {

        default void count(int x, int y) {
            System.out.println("IC count calc");
        }


        default void average(int x, int y) {
            System.out.println("IC average calc");
        }
    }

    private interface ID  extends IC{

        default void reuse(int x, int y) {
            System.out.println("ID reuse calc");
        }

    }

    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        C c = new C();
        D d = new D();

        System.out.println("\nInterface IA:");
        IA.calc(1,2);
        IA.reuse(1,2);
//        A.reuse(1,2);
//        B.reuse(1,2);

        System.out.println("\nClass A:");
        A.calc(1,2);

        System.out.println("\nClass B:");
        B.calc(1,2);
        b.print();
        b.mul(1,2);

        System.out.println("\nClass C:");
        c.div(1,2);
        c.print();
        c.calc(1,2);
        c.reuse(1,2);

        System.out.println("\nClass D:");
        d.reuse(1,2);                   // collision
        d.count(1,2);
        d.average(1,2);



    }

    private static class A implements IA {
        static void calc(int x, int y) {
            System.out.println("A calc plus");
        }

        @Override
        public void print() {
            System.out.println("Class A print");

        }
    }

    private static class B extends  A {
        @Override
        public void mul(int x, int y) {
            System.out.println("B calc mul");
        }
    }

    private static class C extends  A implements IB, IC{

    }

    private static class D implements IB, ID{

        @Override
        public void count(int x, int y) {
            System.out.println("Class D count calc");
        }

        @Override
        public void reuse(int x, int y) {
            System.out.println("Class D reuse calc");
        }
    }

}
