package java01.abstracts;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03 extends Main02 {

    public Main03(int n) {
        super(n);
    }

    public static void print2() {
        System.out.println("Main02DaemonFinally print()");
    }

    @Override
    protected Integer show() {
        System.out.println("Main02DaemonFinally show()");

        int n = 1;


        return 1;
    }


    public static void main(String[] args) {
        Main03 main03 = new Main03(1);
        main03.show();
        main03.voice();
        main03.light();
        IMain.print();  // только через класс интерфейса

    }
}
