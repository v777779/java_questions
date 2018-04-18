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
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
        ElementType.PARAMETER, ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)  // полный цикл компиляция, класс, runtime
public @interface CUserAnnotationStart {
}
