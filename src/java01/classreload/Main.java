package java01.classreload;

import java01.classreload.bin.A1;
import java01.classreload.bin.B1;
import java01.classreload.bin.C1;
import java01.classreload.bin.D1;

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
            Class classA1 = Class.forName("java01.classreload.bin.A1");      // динамическая загрузка класса forName
            ClassLoader loader = B1.class.getClassLoader();
            Class cB1 = loader.loadClass("java01.classreload.bin.B1");

            A1 a1 = (A1) classA1.newInstance();
            A1 b1 = (B1) cB1.newInstance();
            System.out.println(a1 + " " + b1);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        try {

            ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
            MyClassLoader classLoader = new MyClassLoader(parentClassLoader);
            Class myObjectClass = classLoader.loadClass("java01.classreload.bin.C1");

            C1 object1 = (C1)myObjectClass.newInstance();
            Object object2 =                     myObjectClass.newInstance();

            //create new class loader so classes can be reloaded.
            classLoader = new MyClassLoader(parentClassLoader);
            myObjectClass = classLoader.loadClass("java01.classreload.bin.C1");

            object1 = (C1) myObjectClass.newInstance();
            object2 = (D1) myObjectClass.newInstance();

        }catch ( ClassNotFoundException | IllegalAccessException  | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
