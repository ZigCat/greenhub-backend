package com.github.zigcat.greenhub.api_gateway.security.filters;

import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.exceptions.ServerException;
import com.github.zigcat.greenhub.api_gateway.security.services.AuthService;
import com.github.zigcat.greenhub.api_gateway.security.user.JwtAuthenticationToken;
import com.github.zigcat.greenhub.api_gateway.security.user.JwtUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService service;

    @Autowired
    public JwtAuthenticationFilter(AuthService service) {
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearer = request.getHeader("Authorization");
        if(bearer != null && validateToken(bearer)){
            String token = extractToken(bearer);
            try{
                JwtUserDetails userDetails = service.authorizeByToken(token);
                Authentication authentication = new JwtAuthenticationToken(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch(ServerException e){
                log.error("Server Error: "+e.getMessage());
            } finally {
                filterChain.doFilter(request, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateToken(String token){
        return token.startsWith("Bearer ");
    }

    private String extractToken(String token){
        return token.substring(7);
    }
}
