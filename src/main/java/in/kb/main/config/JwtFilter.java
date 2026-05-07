package in.kb.main.config;


import in.kb.main.services.CustomUserDetailsService;
import in.kb.main.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer")){

            String token = header.substring(7);

            try{
                String username = jwtService.getEmail(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                    UserDetails ud = customUserDetailsService.loadUserByUsername(username);
                    if (ud != null){

                        UsernamePasswordAuthenticationToken UPA = new UsernamePasswordAuthenticationToken(ud, token, ud.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(UPA);

                    }
                }else {
                    System.out.println("username mis-ing in jwtToken");
                }


            }catch (ExpiredJwtException e){
                request.setAttribute("error", "Token Expired");
//                e.printStackTrace();

            }catch (Exception e){
                request.setAttribute("error", "Invalid Token");

//                e.getStackTrace();
            }




        };

        filterChain.doFilter(request, response);
    }
}
