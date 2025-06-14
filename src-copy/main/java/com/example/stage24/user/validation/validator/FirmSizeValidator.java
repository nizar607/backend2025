package com.example.stage24.user.validation.validator;

/*

import com.example.stage24.user.domain.RoleType;
import com.example.stage24.user.domain.User;
import com.example.stage24.validation.annotation.ValidFirmSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FirmSizeValidator implements ConstraintValidator<ValidFirmSize, User> {

    @Override
    public void initialize(ValidFirmSize constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (
                user
                        .getRoles()
                        .stream()
                        .anyMatch(role -> RoleType.ROLE_LAWYER.name().equals(role.getName().name()) )
        ) {
            return user.getFirmSize() != null && user.getFirmSize() >= 0;
        }
        return true; // If not a lawyer, firmSize validation is not applied
    }
}

 */