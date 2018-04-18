package java02.annotation;

import java.lang.annotation.*;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 16-Apr-18
 * Email: vadim.v.voronov@gmail.com
 */

@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)  // полный цикл компиляция, класс, runtime
public @interface CUserAnnotationStop {
}
