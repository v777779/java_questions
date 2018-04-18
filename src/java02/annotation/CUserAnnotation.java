package java02.annotation;

import java.lang.annotation.*;


@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
        ElementType.PARAMETER, ElementType.CONSTRUCTOR,
        ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)  // полный цикл компиляция, класс, runtime
public @interface CUserAnnotation {
    String info() default "UserAnnotation info String";
    String name();
    Class mClass() default Main01A.class;

    int getInt() default  5;
    long distance() default 5L;

}
