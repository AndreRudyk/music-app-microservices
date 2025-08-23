package validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import validation.ValidDuration;

public class DurationValidator implements ConstraintValidator<ValidDuration, String> {
    @Override
    public void initialize(ValidDuration constraintAnnotation) {
    }

    @Override
    public boolean isValid(String duration, ConstraintValidatorContext context) {
        if (duration == null) {
            return true;
        }
        String[] parts = duration.split(":");
        if (parts.length != 2) {
            return false;
        }
        try {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return minutes >= 0 && minutes <= 59 && seconds >= 0 && seconds <= 59;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}