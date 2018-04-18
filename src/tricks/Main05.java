package tricks;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 08-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main05 {
    static int a = 1111;

    static {
        System.out.println("static:");
        a = a-- - --a;  // 1111 >> (a--) 1110 >> (--a)1109 = 1111 - 1109
        System.out.println("static:"+a);
    }

    {
        System.out.println("object:"+a);
        a = a++ + ++a;  // 2 >>(a++) 3 >> (++a) 4 = 2+4 = 6
        System.out.println("object:"+a);
    }

    public static void main(String[] args) {
        System.out.println(a);

        Main05 m = new Main05();
        Main05 m2 = new Main05();
    }

}
