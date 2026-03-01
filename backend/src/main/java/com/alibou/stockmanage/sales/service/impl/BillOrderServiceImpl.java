package com.alibou.stockmanage.sales.service.impl;

import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.repositories.CategoryRepository;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.products.web.dtos.CategoryDto;
import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import com.alibou.stockmanage.reporting.pdf.service.BillOrderReportPdf;
import com.alibou.stockmanage.sales.exceptions.InsufficientQtyStockException;
import com.alibou.stockmanage.sales.mappers.BillOrderMapper;
import com.alibou.stockmanage.sales.models.BillOrder;
import com.alibou.stockmanage.sales.models.BillOrderItem;
import com.alibou.stockmanage.sales.repositories.BillOrderRepository;
import com.alibou.stockmanage.sales.service.BillOrderService;
import com.alibou.stockmanage.sales.web.dto.BillOrderItemRequest;
import com.alibou.stockmanage.sales.web.dto.BillOrderMinResponse;
import com.alibou.stockmanage.sales.web.dto.BillOrderRequest;
import com.alibou.stockmanage.sales.web.dto.BillOrderResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.shared.utils.InvoiceNoGenerator;
import com.alibou.stockmanage.stocks.models.StockMovement;
import com.alibou.stockmanage.stocks.models.TypeMovement;
import com.alibou.stockmanage.stocks.repositories.StockMovementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillOrderServiceImpl implements BillOrderService {

    private static final int INVOICE_NO_LENGTH = 6;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BillOrderRepository billOrderRepository;
    private final StockMovementRepository stockMovementRepository;
    private final BillOrderMapper billOrderMapper;
    private final BillOrderReportPdf billOrderReportPdf;

    @Override
    public String checkUniqueInvoiceNo() {
        boolean isUniqueInvoiceNo = true;
        String invoiceNo = null;
        while (isUniqueInvoiceNo) {
            invoiceNo = InvoiceNoGenerator.generateInvoiceNo(INVOICE_NO_LENGTH);
            isUniqueInvoiceNo = billOrderRepository.existsByInvoiceNo(invoiceNo);
        }
        return invoiceNo;
    }

    @Override
    public List<CategoryDto> findAllCategory() {
        return categoryRepository.getCategoryList();
    }

    @Override
    public List<ProductResponse> findAllProductByCategoryId(Long categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public BillOrderResponse findBillOrderById(Long billOrderId) {
        return billOrderMapper.mapToBillOrderResponse(
                billOrderRepository.findById(billOrderId).orElseThrow(
                        () -> new EntityNotFoundException(String.format("No entity BillOrder found with ID: %s", billOrderId))
                )
        );
    }

    @Override
    public PageResponse<BillOrderMinResponse> findAllOrder(Pageable pageable) {
        Page<BillOrder>pages = billOrderRepository.findAll(pageable);
        return new PageResponse<>(
                pages.stream()
                     .map(billOrderMapper::mapToBillOrderMinResponse)
                     .toList(),
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalElements(),
                pages.getTotalPages(),
                pages.isFirst(),
                pages.isLast()
        );
    }

    @Override
    public boolean deleteBillOrderById(Long billOrderId) {
        var billOrder = billOrderRepository.findById(billOrderId).orElseThrow(
                ()-> new EntityNotFoundException(String.format("No entity order found with ID : %s", billOrderId))
        );
        billOrderRepository.delete(billOrder);
        return true;
    }

    @Override
    public byte[] getInvoicePdf(String invoiceNo) {
        var billOrder = billOrderRepository.findByInvoiceNo(invoiceNo).orElseThrow(
                ()-> new EntityNotFoundException(String.format("No order found with invoice of number: %s", invoiceNo))
        );
        return billOrderReportPdf.getPdf(billOrder.getInvoiceNo());
    }

    @Override
    public BillOrderResponse createBillOrder(BillOrderRequest request) {
        var billOrder = billOrderMapper.mapToBillOrder(request);
        for(BillOrderItemRequest billOrderItemRequest : request.items()){
            var billOrderItem = billOrderMapper.mapToBillOrderItem(billOrderItemRequest, billOrder);
            billOrder.getItems().add(billOrderItem);
            decreaseQtyProduct(billOrderItem.getProduct(), billOrderItemRequest.quantity());
            saveMovementStock(billOrder, billOrderItem);
        }
        billOrder = billOrderRepository.save(billOrder);
        //générer facture
        billOrderReportPdf.generateInvoice(billOrder);
        return billOrderMapper.mapToBillOrderResponse(billOrder);
    }

    private void decreaseQtyProduct(Product product, int quantity){
        var currentQtyStock = product.getQtyStock();
        if(currentQtyStock < quantity){
            throw new InsufficientQtyStockException(String.format("Quantity stock of %s insufficient", product.getName()));
        }
        product.setQtyStock(product.getQtyStock() - quantity);
        productRepository.save(product);
    }

    private void saveMovementStock(BillOrder billOrder, BillOrderItem item){
        var stockMovement = StockMovement.builder()
                .type(TypeMovement.OUT)
                .reference(billOrder.getInvoiceNo())
                .quantity(item.getQuantity())
                .product(item.getProduct())
                .build();
        stockMovementRepository.save(stockMovement);
    }

}
