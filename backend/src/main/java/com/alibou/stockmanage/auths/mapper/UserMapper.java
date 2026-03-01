package com.alibou.stockmanage.auths.mapper;

import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.web.dtos.UserDetailsResponse;

public interface UserMapper {

    UserDetailsResponse mapToUserDetailsResponse(User user);
}
