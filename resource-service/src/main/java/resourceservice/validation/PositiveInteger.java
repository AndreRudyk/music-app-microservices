package resourceservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import resourceservice.validation.impl.PositiveIntegerValidatorImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PositiveIntegerValidatorImpl.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveInteger {

    String message() default "Invalid ID. The ID must be a positive integer.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
