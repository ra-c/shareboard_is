package usecase.section.validator;

import domain.entity.Section;
import domain.repository.GenericRepository;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueSectionNameValidator implements ConstraintValidator<UniqueSection, String> {
    @Inject GenericRepository genericRepository;


    @Override
    public void initialize(UniqueSection uniqueSection) {

    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        if(name==null) return true;
        return genericRepository.findByNaturalId(Section.class, name) == null;
    }
}
