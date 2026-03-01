package com.alibou.stockmanage.reporting.pdf.service;
import com.alibou.stockmanage.sales.models.BillOrder;

public interface BillOrderReportPdf {
  void generateInvoice(BillOrder billOrder);
  byte[] getPdf(String filename);
}
