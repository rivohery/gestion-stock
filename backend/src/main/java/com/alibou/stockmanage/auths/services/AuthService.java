package com.alibou.stockmanage.auths.services;

import com.alibou.stockmanage.auths.exceptions.ActivationTokenException;
import com.alibou.stockmanage.auths.web.dtos.LoginRequest;
import com.alibou.stockmanage.auths.web.dtos.LoginResponse;
import com.alibou.stockmanage.auths.web.dtos.UpdatePasswordRequest;
import com.alibou.stockmanage.email.exception.SendingEmailException;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DisabledException;

import java.io.IOException;

public interface AuthService {
    /**
     * Méthode pour l'authentification basée sur email et mots de passe. Elle assure l'authentification et la gestion du refreshToken et accessToken
     * @param request objet contenant email et mots de passe de l'utilisateur
     * @param response type HttpServletResponse pour mettre le "Cookie" contenant le refreshToken
     * @return objet LoginResponse contenant accessToken
     */
    LoginResponse loginByPassword(LoginRequest request, HttpServletResponse response);

    /**
     * Méthode pour la gestion de mots de passe oublié
     * @param email
     * @throws EntityNotFoundException si aucun utilisateur ne correspond au email envoyé
     * @throws DisabledException si utilisateur existe mais non active
     * @throws SendingEmailException si un problème apparait lors de l'envoie d'email pour otpToken
     */
    void forgotPasswordProcess(@NonNull String email);

    /**
     * Méthode qui permet de s'authentifier à partir du token envoyé.
     * Elle suit le même principe que la méthode loginByPassword : gestion de "accessToken" et "refreshToken"
     * @param token chaine de caractère formé par six chiffres
     * @param response type HttpServletResponse pour mettre le "Cookie" contenant le refreshToken
     * @throws ActivationTokenException si le token est invalide (token qui n'existe pas dans la base de donnée)
     * @throws SendingEmailException si un problème apparait lors de l'envoie d'email pour la nouvelle otpToken si le token enregistré est expiré
     * @return objet LoginResponse contenant accessToken
     */
    LoginResponse loginByOTP(@NonNull String token, HttpServletResponse response);

    /**
     * Méthode qui permet de s'authentifier à partir du refreshToken lorsque accessToken est expiré.
     * Elle suit le même principe que la méthode loginByPassword : gestion de "accessToken" et "refreshToken" après la verification du refreshToken
     * @param request type HttpServletRequest pour récupérer la "Cookie"
     * @param response type HttpServletResponse pour mettre la "Cookie" contenant le refreshToken
     * @throws IOException
     * @return void
     */
    void loginByRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Méthode pour se déconnecter en supprimant la "Cookie" de refreshToken côté client
     * @param request
     * @param response
     */
    void logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * Pour Modifier les mots de passe d'un utilisateur sans s'authentifier
     * @param request de type UpdateUserPasswordRequest
     * @return userId
     * @throws EntityNotFoundException si aucun utilisateur ne correspond au champ email envoyé
     * @throws OperationNotPermittedException si le champ oldPassword ne correspond pas au current password
     */
    Long resetPassword(UpdatePasswordRequest request);
}
