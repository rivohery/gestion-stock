package com.alibou.stockmanage.sales.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillOrderMinResponse {
    private long id;
    private String invoiceNo;
    private String customer;
    private String paymentMethod;
    private BigDecimal total;
    private LocalDate createdDate;
}
