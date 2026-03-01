package com.alibou.stockmanage.auths.services.impl;

import com.alibou.stockmanage.auths.mapper.UserMapper;
import com.alibou.stockmanage.auths.models.*;
import com.alibou.stockmanage.auths.repositories.ProfileImageRepository;
import com.alibou.stockmanage.auths.repositories.RoleRepository;
import com.alibou.stockmanage.auths.repositories.UserDetailsRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.auths.services.UserService;
import com.alibou.stockmanage.auths.web.dtos.CreateUserRequest;
import com.alibou.stockmanage.auths.web.dtos.UpdateUserInfoRequest;
import com.alibou.stockmanage.auths.web.dtos.UpdateUserStatusRequest;
import com.alibou.stockmanage.auths.web.dtos.UserDetailsResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final String DOWNLOAD_URL_PATH = "/users/download";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ProfileImageRepository profileImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final static String DEFAULT_PASSWORD = "0000";

    @Override
    public UserDetailsResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new OperationNotPermittedException(String.format("Email already exist in database: %s", request.email()));
        }
        Set<Role> roles = new HashSet<>();
        for (RoleEnum roleName : request.roles()) {
            roleRepository.findByName(roleName).ifPresent(roles::add);
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .enabled(true)
                .roles(new HashSet<>())
                .build();
        user = userRepository.save(user);
        for(Role role: roles){
            user.getRoles().add(role);
        }
        user = userRepository.save(user);
        UserDetails userDetails = UserDetails.builder()
                .user(user)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNu(request.phoneNu())
                .build();
        userDetailsRepository.save(userDetails);
        return userMapper.mapToUserDetailsResponse(user);
    }

    @Override
    public UserDetailsResponse getUserAuthenticated(Authentication connectedUser) {
        User user = ((UserPrincipal) connectedUser.getPrincipal()).getUser();
        if (user != null) {
            return userMapper.mapToUserDetailsResponse(user);
        }
        return null;
    }

    @Override
    public PageResponse<UserDetailsResponse> findAllUser(String search, Pageable pageable) {
        Page<User> userPages = userRepository.getAllUser(search, pageable);
        return new PageResponse<>(
                userPages.getContent()
                        .stream()
                        .map(u -> userMapper.mapToUserDetailsResponse(u))
                        .toList(),
                userPages.getNumber(),
                userPages.getSize(),
                userPages.getTotalElements(),
                userPages.getTotalPages(),
                userPages.isFirst(),
                userPages.isLast()
        );
    }

    @Override
    public UserDetailsResponse updateUserInfos(UpdateUserInfoRequest request, Authentication connectedUser) {
        var user = userRepository.findById(request.userId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No object User match with ID: %s", request.userId()))
        );

        User userConnected = ((UserPrincipal) connectedUser.getPrincipal()).getUser();
        if (userConnected == null || !Objects.equals(user.getId(), userConnected.getId())) {
            throw new OperationNotPermittedException("You are not authorized to update this resource");
        }

        if (StringUtils.isNotBlank(request.email())
                && !user.getEmail().equals(request.email())
        ) {
            var isEmailExist = userRepository.existsByEmail(request.email());
            if (isEmailExist) {
                throw new OperationNotPermittedException(String.format("Email already exist: %s", request.email()));
            }
            user.setEmail(request.email());
            user = userRepository.save(user);
        }

        if (request.firstName() != null || request.lastName() != null || request.phoneNu() != null) {
            var userDetails = userDetailsRepository.findByUser(user).orElseThrow(
                    () -> new EntityNotFoundException(String.format("No Object UserDetails match with user with Id %s", request.userId()))
            );
            if (StringUtils.isNotBlank(request.firstName())
                    && !userDetails.getFirstName().equals(request.firstName())
            ) {
                userDetails.setFirstName(request.firstName());
            }
            if (StringUtils.isNotBlank(request.lastName())
                    && !userDetails.getLastName().equals(request.lastName())
            ) {
                userDetails.setLastName(request.lastName());
            }
            if (StringUtils.isNotBlank(request.phoneNu())
                    && !userDetails.getPhoneNu().equals(request.phoneNu())
            ) {
                userDetails.setPhoneNu(request.phoneNu());
            }
            userDetailsRepository.save(userDetails);
        }
        return userMapper.mapToUserDetailsResponse(user);
    }

    @Override
    public Long saveProfileImage(
            Long userId,
            MultipartFile file,
            Authentication connectedUser
    ) throws IOException {
        var user = checkAndIsAuthenticatedUser(userId, connectedUser);

        if(file != null && !file.isEmpty()) {
            var profileImage = profileImageRepository.getProfileByUser(user).orElse(null);
            if(Objects.isNull(profileImage)){
                var userDetails = userDetailsRepository.findByUser(user).orElseThrow(
                        ()-> new EntityNotFoundException("No object UserDetails found in database")
                );

                Long profileImageId = buildAndSaveProfileImage(file, userDetails,null);

                userDetails.setProfilImageUrl(String.join("/", DOWNLOAD_URL_PATH, profileImageId.toString()));
                userDetailsRepository.save(userDetails);
                return profileImageId;
            }
            return buildAndSaveProfileImage(file, null, profileImage);
        }
        return null;
    }

    @Override
    public byte[] getProfileImageById(Long profileImageId) {
        var profileImage = profileImageRepository.findById(profileImageId).orElseThrow(
                () -> new EntityNotFoundException("Entity ProfileImage not found")
        );
        return profileImage.getImages();
    }

    @Override
    public boolean deleteUserById(Long userId) {
        var deletedUser = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No user match with ID: %s", userId))
        );
        userRepository.delete(deletedUser);
        return true;
    }

    @Override
    public boolean updateUserStatus(UpdateUserStatusRequest request) {
        var user = userRepository.findById(request.id()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No user match with ID: %s", request.id()))
        );
        user.setEnabled(request.enabled());
        userRepository.save(user);
        return true;
    }

    private Long buildAndSaveProfileImage(
            MultipartFile file,
          @Nullable UserDetails userDetails,
          @Nullable  ProfileImage profileImage
    ) throws IOException {
        if(Objects.isNull(profileImage)){
            profileImage = new ProfileImage();
            profileImage.setUserDetails(userDetails);
        }
        profileImage.setFileName(file.getOriginalFilename());
        profileImage.setFileType(file.getContentType());
        profileImage.setImages(file.getBytes());
        return profileImageRepository.save(profileImage).getId();
    }

    private User checkAndIsAuthenticatedUser(Long userId, Authentication connectedUser){
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No object User match with ID: %s", userId))
        );

        User userConnected = ((UserPrincipal) connectedUser.getPrincipal()).getUser();
        if (userConnected == null || !Objects.equals(user.getId(), userConnected.getId())) {
            throw new OperationNotPermittedException("You are not authorized to update the resource");
        }
        return user;
    }


}
