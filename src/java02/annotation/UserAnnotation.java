package java02.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
@Retention( RetentionPolicy.RUNTIME)  // SOURCE>>CLASS>>RUNTIME
public @interface UserAnnotation {
    String info() default "UserAnnotation info String";
}
