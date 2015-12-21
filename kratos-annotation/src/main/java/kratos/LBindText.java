package kratos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by merlin on 15/12/4.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface LBindText {
    String[] value();
}
