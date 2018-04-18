package java01.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.*;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class B {
    static {
        System.out.println("Security Manager B1");
        Policy.setPolicy(new MyPolicy());                   // set own Security Manager
        System.setSecurityManager(new SecurityManager());
    }

    public B() {
        System.out.println("Security Manager B1");
        Policy.setPolicy(new MyPolicy());                   // restore, if class tried to set own SM
        System.setSecurityManager(new SecurityManager());   // Attention. Works in Debug mode only
    }

    public static void main(String[] args) {
        A a = new A();                                      // set own new Security Manager closed reflection
        B b = new B();                                      // rewrite Security Manager open reflection
        try {
// private field
            Field field = a.getClass().getDeclaredFields()[0];
            field.setAccessible(true);
            String value = (String) field.get(a);
            System.out.println(field.getName() + ": " + value);
// private method
            Method method = a.getClass().getDeclaredMethods()[0];  // метод все таки защищен
            method.setAccessible(true);
            System.out.println(method.getName() + ": " + method.invoke(a));
        } catch (InvocationTargetException | IllegalAccessException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private static class MyPolicy extends Policy {
        @Override
        public PermissionCollection getPermissions(CodeSource codesource) {
            Permissions p = new Permissions();
            p.add(new AllPermission());
            return p;
        }
    }

}
