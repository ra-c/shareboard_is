package model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Il nome di sezione deve essere univoco
 * @see UniqueSectionNameValidator
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueSectionNameValidator.class)
@Documented
public @interface UniqueSection {
    String message() default "Section already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}