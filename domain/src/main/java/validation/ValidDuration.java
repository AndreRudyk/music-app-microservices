package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import validation.impl.DurationValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DurationValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDuration {
    String message() default "Duration must be in the format mm:ss and within valid time range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
