package com.alibou.stockmanage.dashboard.service.impl;

import com.alibou.stockmanage.dashboard.dto.SummaryResponse;
import com.alibou.stockmanage.dashboard.service.DashboardService;
import com.alibou.stockmanage.products.repositories.ProductRepository;
import com.alibou.stockmanage.stocks.mappers.StockMovementMapper;
import com.alibou.stockmanage.stocks.repositories.StockMovementRepository;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import com.alibou.stockmanage.suppliers.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SupplierRepository supplierRepository;
    private final StockMovementMapper stockMovementMapper;

    @Override
    public SummaryResponse dashboard() {
        Long nbrProductActive = productRepository.getNbrProductActive();
        Long nbrProductNoActive = productRepository.getNbrProductNoActive();
        Long nbrProductEnAlert = productRepository.getNbrProductEnAlert();
        Long nbrSupplier = supplierRepository.count();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        List<StockMovementDto>lastMovementStock = stockMovementRepository.fetchAllPageOfStock(pageable)
                .stream()
                .map(stockMovementMapper::mapToDto)
                .toList();
        return SummaryResponse.builder()
                .nbrProductActive(nbrProductActive)
                .nbrProductNoActive(nbrProductNoActive)
                .nbrProductEnAlert(nbrProductEnAlert)
                .nbrSupplier(nbrSupplier)
                .lastMovementStock(lastMovementStock)
                .build();
    }
}
