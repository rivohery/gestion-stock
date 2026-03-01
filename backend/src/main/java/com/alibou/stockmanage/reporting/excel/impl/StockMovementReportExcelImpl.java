package com.alibou.stockmanage.reporting.excel.impl;

import com.alibou.stockmanage.reporting.excel.StockMovementReportExcel;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StockMovementReportExcelImpl implements StockMovementReportExcel {
    @Override
    public byte[] exportExcel(List<StockMovementDto> stockMovementList, LocalDate createdDate) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Créer un workbook Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        if (createdDate != null && !createdDate.isEqual(LocalDate.parse("2000-01-01"))) {
            sheet = workbook.createSheet("Liste de mouvement stock-le" + createdDate.format(formatter));
        } else {
            sheet = workbook.createSheet("Liste de mouvement stock");
        }
        // Créer l'en-tête
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Produit", "Numéro Facture", "Quantité", "Type mouvement", "Date de création", "Employée"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Remplir les données
        int rowNum = 1;
        for (StockMovementDto stockMovement : stockMovementList) {
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(stockMovement.getProductName());
            row.createCell(1).setCellValue("FAC-" + stockMovement.getReference());
            row.createCell(2).setCellValue(stockMovement.getQuantity());
            row.createCell(4).setCellValue(stockMovement.getType() == TypeMovement.IN ? "ENTREE" : "SORTIE");
            row.createCell(4).setCellValue(stockMovement.getCreatedDate().format(formatter));
            row.createCell(5).setCellValue(stockMovement.getEmployeName());
            rowNum++;
        }

        // Ajuster la taille des colonnes
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Écrire le workbook dans un ByteArrayOutputStream
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
