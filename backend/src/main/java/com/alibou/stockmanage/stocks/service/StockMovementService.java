package com.alibou.stockmanage.stocks.service;

import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface StockMovementService {
    PageResponse<StockMovementDto>findAllMovementStock(LocalDate createdDate, Pageable pageable);

    byte[]exportPdf(LocalDate createdDate);

    byte[] exportExcel(LocalDate createdDate);

    byte[] exportCsv(LocalDate createdDate);
}
