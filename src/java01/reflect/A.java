package java01.reflect;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 09-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
public class A {
    private static int sA;
    private int mA;

    private static class MyPolicyA extends Policy {
        @Override
        public PermissionCollection getPermissions(CodeSource codesource) {
            Permissions p = new Permissions();
            return p;
        }

    }
    private static class SM extends SecurityManager {
        @Override
        public void checkPackageAccess(String pkg){
            if(pkg.equals("java.lang.reflect")){
                throw new SecurityException("Reflection is not allowed!");
            }
        }
    }
    static { // protection
        System.out.println("Security Manager A1");
        System.setSecurityManager(new SM());  // no access to private fields and methods
        Policy.setPolicy(new MyPolicyA());
    }


    private String field = "I'm private field";
    private String show() {
// protection if SecurityManager is not set
        StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
        if(stackTraceElements.length > 5 && stackTraceElements[4].getClassName().contains("java.lang.reflect")) {
            throw new RuntimeException("This method is accessible via A1.class objects only");
        }

        return "I'm private method";
    }
}
