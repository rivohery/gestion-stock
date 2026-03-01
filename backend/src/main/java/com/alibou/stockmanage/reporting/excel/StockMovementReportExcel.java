package com.alibou.stockmanage.reporting.excel;

import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface StockMovementReportExcel {
    byte[] exportExcel(List<StockMovementDto> transactions, LocalDate createdDate) throws IOException;
}
