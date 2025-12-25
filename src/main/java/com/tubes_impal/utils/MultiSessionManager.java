package com.tubes_impal.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class MultiSessionManager {

    private static final Map<String, Map<String, Object>> roleSessions = new HashMap<>();

    public static void setSessionAttribute(HttpServletRequest request, HttpServletResponse response,
            String role, String key, Object value) {
        String sessionId = getOrCreateSessionId(request, response, role);

        Map<String, Object> sessionData = roleSessions.computeIfAbsent(sessionId, k -> new HashMap<>());
        sessionData.put(key, value);
        sessionData.put("role", role);
    }

    public static Object getSessionAttribute(HttpServletRequest request, String role, String key) {
        String sessionId = getSessionIdFromCookie(request, role);
        if (sessionId == null) {
            return null;
        }

        Map<String, Object> sessionData = roleSessions.get(sessionId);
        return sessionData != null ? sessionData.get(key) : null;
    }

    public static void invalidateSession(HttpServletRequest request, HttpServletResponse response, String role) {
        String sessionId = getSessionIdFromCookie(request, role);
        if (sessionId != null) {
            roleSessions.remove(sessionId);

            // Remove cookie
            Cookie cookie = new Cookie(getSessionCookieName(role), "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    private static String getOrCreateSessionId(HttpServletRequest request, HttpServletResponse response, String role) {
        String sessionId = getSessionIdFromCookie(request, role);

        if (sessionId == null) {
            // Create new session ID
            sessionId = java.util.UUID.randomUUID().toString();

            // Set cookie
            Cookie cookie = new Cookie(getSessionCookieName(role), sessionId);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24); // 24 hours
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        return sessionId;
    }

    private static String getSessionIdFromCookie(HttpServletRequest request, String role) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String cookieName = getSessionCookieName(role);
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static String getSessionCookieName(String role) {
        return role.toUpperCase() + "_SESSION";
    }

    public static boolean isAuthenticated(HttpServletRequest request, String role) {
        Object userId = getSessionAttribute(request, role, "userId");
        Object sessionRole = getSessionAttribute(request, role, "role");
        return userId != null && role.equals(sessionRole);
    }
}
