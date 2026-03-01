package com.alibou.stockmanage.stocks.mappers;

import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.stocks.models.StockMovement;
import com.alibou.stockmanage.stocks.web.dtos.StockMovementDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockMovementMapper {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StockMovementDto mapToDto(StockMovement stockMovement){
        var employeeDetails = userRepository.getEmployeeDetail(stockMovement.getCreatedBy()).orElseThrow(
                () -> new EntityNotFoundException("No entity UserDetails found")
        );
        StockMovementDto dto = new StockMovementDto();
        BeanUtils.copyProperties(stockMovement, dto);
        dto.setEmployeId(stockMovement.getCreatedBy());
        dto.setEmployeName(employeeDetails.getFullName());
        dto.setProductId(stockMovement.getProduct().getId());
        dto.setProductName(stockMovement.getProduct().getName());
        return dto;
    }
}
