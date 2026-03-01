package com.alibou.stockmanage.stocks.web.controllers;

import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.stocks.service.StockMovementService;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@Tag(name="stock-movement-endpoint")
@RequestMapping("/admin/movement-stock")
@RequiredArgsConstructor
public class StockMovementController {
    private final StockMovementService stockMovementService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<StockMovementDto>>findAllStockMovement(
            @RequestParam(name="createdDate",required = false, defaultValue = "2000-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdDate,
            @RequestParam(name="page", defaultValue = "0")int page,
            @RequestParam(name="size", defaultValue = "6")int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return ResponseEntity.ok(
                stockMovementService.findAllMovementStock(createdDate, pageable)
        );
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(name="createdDate", required = false, defaultValue = "2000-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdDate
    ){
        byte[]pdf = stockMovementService.exportPdf(createdDate);
        if(Objects.nonNull(pdf)){
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock-mouvement.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Resource> exportExcel(
            @RequestParam(name="createdDate", required = false, defaultValue = "2000-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdDate
    ){
        byte[]excel = stockMovementService.exportExcel(createdDate);
        if(Objects.nonNull(excel)){
            ByteArrayResource resource = new ByteArrayResource(excel);

            // Définir les en-têtes de la réponse
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.xlsx");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(excel.length)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(name="createdDate", required = false, defaultValue = "2000-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdDate
    ){
        byte[]csv = stockMovementService.exportCsv(createdDate);
        if(Objects.nonNull(csv)){
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stockMovement.csv")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csv);
        }
        return ResponseEntity.internalServerError().build();
    }

}
