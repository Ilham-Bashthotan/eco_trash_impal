package com.tubes_impal.configs;

import com.tubes_impal.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip filter for public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/public/") ||
                path.equals("/") || path.startsWith("/css/") || path.startsWith("/js/") ||
                path.startsWith("/webjars/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get token from header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);

                // Validate token
                if (username != null && !jwtUtil.isTokenExpired(token)) {
                    // Add user info to request attributes
                    request.setAttribute("userId", jwtUtil.extractUserId(token));
                    request.setAttribute("username", username);
                    request.setAttribute("role", jwtUtil.extractRole(token));
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid token\"}");
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authorization header missing\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
