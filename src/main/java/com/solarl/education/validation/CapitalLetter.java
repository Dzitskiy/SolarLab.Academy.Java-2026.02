package com.solarl.education.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CapitalLetterValidator.class)
public @interface CapitalLetter {

    String message() default "Поле должно начинаться с заглавной буквы";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
