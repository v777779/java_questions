package java01.abstracts;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 12-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02 extends Main01 {
    public Main02(int n) {
        super(n);
    }

    public static void print2() {
        System.out.println("Main02DaemonFinally print()");
    }

    @Override
    protected Number show() throws IOException {
        System.out.println("Main02DaemonFinally show()");
        int n = 1;
        if (n == 1) throw new IOException();

        return 1;
    }

    @Override
    public void light() {
        System.out.println("Main02DaemonFinally light()");
    }

    public static void main(String[] args) {
        try {


            Main02 main02 = new Main02(1);

            main02.show();
            main02.voice();
            main02.light();
            IMain.print();  // только через класс интерфейса
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
