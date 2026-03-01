package com.alibou.stockmanage.reporting.pdf.common;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.itextpdf.text.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static java.io.File.separator;

@Slf4j
public class PdfReport {

    protected void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    protected void createDirIfNotExists(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    protected  boolean isFileExist(String path){
        log.info("Inside isFileExist {}", path);
        try{
            File file = new File(path);
            return (file != null && file.exists())? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    protected byte[] getByteArray(String filePath) throws Exception{
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[]byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    protected void addTableHeader(PdfPTable table, Object[] columnTitles) {
        log.info("Inside addTableHeader method");
        for (Object columnTitle : columnTitles) {
            PdfPCell header = new PdfPCell();
            header.setBorderColor(BaseColor.DARK_GRAY);
            header.setBorderWidth(1);
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setPhrase(new Phrase(columnTitle.toString(), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            header.setPadding(5);
            table.addCell(header);
        }
    }
}
