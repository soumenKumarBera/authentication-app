package in.kb.main.services;

import in.kb.main.entitys.RefreshToken;
import in.kb.main.entitys.User;
import in.kb.main.repositorys.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    public RefreshToken create(User user) {
        String jti = UUID.randomUUID().toString();

        RefreshToken token = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .revoked(false)
                .build();

        return repo.save(token);
    }
}
