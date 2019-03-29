package bach.project.bean.validators;

import bach.project.bean.annotation.Regexp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegexpValidator implements ConstraintValidator<Regexp,String> {
    private String regexp;
    private boolean caseSensitive;

    @Override
    public void initialize(Regexp constraintAnnotation) {
        this.regexp=constraintAnnotation.regexp();
        this.caseSensitive=constraintAnnotation.caseSensitive();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(!caseSensitive)
            value=value.toLowerCase();
        return value.matches(regexp);
    }
}
