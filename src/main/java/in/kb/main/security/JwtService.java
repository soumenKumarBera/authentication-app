package in.kb.main.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String generateAccessToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()
                                + accessExpiration)
                )
                .signWith(getKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis()
                                + refreshExpiration)
                )
                .signWith(getKey(), Jwts.SIG.HS512)
                .compact();
    }

//    public boolean validate(String token) {
//
//        try {
//
//            Jwts.parserBuilder()
//                    .setSigningKey(getKey())
//                    .build()
//                    .parseClaimsJws(token);
//
//            return true;
//
//        } catch (Exception e) {
//            return false;
//        }


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

    public boolean isExpired(String token){
        Date expired =  getAllDetailsFromToken(token).getPayload().getExpiration();
        return expired.before(new Date()); //true => account is expired;
        //false => account is not expired;

    }

}





