package com.alibou.stockmanage.home;

import com.alibou.stockmanage.products.web.dtos.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>>checkListProduct(){
       return ResponseEntity.ok(homeService.checkListProduct());
    }
}
