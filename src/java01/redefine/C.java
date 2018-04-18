package java01.redefine;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class C extends B{
    public int show(int value) {
        System.out.println("Method show C:" + value);
        return value + 1;
    }

    public static void main(String[] args) {
        A a =  new A();
        B b =  new B();
        A c = new C();
// hiding
        a.show();
        c.show();

// overriding
        a.voice();
        c.voice();

// overriding
        a.show(1);
        b.show(1);
        c.show(1);

    }
}
