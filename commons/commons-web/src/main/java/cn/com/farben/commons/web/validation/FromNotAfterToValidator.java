package cn.com.farben.commons.web.validation;

import cn.com.farben.commons.web.command.BaseFromToDateCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.Objects;

public class FromNotAfterToValidator implements ConstraintValidator<FromNotAfterTo, BaseFromToDateCommand> {
    @Override
    public boolean isValid(BaseFromToDateCommand baseFromToDateCommand, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate from = baseFromToDateCommand.getFrom();
        LocalDate to = baseFromToDateCommand.getTo();
        if (Objects.nonNull(from) && from.isAfter(LocalDate.now())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(String.format("from日期(%s)不能晚于当前日期", from))
                    .addConstraintViolation();
            return false;
        }
        if (Objects.nonNull(from) && Objects.nonNull(to) && from.isAfter(to)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(String.format("from日期(%s)不能晚于to日期(%s)", from, to))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
