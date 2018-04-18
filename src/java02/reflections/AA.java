package java02.reflections;

import java.lang.annotation.*;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 17-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */


@Documented
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD,ElementType.TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface AA {
   String  info() default "Annotation Info A";
   int value() default 1;
   String name() default "Name A";
}
