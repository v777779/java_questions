package java01.nested;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02Anonymous {
    public static int publicStaticA = 1;
    private static int privateStaticA = 2;
    public int publicA = 3;
    private int privateA = 4;

    private static void staticShow() {
        System.out.println("Static show method");
    }

    private void show() {
        System.out.println("Member show method");
    }

    private interface IShow {
        void show();
    }

    public static void main(String[] args) {
        new Main02Anonymous().show();

// extends
        new Main02Anonymous() {
            private void show2() {
                System.out.println("Anonymous Extends show method");
            }

        }.show2();

// implements and extends
        new IShow() {
            private void show2() {
                System.out.println("Anonymous Interface show2 method");
            }

            @Override
            public void show() {
                System.out.println("Anonymous Interface show method");
                show2();
            }

        }.show();

    }

}
