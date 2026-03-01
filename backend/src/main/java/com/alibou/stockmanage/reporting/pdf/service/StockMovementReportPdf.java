package com.alibou.stockmanage.reporting.pdf.service;

import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import com.itextpdf.text.DocumentException;

import java.time.LocalDate;
import java.util.List;

public interface StockMovementReportPdf {

    byte[] exportToPdfReport(List<StockMovementDto>stockMovementList, LocalDate createdDate) throws DocumentException;
}
