package validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import validation.ValidIdsCsv;
import java.util.Arrays;

public class CsvIntegerStringValidator implements ConstraintValidator<ValidIdsCsv, String> {
    private int maxLength;

    @Override
    public void initialize(ValidIdsCsv constraintAnnotation) {
        maxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
           Arrays.stream(value.split(","))
                    .forEach(Integer::parseInt);
        } catch (NumberFormatException e) {
            return false;
        }
        return value.length() < maxLength;
    }
}
