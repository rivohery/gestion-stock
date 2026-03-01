package com.alibou.stockmanage.reporting.pdf.service.impl;

import com.alibou.stockmanage.reporting.pdf.common.PdfReport;
import com.alibou.stockmanage.reporting.pdf.service.BillOrderReportPdf;
import com.alibou.stockmanage.sales.models.BillOrder;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static java.io.File.separator;

@Service
@Slf4j
public class BillOrderReportPdfImpl extends PdfReport implements BillOrderReportPdf {

    private static final String INVOICE_STORAGE_DIR = "./pdf" + separator + "invoices";

    @Override
    @Async
    public void generateInvoice(BillOrder billOrder) {
        Document document = new Document();
        try {
            createDirIfNotExists(INVOICE_STORAGE_DIR);

            PdfWriter.getInstance(document, new FileOutputStream(INVOICE_STORAGE_DIR  + separator + billOrder.getInvoiceNo() + ".pdf"));
            document.open();
            //--- 0.insert document element start
            setRectangleInPdf(document);

            // --- 1. En-tête (Nom Société) ---
            Font mainFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph companyName = new Paragraph("SOCIÉTÉ XYZ", mainFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            document.add(new Paragraph("\n")); // Espace

            // --- 2. Infos Facture & Client (Utilisation d'un tableau à 2 colonnes) ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Gauche : Infos Facture

            infoTable.addCell("N° Facture : FAC-2026-" + billOrder.getInvoiceNo() +"\n" +
                                   "Date : " + billOrder.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );

            // Droite : Infos Client
            PdfPCell clientCell = new PdfPCell(new Paragraph(
                    "Client: " + billOrder.getCustomer() + "\n" +
                            "Tél: " + billOrder.getPhoneNu() + "\n" +
                            "Email: " + billOrder.getEmail() + "\n" +
                            "Paiement: " + billOrder.getPaymentMethod() + "\n"
            ));
            clientCell.setBorder(Rectangle.NO_BORDER);
            clientCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            infoTable.addCell(clientCell);

            document.add(infoTable);
            document.add(new Paragraph("\n\n"));

            // --- 3. Table des Produits ---
            PdfPTable table = new PdfPTable(4); // 4 colonnes
            table.setWidthPercentage(100);

            // Entête de la table
            Stream.of("Produit", "Quantité", "Prix Unitaire", "Total items")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setPhrase(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                        header.setPadding(5);
                        table.addCell(header);
                    });

            billOrder.getItems().forEach(item -> {
                table.addCell(item.getProduct().getName());
                table.addCell("" + item.getQuantity());
                table.addCell(item.getProduct().getCostPrice() +" €");
                table.addCell(item.getTotalItems() + " €");
            });
            document.add(table);

            // --- 4. Total ---
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph total = new Paragraph("\nTOTAL DE LA COMMANDE : "+ billOrder.getTotal() +" €", boldFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // --- 5. Note de bas de page ---
            document.add(new Paragraph("\n\n"));
            Font italicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Cette facture est générée électroniquement et ne nécessite pas de signature." , italicFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getPdf(String filename) {
        log.info("uuid: {}",filename);
        try{
            if(StringUtils.isBlank(filename)){
                return new byte[0];
            }
            String filePath = INVOICE_STORAGE_DIR + separator + filename +".pdf";
            if(isFileExist(filePath)){
                return getByteArray(filePath);
            } else {
                throw new RuntimeException("File Not Found");
            }
        } catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }
}
