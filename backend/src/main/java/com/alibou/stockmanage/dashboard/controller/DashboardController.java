package com.alibou.stockmanage.dashboard.controller;

import com.alibou.stockmanage.dashboard.dto.SummaryResponse;
import com.alibou.stockmanage.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name="dashboard-endpoint")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<SummaryResponse>dashboard(){
        var summaryResponse = dashboardService.dashboard();
        if(Objects.nonNull(summaryResponse)){
            return ResponseEntity.ok(summaryResponse);
        }
        return ResponseEntity.internalServerError().build();
    }
}
