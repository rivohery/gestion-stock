package com.alibou.stockmanage.shared.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalResponse {
    private LocalDateTime timestamps;
    private int status;
    private String message;
    private Map<String,Object> data;
}