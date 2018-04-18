package java01.redefine;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class A {
    public static void show() {
        System.out.println("Static Method A1.class");
    }

    public void voice() {
        System.out.println("Method voice() A1");
    }

    int show(int value) {
        System.out.println("Method show A:" + value);
        return value + 1;
    }
}
