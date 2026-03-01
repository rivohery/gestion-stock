package com.alibou.stockmanage.auths.repositories;

import com.alibou.stockmanage.auths.models.ProfileImage;
import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage>findByUserDetails(UserDetails userDetails);

    @Query("""
        Select pi from ProfileImage pi 
        where pi.userDetails.user =:user
    """)
    Optional<ProfileImage>getProfileByUser(@Param("user") User user);
}
