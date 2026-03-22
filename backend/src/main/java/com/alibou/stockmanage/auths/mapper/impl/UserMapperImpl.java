package com.alibou.stockmanage.auths.mapper.impl;

import com.alibou.stockmanage.auths.mapper.UserMapper;
import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.web.dtos.UserDetailsResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMapperImpl implements UserMapper {

    private final UserDetailsRepository userDetailsRepository;

    @Override
    public UserDetailsResponse mapToUserDetailsResponse(User user) {
        if(user == null){
            log.info("User Object is null");
            return null;
        }
        UserDetails userDetails;
        if(Objects.isNull(user.getUserDetails())){
            userDetails = userDetailsRepository.findByUser(user).orElseThrow(
                    () -> new EntityNotFoundException(String.format("No object UserDetails found with userId: %s", user.getId()))
            );
            log.info("UserDetails is null");
        } else {
            userDetails = user.getUserDetails();
        }
        return UserDetailsResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .fullName(userDetails.getFullName())
                .phoneNu(userDetails.getPhoneNu())
                .profileImageUrl(userDetails.getProfilImageUrl())
                .role(String.join(",", user.getRoles().stream().map(r -> r.getName().name()).toList()))
                .build();
    }
}
