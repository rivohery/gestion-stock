package com.alibou.stockmanage.dashboard.dto;

import com.alibou.stockmanage.stocks.models.StockMovement;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryResponse {
    private Long nbrProductActive;
    private Long nbrProductNoActive;
    private Long nbrProductEnAlert;
    private Long nbrSupplier;
    private List<StockMovementDto>lastMovementStock;
}
