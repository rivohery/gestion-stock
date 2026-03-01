package com.alibou.stockmanage.stocks.service.impl;

import com.alibou.stockmanage.reporting.csv.StockMovementReportCsv;
import com.alibou.stockmanage.reporting.excel.StockMovementReportExcel;
import com.alibou.stockmanage.reporting.pdf.service.StockMovementReportPdf;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.stocks.mappers.StockMovementMapper;
import com.alibou.stockmanage.stocks.models.StockMovement;
import com.alibou.stockmanage.stocks.repositories.StockMovementRepository;
import com.alibou.stockmanage.stocks.service.StockMovementService;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;
    private final StockMovementReportPdf stockMovementReportPdf;
    private final StockMovementReportExcel stockMovementReportExcel;
    private final StockMovementReportCsv stockMovementReportCsv;

    @Override
    public PageResponse<StockMovementDto> findAllMovementStock(LocalDate createdDate, Pageable pageable) {
        Page<StockMovement>pages;
        if(createdDate != null && !createdDate.isEqual(LocalDate.parse("2000-01-01"))){
            pages = stockMovementRepository.findAllByCreatedDate(createdDate, pageable);
        } else {
            pages = stockMovementRepository.findAll(pageable);
        }

        List<StockMovementDto>content = pages.stream()
                .map(stockMovementMapper::mapToDto)
                .toList();

        return new PageResponse<>(
                content,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages(),
                pages.isFirst(),
                pages.isLast()
        );
    }

    @Override
    public byte[] exportPdf(LocalDate createdDate) {
        var stockMovementList = checkMovementStockList(createdDate);
        try{
            return stockMovementReportPdf.exportToPdfReport(stockMovementList, createdDate);
        } catch (DocumentException ex){
           ex.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] exportExcel(LocalDate createdDate) {
        var stockMovementList = checkMovementStockList(createdDate);
        try{
            return stockMovementReportExcel.exportExcel(stockMovementList, createdDate);
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] exportCsv(LocalDate createdDate) {
        var stockMovementList = checkMovementStockList(createdDate);
        try{
            return stockMovementReportCsv.exportCsv(stockMovementList, createdDate);
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private List<StockMovementDto>checkMovementStockList(LocalDate createdDate){
        List<StockMovement>stockMovementList;
        if(createdDate != null && !createdDate.isEqual(LocalDate.parse("2000-01-01"))){
            stockMovementList = stockMovementRepository.findAllByCreatedDate(createdDate);
        } else {
            stockMovementList = stockMovementRepository.findAll();
        }
        return stockMovementList.stream()
                .map(stockMovementMapper::mapToDto)
                .toList();
    }
}
