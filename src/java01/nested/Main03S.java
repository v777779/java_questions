package java01.nested;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public final class Main03S {
    private static int staticA = 1;
    private int memberA = 2;

    public interface IShow {
        static void show() {

        }
    }

    public static class A implements IShow {
        public A(int n) {
        }

        public static void show() {
            System.out.println("Implementation A");

            System.out.println("static:" + staticA);
            System.out.println("member:" + new Main03S().memberA);

             int m = 2;  // effectively final initialized and not changed

            new A(new IShow() {
                private void mShow(int n) {
                    n = m + 1;
                    System.out.println("mShow :" + n);
                }

                private int getValue() {
                    return m + 2;
                }
            }.getValue());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(m);
                }
            }).start();

        }

        public void mShow() {
            System.out.println("member:" + new Main03S().memberA);
            int m = 1;
            new IShow() {
                private void mShow(int n) {
                    n = m + 1;
                    System.out.println("mShow :" + n);
                }
            }.mShow(1);
        }

    }

    public static class B implements IShow {
        public static void show() {
            System.out.println("Implementation B");
        }
    }


    public static void main(String[] args) {
        A.show();
        B.show();

        new A(1).mShow();
    }
}
