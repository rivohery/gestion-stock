package com.alibou.stockmanage.auths.web.controllers;

import com.alibou.stockmanage.auths.services.UserService;
import com.alibou.stockmanage.auths.web.dtos.CreateUserRequest;
import com.alibou.stockmanage.auths.web.dtos.UpdateUserInfoRequest;
import com.alibou.stockmanage.auths.web.dtos.UpdateUserStatusRequest;
import com.alibou.stockmanage.auths.web.dtos.UserDetailsResponse;
import com.alibou.stockmanage.shared.dtos.GlobalResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "users-endpoint")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse> createUser(
           @Valid @RequestBody CreateUserRequest request)
    {
        UserDetailsResponse userDetails = userService.createUser(request);
        if(userDetails != null){
            return new ResponseEntity<>(
                    GlobalResponse.builder()
                            .message("User was created successfully")
                            .build(),
                    HttpStatus.CREATED
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/get-user-authenticated")
    public ResponseEntity<UserDetailsResponse>getUserAuthenticated(
            Authentication connectedUser
    ){
        return ResponseEntity.ok(userService.getUserAuthenticated(connectedUser));
    }

    @GetMapping("/find-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserDetailsResponse>> findAllUser(
            @RequestParam(name="search", defaultValue = "") String search,
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10")int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        PageResponse<UserDetailsResponse>pageUsers = userService.findAllUser(search, pageable);
        if(pageUsers != null){
            return ResponseEntity.ok(pageUsers);
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/update-user-infos")
    public ResponseEntity<GlobalResponse>updateUserInfos(
         @Valid @RequestBody UpdateUserInfoRequest request,
         Authentication connectedUser
    ){
        UserDetailsResponse userDetails = userService.updateUserInfos(request, connectedUser);
        if(userDetails != null){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message("Infos utilisateur a été modifié avec succès")
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping(value = "/save-profile-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse>updateProfileImage(
         @RequestParam(name="userId", required = true)Long userId,
         @Parameter
         @RequestPart(name="file",required = true)MultipartFile file,
         Authentication connectedUser
    ) throws IOException {
        Long id = userService.saveProfileImage(userId, file, connectedUser);
        if(id != null){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message("Your profile was updated successfully")
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/download/{imageId}")
    public ResponseEntity<?>download(@PathVariable("imageId") Long imageId){
        byte[]images = userService.getProfileImageById(imageId);
        if(images != null && images.length > 0){
            String photo = Base64.encodeBase64String(images);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(photo);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>deleteUserById(@PathVariable("userId") Long userId){
        var isDeleted = userService.deleteUserById(userId);
        if(isDeleted){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("User of ID: %s was deleted successfully", userId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/update-user-status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>updateUserStatus(
         @Valid @RequestBody UpdateUserStatusRequest request
    ){
        var isStatusModified = userService.updateUserStatus(request);
        if(isStatusModified){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message(String.format("Status of user with ID: %s was modified successfully", request.id()))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }
}
