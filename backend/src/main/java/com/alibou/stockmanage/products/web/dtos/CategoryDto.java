package com.alibou.stockmanage.products.web.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {
    private Long id;
    @NotBlank(message = "le champ reference est requis")
    private String reference;
    @NotBlank(message = "le nom du catégorie est requis")
    private String name;

    public CategoryDto(Long id, String name){
        this.id = id;
        this.name =  name;
    }

}
