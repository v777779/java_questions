package java01.redefine;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class B extends A{
    public static void show() {
        System.out.println("Static Method B1.class");
    }

    public void voice() {
        System.out.println("Method voice() B1");
    }

    protected int show(int n) {
        System.out.println("Method show B:" + n);
        return n + 1;
    }
}
