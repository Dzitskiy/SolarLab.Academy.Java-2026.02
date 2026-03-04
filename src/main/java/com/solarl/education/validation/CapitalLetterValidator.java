package com.solarl.education.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CapitalLetterValidator implements ConstraintValidator<CapitalLetter, String> {

    @Override
    public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {
        if (text != null && !text.isEmpty()) {
            return Character.isUpperCase(text.charAt(0));
        }
        return true;
    }

}