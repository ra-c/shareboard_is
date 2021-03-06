package model.validation;

import model.entity.Post;
import model.repository.GenericRepository;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostExistsValidator implements ConstraintValidator<PostExists, Integer> {
    @Inject
    GenericRepository genericRepository;

    @Override
    public void initialize(PostExists constraintAnnotation) {

    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value==null) return true;
        return (genericRepository.findById(Post.class, value) != null);
    }
}
