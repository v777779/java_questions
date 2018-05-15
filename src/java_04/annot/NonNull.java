package java_04.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 15-May-18
 * Email: vadim.v.voronov@gmail.com
 */
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonNull {
}
