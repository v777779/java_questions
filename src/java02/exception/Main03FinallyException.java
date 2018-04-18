package java02.exception;

import java.io.IOException;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main03FinallyException {
    private static void f() {
        throw new NullPointerException();
    }


    private static int f2(int i) {
        try {

            if (i > 5) throw new Exception();
            return i;

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            i = 34;                             // не сработает для нормальных ситуаций
        }
        return i;
    }

    public static void main(String[] args) {

// IOException dimmed by NullPointerException
        try {
            try {
                System.out.println("Standard Message");
                throw new IOException();  // important exception
            } finally {
                f();  // Exception was dimmed
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

// finally return
        System.out.println("Standard Message with return");
        for (int i = 0; i < 10; i++) {
            System.out.println("f2():" +f2(i));  // возвращает finally только для i > 5 (Case of Exception)
        }

        String s = "123";

    }
}
