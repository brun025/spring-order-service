package com.order.demo.infrastructure.rest.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidUUID.UUIDValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {
    String message() default "Invalid UUID format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class UUIDValidator implements ConstraintValidator<ValidUUID, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) return false;
            try {
                UUID.fromString(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}