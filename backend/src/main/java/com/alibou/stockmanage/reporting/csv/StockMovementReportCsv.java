package com.alibou.stockmanage.reporting.csv;

import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface StockMovementReportCsv {
    byte[] exportCsv(List<StockMovementDto> transactions, LocalDate createdDate) throws IOException;
}
