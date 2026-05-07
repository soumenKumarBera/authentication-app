package in.kb.main.services;

import in.kb.main.entitys.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.token.validity}")
    private long JWT_VALIDITY;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.issuer}")
    private String ISSUER;

    @Value("${jwt.access.til.seconds}")
    private long accessTtlSeconds;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(Map.of("role", user.getRole().name(), "type","accessToken"))
                .issuer(ISSUER)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_VALIDITY))
                .signWith(getKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(User user, String jti) {
        return Jwts.builder()
                .claim("jti", jti)
                .subject(user.getEmail())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .signWith(getKey(), Jwts.SIG.HS512)
                .compact();
    }


    public SecretKey getKey(){
        byte[] bytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(bytes);

    }

    public Jws<Claims> getAllDetailsFromToken(String token){
        JwtParserBuilder parser = Jwts.parser();
        return parser.verifyWith(getKey()).build().parseSignedClaims(token);


    }

    public boolean isAccessToken(String token){
        Claims claims = getAllDetailsFromToken(token).getPayload();

        return "access".equals(claims.get("typ"));

    }
    public boolean isRefreshToken(String token){
        Claims claims = getAllDetailsFromToken(token).getPayload();

        return "refresh".equals(claims.get("typ"));

    }

    public String getEmail(String token){

        return getAllDetailsFromToken(token).getPayload().getSubject();

    }

    public String getJti(String token){

        return getAllDetailsFromToken(token).getPayload().getId();

    }

}
