package com.github.gustavoflor.juca.usecase;

import com.github.gustavoflor.juca.entity.Modality;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Validated
public interface TransferBetweenWalletsUseCase {
    void execute(@Valid Payload payload);

    @Builder
    record Payload(@Positive @Digits(integer = 15, fraction = 2) BigDecimal amount,
                   @NotNull Modality from,
                   @NotNull Modality to,
                   @NotNull Long customerId) {
        @AssertFalse
        public boolean isToTheSameModality() {
            return from() == to();
        }
    }
}
