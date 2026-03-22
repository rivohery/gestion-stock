package com.alibou.stockmanage.stocks.repositories;

import com.alibou.stockmanage.stocks.models.StockMovement;
import com.alibou.stockmanage.stocks.models.StockMovementProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @EntityGraph(attributePaths = {"product"})
    @Query("""
        Select 
            s as stockMovement, 
            ud as userDetails 
        from StockMovement s 
        INNER JOIN UserDetails ud
        ON ud.user.id = s.createdBy
        WHERE s.createdDate =:createdDate
    """)
    Page<StockMovementProjection>fetchAllPageOfStockByCreatedDate(@Param("createdDate") LocalDate createdDate, Pageable pageable);

    @EntityGraph(attributePaths = {"product"})
    @Query("""
        Select 
            s as stockMovement, 
            ud as userDetails 
        from StockMovement s 
        INNER JOIN UserDetails ud
        ON ud.user.id = s.createdBy
    """)
    Page<StockMovementProjection>fetchAllPageOfStock(Pageable pageable);

    @EntityGraph(attributePaths = {"product"})
    @Query("""
        Select 
            s as stockMovement, 
            ud as userDetails 
        from StockMovement s 
        INNER JOIN UserDetails ud
        ON ud.user.id = s.createdBy
    """)
    List<StockMovementProjection>fetchAllStock();

    @EntityGraph(attributePaths = {"product"})
    @Query("""
        Select 
            s as stockMovement, 
            ud as userDetails 
        from StockMovement s 
        INNER JOIN UserDetails ud
        ON ud.user.id = s.createdBy
        WHERE s.createdDate =:createdDate
    """)
    List<StockMovementProjection>fetchAllStockByCreatedDate(@Param("createdDate") LocalDate createdDate);

}
