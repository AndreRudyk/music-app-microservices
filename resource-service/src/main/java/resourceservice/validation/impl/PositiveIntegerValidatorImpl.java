package resourceservice.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import resourceservice.validation.PositiveInteger;

public class PositiveIntegerValidatorImpl implements ConstraintValidator<PositiveInteger, String> {

    @Override
    public void initialize(PositiveInteger constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return intValue > 0;
    }
}
