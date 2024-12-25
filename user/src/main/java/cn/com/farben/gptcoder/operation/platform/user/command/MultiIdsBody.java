package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MultiIdsBody {
    @Size(min = 1)
    List<Long> ids;
    @NotNull
    String body;
    String language;
}
