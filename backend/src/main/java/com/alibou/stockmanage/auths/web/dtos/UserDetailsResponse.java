package com.alibou.stockmanage.auths.web.dtos;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsResponse{
    private Long userId;
    private String fullName;
    private String profileImageUrl;
    private String phoneNu;
    private String email;
    private boolean enabled;
    private String role;

}
