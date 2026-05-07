package in.kb.main.controller;




import in.kb.main.dtos.LoginRequest;
import in.kb.main.dtos.RegisterRequest;
import in.kb.main.services.AuthService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //UserRegister

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        authService.register(req);

        return ResponseEntity.ok("User Registered");
    }

    //UserLogin

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest req) {

        return authService.login(req);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request) {

        String refreshToken = null;

        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if (cookie.getName()
                        .equals("refreshToken")) {

                    refreshToken = cookie.getValue();
                }
            }
        }

        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request) {

        String refreshToken = null;

        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if (cookie.getName()
                        .equals("refreshToken")) {

                    refreshToken = cookie.getValue();
                }
            }
        }

        return authService.logout(refreshToken);
    }
}