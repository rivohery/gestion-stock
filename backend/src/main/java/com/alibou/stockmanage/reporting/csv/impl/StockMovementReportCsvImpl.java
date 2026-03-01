package com.alibou.stockmanage.reporting.csv.impl;

import com.alibou.stockmanage.reporting.csv.StockMovementReportCsv;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class StockMovementReportCsvImpl implements StockMovementReportCsv {

    @Override
    public byte[] exportCsv(List<StockMovementDto> stockMovementList, LocalDate createdDate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);

        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        // En-têtes
        String[] headers = {"Produit", "Numéro Facture", "Quantité", "Type mouvement", "Date de création", "Employée"};
        csvWriter.writeNext(headers);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Données
        for (StockMovementDto stockMovement : stockMovementList) {
            String[] data = {
                    stockMovement.getProductName(),
                    String.format("FAC-%s", stockMovement.getReference()),
                    String.format("%s", stockMovement.getQuantity()),
                    stockMovement.getType() == TypeMovement.IN ? "ENTREE" : "SORTIE",
                    stockMovement.getCreatedDate().format(formatter),
                    stockMovement.getEmployeName()
            };
            csvWriter.writeNext(data);
        }

        csvWriter.close();
        return baos.toByteArray();
    }
}
