package com.alibou.stockmanage.products.repositories;

import com.alibou.stockmanage.products.models.Product;
import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    boolean existsByCode(String code);

    @Query("""
        select p from Product p where lower(p.name) like lower(concat('%', :search, '%'))
    """)
    Page<Product>searchByNameLike(@Param("search")String search, Pageable pageable);

    @Query("""
        Select new com.alibou.stockmanage.products.web.dtos.ProductResponse(p.id, p.name, p.costPrice, p.qtyStock) 
        from Product p where p.supplier.id = :supplierId
    """)
    List<ProductResponse> findBySupplierId(@Param("supplierId") Long supplierId);

    @Query("""
        Select new com.alibou.stockmanage.products.web.dtos.ProductResponse(p.id, p.name, p.qtyStock, p.salesPrice) 
        from Product p where p.category.id =:categoryId and p.isActive = true
    """)
    List<ProductResponse> findAllByCategoryId(@Param("categoryId")Long categoryId);

    @Query("Select count(*) from Product p where p.isActive = true")
    Long getNbrProductActive();

    @Query("Select count(*) from Product p where p.isActive = false")
    Long getNbrProductNoActive();
    @Query("Select count(*) from Product p where p.alertStock >= p.qtyStock")
    Long getNbrProductEnAlert();

}
