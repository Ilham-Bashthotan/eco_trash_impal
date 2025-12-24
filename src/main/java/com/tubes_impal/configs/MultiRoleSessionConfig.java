package com.tubes_impal.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MultiRoleSessionConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new RoleBasedSessionInterceptor());
    }

    private static class RoleBasedSessionInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response, @NonNull Object handler) {
            String requestURI = request.getRequestURI();
            String sessionCookieName = determineSessionCookieName(requestURI);

            // Store the role-specific cookie name in request attribute
            request.setAttribute("SESSION_COOKIE_NAME", sessionCookieName);

            return true;
        }

        private String determineSessionCookieName(String uri) {
            if (uri.startsWith("/admin")) {
                return "ADMIN_SESSION";
            } else if (uri.startsWith("/courier")) {
                return "COURIER_SESSION";
            } else if (uri.startsWith("/seller")) {
                return "SELLER_SESSION";
            } else if (uri.contains("/auth/admin")) {
                return "ADMIN_SESSION";
            } else if (uri.contains("/auth/courier")) {
                return "COURIER_SESSION";
            } else if (uri.contains("/auth/seller")) {
                return "SELLER_SESSION";
            }
            return "JSESSIONID";
        }
    }
}
