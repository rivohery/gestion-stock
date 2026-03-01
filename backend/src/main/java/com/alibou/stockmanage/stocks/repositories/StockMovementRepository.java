package com.alibou.stockmanage.stocks.repositories;

import com.alibou.stockmanage.stocks.models.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Page<StockMovement>findAllByCreatedDate(LocalDate createdDate, Pageable pageable);
    List<StockMovement>findAllByCreatedDate(LocalDate createDate);
}
