package kratos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by merlin on 15/12/21.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface LBindLayout {
    String value();
}
