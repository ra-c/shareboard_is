package model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * L'identificativo deve corrispondere a un commento esistente
 * @see CommentExistsValidator
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CommentExistsValidator.class)
@Documented
public @interface CommentExists {
    String message() default "Comment must exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
