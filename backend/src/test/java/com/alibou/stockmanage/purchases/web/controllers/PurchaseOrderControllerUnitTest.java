package com.alibou.stockmanage.purchases.web.controllers;

import com.alibou.stockmanage.auths.config.SecurityConfig;
import com.alibou.stockmanage.auths.utils.JwtFilter;
import com.alibou.stockmanage.purchases.models.PurchaseOrderStatus;
import com.alibou.stockmanage.purchases.services.PurchaseOrderService;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderItemRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderItemResponse;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderRequest;
import com.alibou.stockmanage.purchases.web.dtos.PurchaseOrderResponse;
import com.alibou.stockmanage.shared.config.AuditingConfig;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(
        controllers = PurchaseOrderController.class,
        excludeFilters = {
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuditingConfig.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        }
)
@AutoConfigureMockMvc(addFilters = false)
public class PurchaseOrderControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private ObjectMapper objectMapper;

    private Authentication mockAuthentication;
    private  PurchaseOrderRequest purchaseOrderRequest;

    @BeforeEach
    void setUp(){
        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn("testuser");

        purchaseOrderRequest = new PurchaseOrderRequest(
                "1234567",
                1L,
                LocalDate.now().plusDays(1),
                BigDecimal.valueOf(5000.0),
                Set.of(
                        new  PurchaseOrderItemRequest(10, 1L, null, BigDecimal.valueOf(2000.0)),
                        new  PurchaseOrderItemRequest(10, 2L, null, BigDecimal.valueOf(2000.0)),
                        new  PurchaseOrderItemRequest(10, 3L, null, BigDecimal.valueOf(1000.0))
                )
        );
    }

    @Test
    void generateUniqueInvoiceNo_shouldReturnInvoiceNoGenerated() throws Exception{
        //Given
        when(purchaseOrderService.generateUniqueInvoiceNo()).thenReturn("123456");
        //When-then
        mockMvc.perform(get("/stock/purchase-order/check-invoice-no")
                .principal(mockAuthentication))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceNo").value("123456"));
    }

    @Test
    void generateUniqueInvoiceNo_shouldReturnInternalServerError() throws Exception{
        //Given
        when(purchaseOrderService.generateUniqueInvoiceNo()).thenReturn(null);
        //When-then
        mockMvc.perform(get("/stock/purchase-order/check-invoice-no")
                        .principal(mockAuthentication))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void createPurchaseOrder_shouldReturnPurchaseOrderResponse() throws Exception {
        //Given
        PurchaseOrderResponse response = PurchaseOrderResponse.builder()
                .invoiceNo("1234567")
                .employee("user1")
                .receiveDate(LocalDate.now().plusDays(1))
                .status(PurchaseOrderStatus.PENDING)
                .supplierId(1L)
                .supplierName("John Doe")
                .totalAmounts(BigDecimal.valueOf(5000))
                .items(Set.of(
                        PurchaseOrderItemResponse.builder().totalItems(BigDecimal.valueOf(2000)).costPrice(BigDecimal.valueOf(200)).itemName("product1").quantity(10).build(),
                        PurchaseOrderItemResponse.builder().totalItems(BigDecimal.valueOf(2000)).costPrice(BigDecimal.valueOf(200)).itemName("product2").quantity(10).build(),
                        PurchaseOrderItemResponse.builder().totalItems(BigDecimal.valueOf(1000)).costPrice(BigDecimal.valueOf(100)).itemName("product3").quantity(10).build()
                ))
                .build();

        when(purchaseOrderService.createPurchaseOrder(purchaseOrderRequest)).thenReturn(response);
        //when-then
        mockMvc.perform(post("/stock/purchase-order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseOrderRequest))
                .principal(mockAuthentication)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        verify(purchaseOrderService, times(1)).createPurchaseOrder(any(PurchaseOrderRequest.class));
    }

    @Test
    void createPurchaseOrder_shouldReturnBadRequest_whenObjectRequestInvalid() throws Exception{
        //Given
        purchaseOrderRequest = new PurchaseOrderRequest(
                null,
                1L,
                LocalDate.now(),
                BigDecimal.valueOf(5000.0),
                Set.of(
                        new  PurchaseOrderItemRequest(10, 1L,null, BigDecimal.valueOf(2000.0)),
                        new  PurchaseOrderItemRequest(10, 2L, null, BigDecimal.valueOf(2000.0)),
                        new  PurchaseOrderItemRequest(10, 3L, null,  BigDecimal.valueOf(1000.0))
                )
        );
        //when-then
        mockMvc.perform(post("/stock/purchase-order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseOrderRequest))
                .principal(mockAuthentication)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invoice Number is required"));

        verifyNoInteractions(purchaseOrderService);
    }

    @Test
    void createPurchaseOrder_shouldHandleServiceException() throws Exception{
        //When
        purchaseOrderRequest = new PurchaseOrderRequest(
                "1234567",
                1L,
                LocalDate.now().plusDays(1),
                BigDecimal.valueOf(5000.0),
                Set.of(
                        new  PurchaseOrderItemRequest(10, 1L,null,  BigDecimal.valueOf(2000.0)),
                        new  PurchaseOrderItemRequest(10, 2L, null, BigDecimal.valueOf(2000.0)),
                        new  PurchaseOrderItemRequest(10, 3L,null, BigDecimal.valueOf(1000.0))
                )
        );

        Mockito.doThrow(new OperationNotPermittedException("Something work wrong in service"))
                .when(purchaseOrderService).createPurchaseOrder(purchaseOrderRequest);

        //When-then
        mockMvc.perform(post("/stock/purchase-order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseOrderRequest))
                .principal(mockAuthentication)
        ).andExpect(MockMvcResultMatchers.status().isNotAcceptable())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Something work wrong in service"));

        verify(purchaseOrderService, times(1)).createPurchaseOrder(any(PurchaseOrderRequest.class));
    }
}
