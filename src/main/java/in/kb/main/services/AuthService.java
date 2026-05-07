package in.kb.main.services;

import in.kb.main.dtos.LoginRequest;
import in.kb.main.dtos.RegisterRequest;
import in.kb.main.entitys.RefreshToken;
import in.kb.main.entitys.User;
import in.kb.main.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshService;

    public User register(RegisterRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());

        return userRepo.save(user);
    }

    public Map<String, String> login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(()-> new BadCredentialsException("invalid email"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String access = jwtService.generateAccessToken(user);

        RefreshToken refresh = refreshService.create(user);
        String refreshToken = jwtService.generateRefreshToken( user, refresh.getJti());

        return Map.of(
                "access", access,
                "refresh", refreshToken
        );
    }
}
