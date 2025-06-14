package com.example.stage24.user.validation.validator;

/*

import com.example.stage24.user.domain.RoleType;
import com.example.stage24.user.domain.User;
import com.example.stage24.validation.annotation.CreatedByRequired;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class CreatedByValidator implements ConstraintValidator<CreatedByRequired, User> {

    @Override
    public void initialize(CreatedByRequired constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user == null) {
            return true; // Not the responsibility of this validator to check null user objects.
        }
        boolean roleMatch = user
                .getRoles()
                .stream()
                .anyMatch(role -> RoleType.ROLE_AGENT.name().equals(role.getName().name()) || RoleType.ROLE_CLIENT.name().equals(role.getName().name()));
        if (roleMatch) {
            return user.getCreatedBy() != null;
        }
        return true;
    }
}

 */
