package org.msa.item.valid;

import org.msa.item.dto.constant.ItemType;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = ItemTypeValid.ItemTypeValidator.class)
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemTypeValid {
    String message() default "허용되지 않은 물품 유형입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};

    class ItemTypeValidator implements ConstraintValidator<ItemTypeValid, String> {

        @Override
        public boolean isValid(String cd, ConstraintValidatorContext context) {
            boolean hasItemType = false;
            ItemType[] itemTypeList = ItemType.values();
            for(ItemType i : itemTypeList) {
                hasItemType = i.hasItemCd(cd);
            }

            return hasItemType;
        }
    }
}