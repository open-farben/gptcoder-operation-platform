package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SimpleClass<T> {
    @NotNull
    T body;
}
