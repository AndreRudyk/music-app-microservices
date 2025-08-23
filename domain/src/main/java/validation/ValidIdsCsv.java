package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import validation.impl.CsvIntegerStringValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CsvIntegerStringValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIdsCsv {
    String message() default "CSV string format is invalid or exceeds length restrictions.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int maxLength() default Integer.MAX_VALUE;
}
