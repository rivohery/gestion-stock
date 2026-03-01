package com.alibou.stockmanage.home;

import com.alibou.stockmanage.products.web.dtos.ProductResponse;

import java.util.List;

public interface HomeService {
    List<ProductResponse> checkListProduct();
}
