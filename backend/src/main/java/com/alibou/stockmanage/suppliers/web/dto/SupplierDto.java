package com.alibou.stockmanage.suppliers.web.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierDto {
    private Long id;
    @NotBlank(message = "Name of provider is required")
    private String name;
    @NotBlank(message = "Email of provider is required")
    @Email(message = "Email invalid format")
    private String email;
    @NotBlank(message = "Phone number is required")
    private String phoneNu;
    @NotBlank(message = "Address of provider is required")
    private String address;
    private LocalDate createdAt;

    public SupplierDto(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
