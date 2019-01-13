package bach.project.bean.annotation;

import bach.project.utils.RegexpValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegexpValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Regexp {
    String message() default "Value doesn't match given constraints";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String regexp();
    boolean caseSensitive() default false;

}
