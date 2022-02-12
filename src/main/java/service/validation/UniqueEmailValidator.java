package service.validation;

import persistence.repo.UserRepository;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Inject
    UserRepository userRepository;


    @Override
    public void initialize(UniqueEmail userExists) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        boolean check;
        try{
            check = userRepository.getByEmail(email) == null;
        }catch (NoResultException e){
            check = true;
        }
        return check;
    }
}