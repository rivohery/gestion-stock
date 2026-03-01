package com.alibou.stockmanage.reporting.pdf.service.impl;

import com.alibou.stockmanage.reporting.pdf.common.PdfReport;
import com.alibou.stockmanage.reporting.pdf.service.StockMovementReportPdf;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class StockMovementReportPdfImpl extends PdfReport implements StockMovementReportPdf {

    @Override
    public byte[] exportToPdfReport(List<StockMovementDto> stockMovementList, LocalDate createdDate) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        //Bordure
        setRectangleInPdf(document);
        //Titre
        Paragraph title = new Paragraph("Historique des Mouvements de Stock" + "\n", getFont("Header"));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        //Paragraphe
        if(createdDate != null && !createdDate.isEqual(LocalDate.parse("2000-01-01"))){
            String data = "Créer le    :    "+ createdDate.format(formatter) + "\n";
            Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
        }

        // --- 3. Table de mouvement stock ---
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        // Entête de la table
        Stream.of("Produit", "Numéro Facture", "Quantité", "Type mouvement", "Date de création", "Employée")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                    header.setPadding(5);
                    table.addCell(header);
                });
        stockMovementList.forEach(item -> {
            table.addCell(item.getProductName());
            table.addCell(item.getReference());
            table.addCell("" + item.getQuantity());
            table.addCell(item.getType() == TypeMovement.IN ? "ENTREE" : "SORTIE");
            table.addCell(item.getCreatedDate().format(formatter));
            table.addCell(item.getEmployeName());
        });
        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private Font getFont(String type){
        log.info("Inside getFont method");
        switch (type) {
            case "Header" -> {
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            }
            case "Data" -> {
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            }
            default -> {
                return new Font();
            }
        }
    }
}
