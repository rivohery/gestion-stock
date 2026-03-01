package com.alibou.stockmanage.auths.services.impl;

import com.alibou.stockmanage.auths.config.SecurityConstant;
import com.alibou.stockmanage.auths.exceptions.ActivationTokenException;
import com.alibou.stockmanage.auths.models.OTPToken;
import com.alibou.stockmanage.auths.models.RefreshToken;
import com.alibou.stockmanage.auths.models.User;
import com.alibou.stockmanage.auths.models.UserPrincipal;
import com.alibou.stockmanage.auths.repositories.OTPTokenRepository;
import com.alibou.stockmanage.auths.repositories.RefreshTokenRepository;
import com.alibou.stockmanage.auths.repositories.UserRepository;
import com.alibou.stockmanage.auths.services.AuthService;
import com.alibou.stockmanage.auths.utils.JwtService;
import com.alibou.stockmanage.auths.utils.TokenGenerator;
import com.alibou.stockmanage.auths.web.dtos.LoginRequest;
import com.alibou.stockmanage.auths.web.dtos.LoginResponse;
import com.alibou.stockmanage.auths.web.dtos.UpdatePasswordRequest;
import com.alibou.stockmanage.email.exception.SendingEmailException;
import com.alibou.stockmanage.email.service.EmailService;
import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final OTPTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public LoginResponse loginByPassword(LoginRequest request, HttpServletResponse response) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        if (authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            saveAndPrepareCookieForRefreshToken(userPrincipal, response);
            return buildLoginResponse(userPrincipal);
        }
        return null;
    }

    @Override
    public void forgotPasswordProcess(String email) {
        Optional<User> optional = userRepository.findByEmail(email);
        if(optional.isEmpty()){
            throw new EntityNotFoundException(String.format(SecurityConstant.USER_NOT_FOUND_MSG, email));
        }

        if(!optional.get().isEnabled()){
            throw new DisabledException(SecurityConstant.USER_DISABLED_MSG);
        }
        try{
            sendValidationEmail(optional.get());
        }catch (MessagingException ex){
            ex.printStackTrace();
            throw new SendingEmailException(ex.getMessage());
        }
    }

    @Override
    public LoginResponse loginByOTP(String token, HttpServletResponse response) {
        try{
            OTPToken savedToken = checkOTPTokenAndValidate(token);
            if(savedToken != null){
                User user = savedToken.getUser();
                UserPrincipal userDetails = new UserPrincipal(user);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                saveAndPrepareCookieForRefreshToken(userDetails, response);
                return buildLoginResponse(userDetails);
            }
            return null;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            throw new SendingEmailException(ex.getMessage());
        }
    }

    @Override
    public void loginByRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String userEmail;
        String refreshTokenFromCookie = getRefreshTokenFromCookie(request);

        log.info("refreshTokenFromCookie : {}", refreshTokenFromCookie);

        if (refreshTokenFromCookie == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token Missing");
            return;
        }
        userEmail = jwtService.extractUsername(refreshTokenFromCookie);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow(
                    () -> new EntityNotFoundException(String.format(SecurityConstant.USER_NOT_FOUND_MSG, userEmail))
            );
            var userPrincipal = new UserPrincipal(user);
            if (jwtService.isTokenValid(refreshTokenFromCookie, userPrincipal)) {

                var refreshToken = refreshTokenRepository.findByToken(refreshTokenFromCookie).orElse(null);
                //Pour s'assurer que le refresh token n'est utilisé qu'une seule fois
                if (refreshToken == null || refreshToken.isRevoked()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token Not exist || already revoked");
                    return;
                }
                refreshToken.setRevoked(true);
                refreshTokenRepository.save(refreshToken);

                //On génère un nouvel accessToken et refreshToken
                saveAndPrepareCookieForRefreshToken(userPrincipal, response);
                var loginResponse = buildLoginResponse(userPrincipal);

                new ObjectMapper().writeValue(response.getOutputStream(), loginResponse);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Refresh Token");
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Refresh Token");
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        // Supprimer le cookie de refresh token côté client
        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "None");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // Expirer le cookie immédiatement
        response.addCookie(refreshTokenCookie);
    }

    @Override
    public Long resetPassword(UpdatePasswordRequest request) {
        var user = this.userRepository.findByEmail(request.email()).orElseThrow(
                () -> new EntityNotFoundException(String.format(SecurityConstant.USER_NOT_FOUND_MSG, request.email()))
        );
        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())){
           throw new OperationNotPermittedException("OldPassword and currentPassword are not match");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        return userRepository.save(user).getId();
    }

    private OTPToken checkOTPTokenAndValidate(String token) throws MessagingException {
        OTPToken savedToken = otpTokenRepository.findByToken(token).orElseThrow(
                () -> new ActivationTokenException(SecurityConstant.INVALID_TOKEN_MSG)
        );
        if (savedToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            sendValidationEmail(savedToken.getUser());
            log.info("OTPToken was expired, new OTPToken was sent");
            return null;
        }
        savedToken.setValidatedAt(LocalDateTime.now());
        return otpTokenRepository.save(savedToken);
    }

    private void saveAndPrepareCookieForRefreshToken(UserPrincipal userPrincipal, HttpServletResponse response) {
        diseableAllUserRefreshTokens(userPrincipal.getUser());
        var refreshToken = jwtService.generateRefreshToken(userPrincipal);

        saveUserRefreshToken(userPrincipal.getUser(), refreshToken);

        //Cookies HttpOnly :pour protéger contre les attaques XSS (Cross-Site Scripting)
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setAttribute("SameSite", "None");
        refreshTokenCookie.setSecure(true); // À utiliser en production avec HTTPS
        refreshTokenCookie.setPath("/"); // Accessible depuis toutes les URLs
        refreshTokenCookie.setMaxAge((int) (jwtService.getRefreshExpiration() / 1000)); // En secondes
        response.addCookie(refreshTokenCookie);
    }

    private void diseableAllUserRefreshTokens(User user) {
        var noRevokedTokens = refreshTokenRepository.findAllNoRevokedTokensByUser(user.getId());
        if (noRevokedTokens == null || noRevokedTokens.isEmpty()){
            return;
        }
        noRevokedTokens.forEach(token -> {
            token.setRevoked(true);
        });
        refreshTokenRepository.saveAll(noRevokedTokens);
    }

    private void saveUserRefreshToken(User user, String jwtToken) {
        var token = RefreshToken.builder()
                .user(user)
                .token(jwtToken)
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
    }

    private LoginResponse buildLoginResponse(UserPrincipal userPrincipal) {
        //accessToken est renvoyé dans le corps de la réponse pour la manipulation facile côté client
        var claims = new HashMap<String, Object>();
        String accessToken = jwtService.generateToken(claims, userPrincipal);
        return new LoginResponse(accessToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var token = generateAndSaveActivationToken(user);

        emailService.sendActivateAccountEmail(
                user.getEmail(),
                user.getUserDetails().getFullName(),
                activationUrl,
                token
        );
    }

    private String generateAndSaveActivationToken(User user){
        String generatedToken = TokenGenerator.generateNumericCode(SecurityConstant.OTP_TOKEN_LENGTH);
        var otpToken = OTPToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(SecurityConstant.EXPIRATION_OTP_TOKEN))
                .user(user)
                .build();
        otpTokenRepository.save(otpToken);
        return generatedToken;
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
