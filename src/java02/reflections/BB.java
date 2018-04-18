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
public @interface BB {
   String  info() default "Annotation Info B";
   int value() default 2;
   String name() default "Name B";
}
