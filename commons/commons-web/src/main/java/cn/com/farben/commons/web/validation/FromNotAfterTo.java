package cn.com.farben.commons.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * from日期不能晚于to日期
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FromNotAfterToValidator.class)
public @interface FromNotAfterTo {
    String message() default "`from`日期不能晚于`to`日期";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
