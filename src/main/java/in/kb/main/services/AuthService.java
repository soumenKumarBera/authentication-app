package in.kb.main.services;


import in.kb.main.dtos.LoginRequest;
import in.kb.main.dtos.RegisterRequest;

import in.kb.main.entitys.RefreshToken;
import in.kb.main.entitys.User;

import in.kb.main.repositorys.RefreshTokenRepository;
import in.kb.main.repositorys.UserRepository;
import in.kb.main.security.JwtService;
import in.kb.main.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest req) {

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(
                        passwordEncoder.encode(req.getPassword())
                )
                .role(req.getRole())
                .build();

        userRepository.save(user);
    }

    public ResponseEntity<?> login(LoginRequest req) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow();

        String accessToken =
                jwtService.generateAccessToken(user.getEmail());

        String refreshToken =
                jwtService.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteByUser(user);

        RefreshToken token = RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(Instant.now().plusSeconds(604800))
                .user(user)
                .build();

        refreshTokenRepository.save(token);

        ResponseCookie accessCookie =
                CookieUtil.accessCookie(accessToken);

        ResponseCookie refreshCookie =
                CookieUtil.refreshCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE,
                        refreshCookie.toString())
                .body("Login Success");
    }

    public ResponseEntity<?> refresh(String refreshToken) {

        if (jwtService.isExpired(refreshToken)) {

            return ResponseEntity.status(401)
                    .body("Invalid Refresh Token");
        }

        var stored =
                refreshTokenRepository.findByToken(refreshToken)
                        .orElse(null);

        if (stored == null) {

            return ResponseEntity.status(401)
                    .body("Refresh Token Not Found");
        }

        String email =
                jwtService.getEmail(refreshToken);

        String newAccessToken =
                jwtService.generateAccessToken(email);

        ResponseCookie accessCookie =
                CookieUtil.accessCookie(newAccessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        accessCookie.toString())
                .body("Token Refreshed");
    }

    public ResponseEntity<?> logout(String refreshToken) {

        if (refreshToken != null) {

            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }

        ResponseCookie accessCookie =
                CookieUtil.clearCookie("accessToken");

        ResponseCookie refreshCookie =
                CookieUtil.clearCookie("refreshToken");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE,
                        refreshCookie.toString())
                .body("Logout Success");
    }
}