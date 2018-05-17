package java_08.annot;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-May-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Main02B {
    private interface ExceptionHandler {
        void handleException(Throwable t);
    }

    @Target(ElementType.ANNOTATION_TYPE)  // for annotations only
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Catch {
        Class<? extends ExceptionHandler> targetCatchHandler();

        Class<? extends Throwable> targetException() default Exception.class;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface CatchGroup {
        Catch[] catchers();  // method gets Catch[] array
    }

    private interface Caller {
        void callMethod() throws Throwable;              // any Exception
    }

    private static class MethodCaller {
        private static void callMethod(Caller instance) throws Exception {
            Method m = instance.getClass().getMethod("callMethod");
            Annotation[] as = m.getAnnotations();
            Catch[] catches = null;
            for (Annotation a : as) {
                catches = ((CatchGroup) a).catchers();
            }

            try {
                instance.callMethod();
            } catch (Throwable e) {
                Class<?> ec = e.getClass();
                if (catches == null) return;
                for (Catch c : catches) {
                    if (c.targetException().equals(ec)) {
                        ExceptionHandler h = c.targetCatchHandler().newInstance();
                        h.handleException(e);
                        break;
                    }
                }
            }

        }

        static class Bar implements ExceptionHandler {

            @Override
            public void handleException(Throwable t) {
                System.out.println("NullPointerException: bar");
                System.out.println(t.getMessage());
            }
        }

        private static class Foo implements Caller {
            @Override
            @CatchGroup(catchers = {
                    @Catch(targetCatchHandler = Bar.class, targetException = ArithmeticException.class),
                    @Catch(targetCatchHandler = Bar.class, targetException = NullPointerException.class)
            })
            public void callMethod() throws Throwable {
                int a = 0;
                int b = 10;
                System.out.println(b/a);
            }

            public static void main(String[] args) throws Exception {
                Foo foo = new Foo();            // тестирование метода снаружи через Reflection и аннотации
                MethodCaller.callMethod(foo);   // метод сначала запускается, а потом ловятся его аннотации
            }
        }

    }

    public static void main(String[] args)throws Exception {
        MethodCaller.Foo.main(args);
    }
}
