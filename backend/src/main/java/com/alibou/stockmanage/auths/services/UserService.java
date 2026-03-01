package com.alibou.stockmanage.auths.services;

import com.alibou.stockmanage.auths.web.dtos.CreateUserRequest;
import com.alibou.stockmanage.auths.web.dtos.UpdateUserInfoRequest;
import com.alibou.stockmanage.auths.web.dtos.UpdateUserStatusRequest;
import com.alibou.stockmanage.auths.web.dtos.UserDetailsResponse;
import com.alibou.stockmanage.shared.dtos.PageResponse;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    /**
     * Méthode pour créer un utilisateur en attribuant leur rôle
     * @param request
     * @throws OperationNotPermittedException si email de l'utilisateur existe deja
     * @return UserDetailsResponse : objet contenant l'information de l'utilisateur
     */
    UserDetailsResponse createUser(CreateUserRequest request);


    /**
     * Méthode pour obtenir des informations sur l'utilisateur authentifié
     * @param connectedUser objet Authentication
     * @return UserDetailsResponse
     */
    UserDetailsResponse getUserAuthenticated(Authentication connectedUser);


    /**
     * Méthode pour obtenir la liste des utilisateurs et les filtrés par nom ou prénom
     * @param search peut-être une chaine vide
     * @param pageable
     * @return liste paginée
     */
    PageResponse<UserDetailsResponse>findAllUser(String search, Pageable pageable);


    /**
     * Méthode qui permet à l'utilisateur authentifié de modifier des informations sur son profile
     * @param request
     * @throws EntityNotFoundException si aucun utilisateur ne correspond à l'ID envoyé dans la requête ou
     * @throws OperationNotPermittedException si l'utilisateur qui modifie l'information ne correspond pas à l'utilisateur connecté
     * @throws OperationNotPermittedException si le nouveau adresse email envoyé existe deja si l'utilisateur modifie son email
     * @throws EntityNotFoundException si aucun objet UserDetails ne correspond à l'objet User trouvé
     * @return UserDetailsResponse objet qui contient les informations modifiées (pour le teste)
     */
    UserDetailsResponse updateUserInfos(UpdateUserInfoRequest request, Authentication connectedUser);


    /**
     * Méthode pour ajouter|modifier le profile image de l'utilisateur
     * @param userId non null
     * @param file Objet MultipartFile non null
     * @throws EntityNotFoundException si aucun utilisateur ne correspond à l'ID envoyé dans la requête
     * @throws OperationNotPermittedException si l'utilisateur qui modifie l'information ne correspond pas à l'utilisateur connecté
     * @throws EntityNotFoundException si aucun objet UserDetails ne correspond à l'utilisateur authentifié dans le cas ôu il n'a pas encore un objet ProfileImage crée
     * @return Id de l'objet ProfileImage
     * @throws IOException
     */
    Long saveProfileImage(
            @NonNull Long userId,
            @NonNull MultipartFile file,
            Authentication connectedUser
    ) throws IOException;


    /**
     * Méthode pour récupérer l'image d'un utilisateur en base de donnée
     * @param profileImageId
     * @return
     */
    byte[] getProfileImageById(@NonNull  Long profileImageId);

    /**
     * Méthode pour supprimer un utilisateur par son ID
     * @param userId ID de l'utilisateur supprimé
     * @throws EntityNotFoundException si aucun utilisateur ne correspond à ID envoyé
     * @return true si l'utilisateur est supprimé
     */
    boolean deleteUserById(@NonNull Long userId);


    /**
     * Méthode pour activer/désactiver un utilisateur
     * @param request objet UpdateUserStatusRequest contenant l'ID d'utilisateur modifié et son nouveau status
     * @throws EntityNotFoundException si aucun utilisateur ne correspond à ID envoyé
     * @return true si le status d'utilisateur est modifié
     */
    boolean updateUserStatus(@NonNull UpdateUserStatusRequest request);



}
