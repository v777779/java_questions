package java01.classloader;

import java01.classloader.bin.A1;
import java01.classloader.bin.B1;
import java01.classloader.bin.D1;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 10-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main {
    public static void main(String[] args) {
        A a = new A();                                                      // статическая загрузка класса
        B b = new B();
        try {
            Class classA1 = Class.forName("java01.classloader.bin.A1");      // динамическая загрузка класса forName
            ClassLoader loader = D1.class.getClassLoader();
            Class cD1 = loader.loadClass("java01.classloader.bin.D1"); // динамическая загрузка класса ClassLoader
            Class cB1 = loader.loadClass("java01.classloader.bin.B1");

            A1 a1 = (A1) classA1.newInstance();
            A1 d1 = (D1) cD1.newInstance();
            A1 b1 = (B1) cB1.newInstance();
            System.out.println(a1 + " " + d1 + " " + b1);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
