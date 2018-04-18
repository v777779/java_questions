package java02.annotation;

import java.lang.reflect.Method;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */
@CUserAnnotation(name="Main02A name")
public class Main02A extends Main01A {


    @UserAnnotation(info="M2A stopMethod")
    public void stopMethod() {

    }

    public static void main(String[] args) {
        Class c = Main02A.class;
        if(c.isAnnotationPresent(CUserAnnotation.class)) {
            CUserAnnotation a = (CUserAnnotation)c.getAnnotation(CUserAnnotation.class);
            System.out.println("Annotation: "+a.annotationType().getName()+" name:"+a.name()+" info:"+a.info());
        }

        boolean hasStart = false;
        boolean hasStop = false;

        Method[] methods = c.getMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(CUserAnnotationStart.class)) hasStart = true;
            if(method.isAnnotationPresent(CUserAnnotationStop.class)) hasStop = true;
            if(method.isAnnotationPresent(UserAnnotation.class)) {
                UserAnnotation a = (UserAnnotation)method.getAnnotation(UserAnnotation.class);
                System.out.println("Method string: " + a.info());
            }
        }
        System.out.println("Start present: "+hasStart);
        System.out.println("Stop  present: "+hasStop);
    }
}
