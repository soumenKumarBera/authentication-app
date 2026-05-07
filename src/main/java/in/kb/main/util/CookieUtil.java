package in.kb.main.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {

    public static ResponseCookie accessCookie(String token) {

        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();
    }

    public static ResponseCookie refreshCookie(String token) {

        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }

    public static ResponseCookie clearCookie(String name) {

        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
