package com.alibou.stockmanage.stocks.models;

import com.alibou.stockmanage.auths.models.UserDetails;

public interface StockMovementProjection {
    UserDetails getUserDetails();
    StockMovement getStockMovement();
}
